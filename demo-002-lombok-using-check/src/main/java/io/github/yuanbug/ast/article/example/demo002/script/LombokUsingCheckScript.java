package io.github.yuanbug.ast.article.example.demo002.script;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.*;
import io.github.yuanbug.ast.article.example.base.script.BaseSimpleScript;
import lombok.*;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author yuanbug
 * @since 2024-03-25
 */
public class LombokUsingCheckScript extends BaseSimpleScript {

    private static final String CALL_SUPER = "callSuper";

    @Override
    protected void doHandle(CompilationUnit ast) {
        fixDataWithoutCallSuper(ast);
        fixBuilderWithoutDefault(ast);
        fixBuilderWithoutConstructor(ast);
    }

    private void fixDataWithoutCallSuper(CompilationUnit ast) {
        // 获取待检测的类定义，逐个处理（一个Java文件可能定义了多个类）
        ast.findAll(ClassOrInterfaceDeclaration.class, declaration -> {
            // 跳过接口
            if (declaration.isInterface()) {
                return false;
            }
            // 跳过没有加@Data注解的类
            if (!declaration.isAnnotationPresent(Data.class)) {
                return false;
            }
            // 存在父类
            return !declaration.getExtendedTypes().isEmpty();
        }).forEach(classDeclaration -> {
            // 这里并没有检查是否已经显式定义了toString、equals等方法，因为如果已经有显式的实现，Lombok也不会覆盖它们；追求完美的话可以加上检查
            ensureAnnotationCallSuper(classDeclaration, ToString.class);
            ensureAnnotationCallSuper(classDeclaration, EqualsAndHashCode.class);
        });
    }

    private void ensureAnnotationCallSuper(ClassOrInterfaceDeclaration classDeclaration, Class<? extends Annotation> annotationType) {
        // 如果不存在注解，补充（javaparser会自动增加import语句）
        if (!classDeclaration.isAnnotationPresent(annotationType)) {
            classDeclaration.addAnnotation(annotationType);
        }
        AnnotationExpr annotationExpr = classDeclaration.getAnnotationByClass(annotationType).orElseThrow();
        // 简单的@Annotation形式
        if (annotationExpr instanceof MarkerAnnotationExpr expr) {
            annotationExpr.replace(new NormalAnnotationExpr(expr.getName(), NodeList.nodeList(new MemberValuePair(CALL_SUPER, new BooleanLiteralExpr(true)))));
            return;
        }
        // 带属性的@Annotation(key=value)形式
        if (annotationExpr instanceof NormalAnnotationExpr expr) {
            // 已经指定了callSuper，跳过
            if (expr.getPairs().stream().anyMatch(pair -> CALL_SUPER.equals(pair.getNameAsString()))) {
                return;
            }
            expr.addPair(CALL_SUPER, new BooleanLiteralExpr(true));
            return;
        }
        // 不需要处理@Annotation(value)的形式（SingleMemberAnnotationExpr）
        throw new IllegalStateException("未处理的注解表达式 " + annotationExpr);
    }

    private void fixBuilderWithoutDefault(CompilationUnit ast) {
        ast.findAll(ClassOrInterfaceDeclaration.class, declaration -> declaration.isAnnotationPresent(Builder.class)).forEach(this::fixBuilderWithoutDefault);
    }

    private void fixBuilderWithoutDefault(ClassOrInterfaceDeclaration classDeclaration) {
        classDeclaration.getFields().forEach(fieldDeclaration -> {
            // final字段跳过
            if (fieldDeclaration.isFinal()) {
                return;
            }
            // 已经存在注解的字段跳过
            if (fieldDeclaration.isAnnotationPresent(Builder.Default.class)) {
                return;
            }
            // 一行字段定义可能涉及多个变量,例如 `private String a1, a2 = "a2", a3;`，只要其中一个变量存在初始化，就需要补充@Builder.Default注解
            boolean initializerExists = fieldDeclaration.getVariables()
                    .stream()
                    .anyMatch(variableDeclarator -> variableDeclarator.getInitializer().isPresent());
            if (initializerExists) {
                // 确保Builder已经import（Builder不一定已经导入过，因为类上的注解可能用的是用完整的类限定名）
                classDeclaration.tryAddImportToParentCompilationUnit(Builder.class);
                fieldDeclaration.addAnnotation(new MarkerAnnotationExpr("Builder.Default"));
            }
        });
    }

    private void fixBuilderWithoutConstructor(CompilationUnit ast) {
        ast.findAll(ClassOrInterfaceDeclaration.class, declaration -> {
            if (!declaration.isAnnotationPresent(Builder.class)) {
                return false;
            }
            // 如果任意一个非静态字段被final修饰但未被初始化，这样的类本来就不会有无参构造器，跳过处理
            return declaration.getFields().stream().noneMatch(fieldDeclaration -> {
                if (fieldDeclaration.isStatic()) {
                    return false;
                }
                if (!fieldDeclaration.isFinal()) {
                    return false;
                }
                return fieldDeclaration.getVariables().stream().anyMatch(variableDeclarator -> variableDeclarator.getInitializer().isEmpty());
            });
        }).forEach(this::fixBuilderWithoutConstructor);
    }

    private void fixBuilderWithoutConstructor(ClassOrInterfaceDeclaration declaration) {
        ensureNoArgsConstructorExists(declaration);
        long nonFinalFieldCount = declaration.getFields().stream().filter(fieldDeclaration -> !fieldDeclaration.isFinal()).count();
        if (nonFinalFieldCount > 0) {
            ensureAllArgsConstructorExists(declaration, nonFinalFieldCount);
        }
    }

    private void ensureNoArgsConstructorExists(ClassOrInterfaceDeclaration declaration) {
        if (declaration.isAnnotationPresent(NoArgsConstructor.class)) {
            return;
        }
        if (declaration.getDefaultConstructor().isPresent()) {
            return;
        }
        declaration.addAnnotation(new MarkerAnnotationExpr("NoArgsConstructor"));
    }

    private void ensureAllArgsConstructorExists(ClassOrInterfaceDeclaration declaration, long nonFinalFieldCount) {
        if (declaration.isAnnotationPresent(AllArgsConstructor.class)) {
            return;
        }
        if (declaration.getConstructors().stream().anyMatch(constructorDeclaration -> constructorDeclaration.getParameters().size() == nonFinalFieldCount)) {
            return;
        }
        declaration.addAnnotation(new MarkerAnnotationExpr("AllArgsConstructor"));
    }

}
