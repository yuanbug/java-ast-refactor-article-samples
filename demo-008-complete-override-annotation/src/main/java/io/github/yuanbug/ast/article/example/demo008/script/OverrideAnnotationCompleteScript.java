package io.github.yuanbug.ast.article.example.demo008.script;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import io.github.yuanbug.ast.article.example.base.entity.JavaFileIndexContext;
import io.github.yuanbug.ast.article.example.base.script.BaseMultiFileScript;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yuanbug
 * @since 2024-04-23
 */
public class OverrideAnnotationCompleteScript extends BaseMultiFileScript {

    public OverrideAnnotationCompleteScript(File... javaFileRoots) {
        super(javaFileRoots);
    }

    @Override
    protected boolean doHandle(CompilationUnit ast, JavaFileIndexContext indexContext, JavaParser javaParser) {
        return ast.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .anyMatch(declaration -> handleClass(declaration, indexContext));
    }

    private boolean handleClass(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, JavaFileIndexContext indexContext) {
        Set<String> parents = getAllParentTypes(classOrInterfaceDeclaration, indexContext);
        boolean astChanged = false;
        List<MethodDeclaration> methods = classOrInterfaceDeclaration.getMethods();
        for (MethodDeclaration method : methods) {
            if (method.isAnnotationPresent(Override.class)) {
                continue;
            }
            if (isOverride(method, parents, indexContext)) {
                method.addMarkerAnnotation(Override.class);
                astChanged = true;
            }
        }
        return astChanged;
    }

    private boolean isOverride(MethodDeclaration method, Set<String> parents, JavaFileIndexContext indexContext) {
        for (String parent : parents) {
            ClassOrInterfaceDeclaration parentDeclaration = indexContext.findTypeInIndex(parent);
            if (null != parentDeclaration) {
                if (isSameSignatureMethodExist(method, parentDeclaration)) {
                    return true;
                }
            } else if (isSameSignatureMethodExist(method, forName(parent))) {
                return true;
            }
        }
        return false;
    }

    private boolean isSameSignatureMethodExist(MethodDeclaration method, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        String methodName = method.getNameAsString();
        String signature = toSignature(method);
        return classOrInterfaceDeclaration.getMethodsByName(methodName)
                .stream()
                .map(this::toSignature)
                .anyMatch(signature::equals);
    }

    private boolean isSameSignatureMethodExist(MethodDeclaration method, Class<?> type) {
        if (null == type) {
            return false;
        }
        String methodName = method.getNameAsString();
        String signature = toSignature(method);
        return Stream.of(type.getDeclaredMethods())
                .filter(declaredMethod -> declaredMethod.getName().equals(methodName))
                .map(this::toSignature)
                .anyMatch(signature::equals);
    }

    private String toSignature(MethodDeclaration methodDeclaration) {
        return "%s(%s)".formatted(
                methodDeclaration.getNameAsString(),
                methodDeclaration.getParameters().stream()
                        .map(Parameter::getType)
                        .map(Type::resolve)
                        .map(ResolvedType::asReferenceType)
                        .map(ResolvedReferenceType::getQualifiedName)
                        .collect(Collectors.joining(", "))
        );
    }

    private String toSignature(Method method) {
        return "%s(%s)".formatted(
                method.getName(),
                Stream.of(method.getParameterTypes())
                        .map(Class::getName)
                        .collect(Collectors.joining(", "))
        );
    }

    private Set<String> getAllParentTypes(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, JavaFileIndexContext indexContext) {
        // 应该没有哪个瓜怂会给自己的类命名为Object，所以这里判断简单类名即可
        if ("Object".equals(classOrInterfaceDeclaration.getNameAsString())) {
            return Collections.emptySet();
        }
        Set<String> parents = new HashSet<>();
        parents.add(Object.class.getName());

        NodeList<ClassOrInterfaceType> extendedTypes = classOrInterfaceDeclaration.getExtendedTypes();
        NodeList<ClassOrInterfaceType> implementedTypes = classOrInterfaceDeclaration.getImplementedTypes();
        List<ClassOrInterfaceType> parentTypes = Stream.concat(extendedTypes.stream(), implementedTypes.stream()).toList();

        for (ClassOrInterfaceType type : parentTypes) {
            // 借助符号解析器获取extends和implements的类名对应的实际类型
            ResolvedType resolvedType = tryResolve(type);
            if (null == resolvedType) {
                continue;
            }
            // 获取类限定名
            String qualifiedName = resolvedType.asReferenceType().getQualifiedName();
            parents.add(qualifiedName);
            // 从索引中取得父类型的声明，递归
            ClassOrInterfaceDeclaration superType = indexContext.findTypeInIndex(qualifiedName);
            if (null != superType) {
                parents.addAll(getAllParentTypes(superType, indexContext));
            } else {
                // 索引中取不到AST，就尝试获取字节码进行处理
                Set<Class<?>> superClasses = getAllParentTypes(forName(qualifiedName));
                parents.addAll(superClasses.stream().map(Class::getName).toList());
            }
        }
        return parents;
    }

    private Set<Class<?>> getAllParentTypes(Class<?> type) {
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

    private Class<?> forName(String name) {
        try {
            return Class.forName(name);
        } catch (Exception ignored) {
            return null;
        }
    }

    private ResolvedType tryResolve(ClassOrInterfaceType type) {
        try {
            return type.resolve();
        } catch (Exception ignored) {
            return null;
        }
    }

}
