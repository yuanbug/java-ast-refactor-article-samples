package io.github.yuanbug.ast.article.example.demo006;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import io.github.yuanbug.ast.article.example.base.utils.AstUtils;
import io.github.yuanbug.ast.article.example.demo006.cases.case1.MyServiceImpl;
import io.github.yuanbug.ast.article.example.demo006.cases.case2.GenericMethodCase;
import io.github.yuanbug.ast.article.example.demo006.cases.case3.GenericFieldImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yuanbug
 * @since 2024-05-18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Main {

    private static final Path SRC_PATH = Path.of(System.getProperty("user.dir"), "demo-006-generic", "src/main/java");

    private static final String CASE_GAP_LINE = "===================================";

    public static JavaParser buildJavaParser() {
        ParserConfiguration configuration = new ParserConfiguration();
        configuration.setSymbolResolver(new JavaSymbolSolver(new CombinedTypeSolver(
                new ReflectionTypeSolver(),
                new JavaParserTypeSolver(SRC_PATH)
        )));
        return new JavaParser(configuration);
    }

    public static File getFile(Class<?> byteCode) {
        return SRC_PATH.resolve(Path.of(byteCode.getName().replace(".", "/") + ".java")).toFile();
    }

    public static void main(String[] args) {
        JavaParser javaParser = buildJavaParser();

        parseTypeGenericParam(javaParser);
        parseMethodGenericParam(javaParser);
        parseGenericField(javaParser);
    }

    private static void parseTypeGenericParam(JavaParser javaParser) {
        var type = MyServiceImpl.class;
        Optional.ofNullable(AstUtils.parseAst(getFile(type), javaParser))
                .flatMap(ast -> ast.findFirst(ClassOrInterfaceDeclaration.class))
                .map(declaration -> declaration.getExtendedTypes(0))
                .map(ClassOrInterfaceType::resolve)
                .map(ResolvedType::asReferenceType)
                .ifPresent(parentType -> {
                    System.out.println(type.getName());
                    System.out.println("parent describe: " + parentType.describe());
                    System.out.println("parent qualifiedName: " + parentType.getQualifiedName());
                    var params = parentType.getTypeParametersMap();
                    for (var param : params) {
                        System.out.println(param.a.getName() + " : " + param.b);
                    }
                });
        System.out.println(CASE_GAP_LINE);
    }

    private static void parseMethodGenericParam(JavaParser javaParser) {
        var type = GenericMethodCase.class;
        Optional.ofNullable(AstUtils.parseAst(getFile(type), javaParser))
                .flatMap(ast -> ast.findFirst(MethodDeclaration.class, method -> "call".equals(method.getNameAsString())))
                .map(method -> method.findAll(MethodCallExpr.class))
                .stream()
                .flatMap(Collection::stream)
                .forEach(methodCall -> {
                    System.out.println(methodCall);
                    ResolvedMethodDeclaration methodDeclaration = methodCall.resolve();
                    List<ResolvedType> paramTypes = methodDeclaration.formalParameterTypes();
                    for (int i = 0; i < paramTypes.size(); i++) {
                        ResolvedType paramType = paramTypes.get(i);
                        if (paramType.isTypeVariable()) {
                            String paramDescribe = paramType.asTypeVariable().describe();
                            String realType = methodCall.getArgument(i).calculateResolvedType().describe();
                            System.out.printf("第%d个参数是泛型参数%s，实际类型为%s%n", i + 1, paramDescribe, realType);
                            continue;
                        }
                        System.out.printf("第%d个参数是固定类型%s%n", i + 1, paramType.describe());
                    }
                    System.out.println();
                });
        System.out.println(CASE_GAP_LINE);
    }

    private static void parseGenericField(JavaParser javaParser) {
        CompilationUnit ast = Objects.requireNonNull(AstUtils.parseAst(getFile(GenericFieldImpl.class), javaParser));
        NameExpr nameExpr = ast.findFirst(NameExpr.class).orElseThrow();

        ResolvedFieldDeclaration field = nameExpr.resolve().asField();
        ResolvedType type = field.getType();
        String fieldName = field.getName();
        System.out.printf("字段%s的类型是%s%n", fieldName, type);

        ResolvedTypeDeclaration declaringType = field.declaringType();
        ClassOrInterfaceDeclaration fieldDeclaringTYpe = declaringType.toAst(ClassOrInterfaceDeclaration.class).orElseThrow();
        FieldDeclaration rawFieldDeclaration = fieldDeclaringTYpe.getFieldByName(fieldName).orElseThrow();
        System.out.printf("字段%s的声明位于%s，声明类型为%s %n", fieldName, declaringType.getName(), rawFieldDeclaration.getElementType());

        System.out.println(CASE_GAP_LINE);
    }

}
