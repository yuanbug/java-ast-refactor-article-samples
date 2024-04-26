package io.github.yuanbug.ast.article.example.base.entity;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import io.github.yuanbug.ast.article.example.base.utils.AstUtils;
import lombok.Getter;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author yuanbug
 * @since 2024-04-23
 */
public class AstScriptIndexContext {

    /**
     * 类限定名 -> 文件AST信息
     */
    private final Map<String, JavaFileAstInfo> classNameToFileInfo = new HashMap<>(16);
    /**
     * Java文件 -> 所在文件AST信息
     */
    private final Map<File, JavaFileAstInfo> fileToFileInfo = new HashMap<>(16);

    @Getter
    private final JavaParser javaParser;

    public AstScriptIndexContext(JavaParser javaParser) {
        this.javaParser = javaParser;
    }

    /**
     * 把文件添加到索引
     */
    public void addFileToIndex(File javaFile) {
        CompilationUnit ast = AstUtils.parseAst(javaFile, javaParser);
        if (null == ast) {
            return;
        }
        JavaFileAstInfo info = JavaFileAstInfo.builder()
                .file(javaFile)
                .ast(ast)
                .build();
        List<ClassOrInterfaceDeclaration> classDeclarations = ast.findAll(ClassOrInterfaceDeclaration.class);
        if (classDeclarations.isEmpty()) {
            return;
        }
        for (ClassOrInterfaceDeclaration classDeclaration : classDeclarations) {
            classDeclaration.getFullyQualifiedName().ifPresent(qualifiedName -> classNameToFileInfo.put(qualifiedName, info));
        }
        fileToFileInfo.put(javaFile, info);
    }

    /**
     * 通过类名获取文件的信息
     */
    public JavaFileAstInfo getInfoByClassName(String qualifiedName) {
        return classNameToFileInfo.get(qualifiedName);
    }

    /**
     * 通过文件获取文件信息
     */
    public JavaFileAstInfo getInfoByFile(File javaFile) {
        return fileToFileInfo.get(javaFile);
    }

    public ClassOrInterfaceDeclaration findTypeInIndex(String qualifiedName) {
        return Optional.ofNullable(classNameToFileInfo.get(qualifiedName))
                .map(JavaFileAstInfo::getAst)
                .flatMap(ast -> ast.findFirst(ClassOrInterfaceDeclaration.class, declaration -> declaration.getFullyQualifiedName().map(qualifiedName::equals).orElse(Boolean.FALSE)))
                .orElse(null);
    }

    public Map<String, JavaTypeInfo> getAllParentTypes(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        // 应该没有哪个瓜怂会给自己的类命名为Object，所以这里判断简单类名即可
        if ("Object".equals(classOrInterfaceDeclaration.getNameAsString())) {
            return Collections.emptyMap();
        }
        Map<String, JavaTypeInfo> result = new HashMap<>(8);
        result.put(JavaTypeInfo.JAVA_LANG_OBJECT.getName(), JavaTypeInfo.JAVA_LANG_OBJECT);
        var extendedTypes = classOrInterfaceDeclaration.getExtendedTypes();
        var implementedTypes = classOrInterfaceDeclaration.getImplementedTypes();
        List<ClassOrInterfaceType> parentTypes = Stream.concat(extendedTypes.stream(), implementedTypes.stream()).toList();
        for (ClassOrInterfaceType parentType : parentTypes) {
            result.putAll(getAllParentTypes(parentType));
        }
        return result;
    }

    public Map<String, JavaTypeInfo> getAllParentTypes(ClassOrInterfaceType type) {
        // 借助符号解析器获取对应的实际类型
        ResolvedType resolvedType = AstUtils.tryResolve(type);
        if (null == resolvedType) {
            return Collections.emptyMap();
        }
        Map<String, JavaTypeInfo> result = new HashMap<>(8);
        ResolvedReferenceType referenceType = resolvedType.asReferenceType();
        // 获取名称（可能带泛型参数）
        String name = referenceType.describe();
        // 获取类限定名
        String qualifiedName = referenceType.getQualifiedName();
        // 从索引中取得父类型的声明，递归
        ClassOrInterfaceDeclaration superTypeDeclaration = this.findTypeInIndex(qualifiedName);
        Class<?> byteCode = AstUtils.forName(qualifiedName);
        if (null != superTypeDeclaration) {
            JavaTypeInfo self = JavaTypeInfo.builder()
                    .name(name)
                    .classQualifiedName(qualifiedName)
                    .sourceFile(classNameToFileInfo.get(qualifiedName))
                    .astDeclaration(superTypeDeclaration)
                    .byteCode(byteCode)
                    .build();
            result.put(self.getName(), self);
            result.putAll(getAllParentTypes(superTypeDeclaration));
            return result;
        }
        // 索引中取不到AST，就尝试通过字节码进行处理
        if (null == byteCode) {
            return Collections.emptyMap();
        }
        result.put(name, JavaTypeInfo.builder()
                .name(name)
                .classQualifiedName(qualifiedName)
                .byteCode(byteCode)
                .build());
        getAllParentTypes(byteCode).stream()
                .map(JavaTypeInfo::byByteCode)
                .forEach(typeInfo -> result.put(typeInfo.getName(), typeInfo));
        return result;
    }

    public static Set<Class<?>> getAllParentTypes(Class<?> type) {
        if (null == type) {
            return Collections.emptySet();
        }
        Set<Class<?>> result = new HashSet<>(16);
        Optional.ofNullable(type.getSuperclass()).ifPresent(superClass -> {
            result.add(superClass);
            result.addAll(getAllParentTypes(superClass));
        });
        Class<?>[] interfaces = type.getInterfaces();
        for (Class<?> impl : interfaces) {
            result.add(impl);
            result.addAll(getAllParentTypes(impl));
        }
        return result;
    }

}
