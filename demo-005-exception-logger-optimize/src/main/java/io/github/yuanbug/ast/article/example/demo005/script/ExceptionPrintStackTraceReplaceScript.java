package io.github.yuanbug.ast.article.example.demo005.script;

import com.github.javaparser.ast.CompilationUnit;
import io.github.yuanbug.ast.article.example.base.script.BaseSimpleScript;

/**
 * @author yuanbug
 * @since 2024-03-31
 */
public class ExceptionPrintStackTraceReplaceScript extends BaseSimpleScript {

    private static final ExceptionPrintStackTraceReplaceVisitor VISITOR = new ExceptionPrintStackTraceReplaceVisitor();

    @Override
    protected void doHandle(CompilationUnit ast) {
        VISITOR.visit(ast, null);
    }

}
