package io.github.yuanbug.ast.article.example.demo001.script;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import io.github.yuanbug.ast.article.example.base.script.BaseSimpleScript;

/**
 * @author yuanbug
 * @since 2024-03-26
 */
public class IfStatementBlockCheckScript extends BaseSimpleScript {

    @Override
    protected void doHandle(CompilationUnit ast) {
        ast.findAll(IfStmt.class).forEach(ifStmt -> {
            // 处理then部分
            ifStmt.setThenStmt(wrapWithBlock(ifStmt.getThenStmt()));
            // 处理else部分
            ifStmt.getElseStmt().ifPresent(elseStmt -> {
                // 跳过else-if，避免重复处理
                if (elseStmt instanceof IfStmt) {
                    return;
                }
                ifStmt.setElseStmt(wrapWithBlock(elseStmt));
            });
        });
    }

    /**
     * 如果语句已经是块语句，直接返回，否则使用块语句包裹
     */
    protected Statement wrapWithBlock(Statement statement) {
        if (statement instanceof BlockStmt) {
            return statement;
        }
        return new BlockStmt(NodeList.nodeList(statement));
    }

}
