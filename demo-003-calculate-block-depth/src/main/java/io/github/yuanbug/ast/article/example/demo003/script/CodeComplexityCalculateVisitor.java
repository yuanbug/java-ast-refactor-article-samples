package io.github.yuanbug.ast.article.example.demo003.script;

import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import java.util.Comparator;
import java.util.Objects;

/**
 * @author yuanbug
 * @since 2024-04-04
 */
public class CodeComplexityCalculateVisitor extends GenericVisitorAdapter<Integer, Integer> {

    @Override
    public Integer visit(IfStmt ifStmt, Integer arg) {
        int currentComplexity = arg + 1;
        int thenComplexity = Objects.requireNonNullElse(ifStmt.getThenStmt().accept(this, currentComplexity), currentComplexity);
        int elseComplexity = ifStmt.getElseStmt()
                // else-if和if平级，不加一
                .map(elseStmt -> elseStmt.accept(this, elseStmt instanceof IfStmt ? arg : arg + 1))
                .orElse(currentComplexity);
        return Math.max(thenComplexity, elseComplexity);
    }

    @Override
    public Integer visit(BlockStmt blockStmt, Integer arg) {
        return blockStmt.getStatements()
                .stream()
                .map(statement -> statement.accept(this, arg))
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

    @Override
    public Integer visit(ForStmt forStmt, Integer arg) {
        return Objects.requireNonNullElse(forStmt.getBody().accept(this, arg + 1), arg + 1);
    }

    @Override
    public Integer visit(SwitchStmt switchStmt, Integer arg) {
        int currentComplexity = arg + 1;
        return switchStmt.getEntries().stream()
                .map(entry -> entry.accept(this, currentComplexity))
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(currentComplexity);
    }

    @Override
    public Integer visit(WhileStmt whileStmt, Integer arg) {
        return Objects.requireNonNullElse(whileStmt.getBody().accept(this, arg + 1), arg + 1);
    }

    @Override
    public Integer visit(DoStmt doStmt, Integer arg) {
        return Objects.requireNonNullElse(doStmt.getBody().accept(this, arg + 1), arg + 1);
    }

    @Override
    public Integer visit(TryStmt tryStmt, Integer arg) {
        int currentComplexity = arg + 1;
        Integer blockComplexity = tryStmt.getTryBlock().accept(this, currentComplexity);
        Integer catchComplexity = tryStmt.getCatchClauses().stream()
                .map(catchClause -> catchClause.accept(this, currentComplexity))
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(currentComplexity);
        return Math.max(blockComplexity, catchComplexity);
    }

    @Override
    public Integer visit(LambdaExpr lambdaExpr, Integer arg) {
        Statement body = lambdaExpr.getBody();
        if (body instanceof BlockStmt blockStmt) {
            return blockStmt.accept(this, arg + 1);
        }
        return arg;
    }

}
