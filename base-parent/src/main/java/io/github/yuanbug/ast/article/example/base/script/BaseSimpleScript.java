package io.github.yuanbug.ast.article.example.base.script;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import io.github.yuanbug.ast.article.example.base.utils.AstUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.io.PrintWriter;

/**
 * @author yuanbug
 * @since 2024-03-26
 */
public abstract class BaseSimpleScript implements AstScript {

    protected static final JavaParser JAVA_PARSER = new JavaParser();

    @Override
    @SneakyThrows
    public void handleJavaFile(File file, boolean writeBack, boolean printCode) {
        CompilationUnit ast = AstUtils.parseAst(file, JAVA_PARSER);
        if (null == ast) {
            return;
        }
        // 尽量避免破坏原始代码格式
        LexicalPreservingPrinter.setup(ast);
        doHandle(ast);
        output(file, ast, writeBack, printCode);
    }

    protected abstract void doHandle(CompilationUnit ast);

}
