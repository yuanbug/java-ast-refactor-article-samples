package io.github.yuanbug.ast.article.example.demo004;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.declarations.ResolvedMethodLikeDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.nio.file.Path;

/**
 * @author yuanbug
 * @since 2024-04-20
 */
public class Main {

    private static final Path SRC_PATH = Path.of(System.getProperty("user.dir"), "demo-004-type-solver", "src/main/java");

    public static JavaParser buildJavaParser() {
        ParserConfiguration configuration = new ParserConfiguration();
        configuration.setSymbolResolver(new JavaSymbolSolver(new CombinedTypeSolver(
                new ReflectionTypeSolver(),
                new JavaParserTypeSolver(SRC_PATH)
        )));
        return new JavaParser(configuration);
    }

    public static void main(String[] args) throws Exception {
        JavaParser javaParser = buildJavaParser();

        File fileOfBar = SRC_PATH.resolve(Path.of(Main.class.getPackageName().replace(".", "/"), "codes", "Bar.java")).toFile();
        CompilationUnit compilationUnit = javaParser.parse(fileOfBar).getResult().orElseThrow();

        resolveVariable(compilationUnit);
        System.out.println("============================================");
        resolveMethodReference(compilationUnit);
        System.out.println("============================================");
        resolveOverloadMethod(compilationUnit);
    }

    private static void resolveVariable(CompilationUnit compilationUnit) {
        MethodDeclaration methodDeclaration = compilationUnit.findFirst(MethodDeclaration.class).orElseThrow();
        methodDeclaration.findAll(NameExpr.class).forEach(nameExpr -> {
            Node parent = nameExpr.getParentNode().orElse(null);
            // 过滤`System.currentTimeMillis()`等静态调用中的类名
            if (parent instanceof MethodCallExpr || parent instanceof FieldAccessExpr) {
                return;
            }
            System.out.printf("%s | %s %n", nameExpr, parent);
            try {
                System.out.println(nameExpr.resolve());
                System.out.println(nameExpr.calculateResolvedType());
            } catch (Exception e) {
                System.out.println("解析失败");
            }
            System.out.println("-------------");
        });
    }

    private static void resolveMethodReference(CompilationUnit compilationUnit) {
        MethodDeclaration methodDeclaration = compilationUnit.findFirst(MethodDeclaration.class, method -> "functionWithLambda".equals(method.getNameAsString())).orElseThrow();
        methodDeclaration.findAll(MethodReferenceExpr.class).forEach(methodReferenceExpr -> {
            System.out.println(methodReferenceExpr);
            System.out.println(methodReferenceExpr.resolve());
            System.out.println(methodReferenceExpr.calculateResolvedType());
            System.out.println("-------------");
        });
    }

    private static void resolveOverloadMethod(CompilationUnit compilationUnit) {
        MethodDeclaration methodDeclaration = compilationUnit.findFirst(MethodDeclaration.class, method -> "usingOverLoad".equals(method.getNameAsString())).orElseThrow();
        methodDeclaration.findAll(MethodCallExpr.class).stream()
                .map(MethodCallExpr::resolve)
                .map(ResolvedMethodLikeDeclaration::getQualifiedSignature)
                .forEach(System.out::println);
    }

}
