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
        // Node#findAll(Class<?> nodeType)方法可以获取当前结点下所有指定结点，包括嵌套的
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
    private Statement wrapWithBlock(Statement statement) {
        if (statement instanceof BlockStmt) {
            return statement;
        }
        // 要构造一个结点，通常直接使用对应类型的构造方法即可，可以点进对应类型的源码查看构造方法重载，从中选择一个适合的
        return new BlockStmt(NodeList.nodeList(statement));
    }

}
