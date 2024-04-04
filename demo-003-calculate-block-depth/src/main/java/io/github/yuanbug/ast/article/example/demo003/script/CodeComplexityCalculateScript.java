package io.github.yuanbug.ast.article.example.demo003.script;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import io.github.yuanbug.ast.article.example.base.script.BaseSimpleScript;
import org.apache.commons.lang3.tuple.Pair;

import java.util.stream.Collectors;

/**
 * @author yuanbug
 * @since 2024-04-04
 */
public class CodeComplexityCalculateScript extends BaseSimpleScript {

    private static final CodeComplexityCalculateVisitor VISITOR = new CodeComplexityCalculateVisitor();

    @Override
    protected void doHandle(CompilationUnit ast) {
        ast.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .flatMap(typeDeclaration -> typeDeclaration.findAll(MethodDeclaration.class).stream()
                        .map(methodDeclaration -> Pair.of(typeDeclaration, methodDeclaration)))
                .forEach(kv -> {
                    ClassOrInterfaceDeclaration type = kv.getKey();
                    MethodDeclaration methodDeclaration = kv.getValue();
                    Integer complexity = VISITOR.visit(methodDeclaration, 0);
                    System.out.printf(
                            "%s#%s(%s) : %s%n",
                            type.getNameAsString(),
                            methodDeclaration.getNameAsString(),
                            methodDeclaration.getParameters().stream().map(NodeWithType::getTypeAsString).collect(Collectors.joining(", ")),
                            complexity
                    );
                });
    }

}
