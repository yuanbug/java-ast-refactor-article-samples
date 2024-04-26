package io.github.yuanbug.ast.article.example.demo008.script;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import io.github.yuanbug.ast.article.example.base.entity.AstScriptIndexContext;
import io.github.yuanbug.ast.article.example.base.entity.JavaTypeInfo;
import io.github.yuanbug.ast.article.example.base.script.BaseMultiFileScript;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
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
    protected boolean doHandle(CompilationUnit ast, AstScriptIndexContext indexContext) {
        return ast.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .anyMatch(declaration -> handleClass(declaration, indexContext));
    }

    private boolean handleClass(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, AstScriptIndexContext indexContext) {
        boolean astChanged = false;
        List<MethodDeclaration> methods = classOrInterfaceDeclaration.getMethods();
        for (MethodDeclaration method : methods) {
            if (method.isAnnotationPresent(Override.class)) {
                continue;
            }
            if (isOverride(method, indexContext.getAllParentTypes(classOrInterfaceDeclaration).values())) {
                method.addMarkerAnnotation(Override.class);
                astChanged = true;
            }
        }
        return astChanged;
    }

    private boolean isOverride(MethodDeclaration method, Collection<JavaTypeInfo> parents) {
        for (JavaTypeInfo parent : parents) {
            ClassOrInterfaceDeclaration parentDeclaration = parent.getAstDeclaration();
            if (null != parentDeclaration) {
                if (isSameSignatureMethodExist(method, parentDeclaration)) {
                    return true;
                }
            } else if (isSameSignatureMethodExist(method, parent.getByteCode())) {
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

}
