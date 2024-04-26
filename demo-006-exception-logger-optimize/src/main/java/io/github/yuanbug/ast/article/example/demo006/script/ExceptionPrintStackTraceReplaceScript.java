package io.github.yuanbug.ast.article.example.demo006.script;

import com.github.javaparser.ast.CompilationUnit;
import io.github.yuanbug.ast.article.example.base.entity.AstScriptIndexContext;
import io.github.yuanbug.ast.article.example.base.script.BaseMultiFileScript;

import java.io.File;

/**
 * @author yuanbug
 * @since 2024-03-31
 */
public class ExceptionPrintStackTraceReplaceScript extends BaseMultiFileScript {

    public ExceptionPrintStackTraceReplaceScript(File... javaFileRoots) {
        super(javaFileRoots);
    }

    private static final ExceptionPrintStackTraceReplaceVisitor VISITOR = new ExceptionPrintStackTraceReplaceVisitor();

    @Override
    protected boolean doHandle(CompilationUnit ast, AstScriptIndexContext indexContext) {
        return Boolean.TRUE.equals(VISITOR.visit(ast, indexContext));
    }

}
