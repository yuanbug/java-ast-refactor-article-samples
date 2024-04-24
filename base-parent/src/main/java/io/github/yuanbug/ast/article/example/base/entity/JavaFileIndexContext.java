package io.github.yuanbug.ast.article.example.base.entity;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import io.github.yuanbug.ast.article.example.base.utils.AstUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author yuanbug
 * @since 2024-04-23
 */
public class JavaFileIndexContext {

    /**
     * 类名 -> 文件AST信息
     */
    private final Map<String, JavaFileAstInfo> classNameIndex = new HashMap<>(16);
    /**
     * Java文件 -> 文件AST信息
     */
    private final Map<File, JavaFileAstInfo> fileIndex = new HashMap<>(16);

    private final JavaParser javaParser;

    public JavaFileIndexContext(JavaParser javaParser) {
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
            classDeclaration.getFullyQualifiedName().ifPresent(qualifiedName -> classNameIndex.put(qualifiedName, info));
        }
        fileIndex.put(javaFile, info);
    }

    /**
     * 通过类名获取文件的信息
     */
    public JavaFileAstInfo getInfoByClassName(String qualifiedName) {
        return classNameIndex.get(qualifiedName);
    }

    /**
     * 通过文件获取文件信息
     */
    public JavaFileAstInfo getInfoByFile(File javaFile) {
        return fileIndex.get(javaFile);
    }

    public ClassOrInterfaceDeclaration findTypeInIndex(String qualifiedName) {
        return Optional.ofNullable(classNameIndex.get(qualifiedName))
                .map(JavaFileAstInfo::getAst)
                .flatMap(ast -> ast.findFirst(ClassOrInterfaceDeclaration.class, declaration -> declaration.getFullyQualifiedName().map(qualifiedName::equals).orElse(Boolean.FALSE)))
                .orElse(null);
    }

}
