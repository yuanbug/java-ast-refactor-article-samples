package io.github.yuanbug.ast.article.example.base.script;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import lombok.SneakyThrows;

import java.io.File;
import java.io.PrintWriter;

/**
 * @author yuanbug
 * @since 2024-03-26
 */
public abstract class BaseSimpleScript {

    protected static final JavaParser JAVA_PARSER = new JavaParser();

    @SneakyThrows
    public void handleJavaFile(File file, boolean writeBack) {
        CompilationUnit ast = parseAst(file);
        if (null == ast) {
            return;
        }
        doHandle(ast);
        if (!writeBack) {
            System.out.printf("%n------- %s : %n", file.getName());
            System.out.println(ast);
            System.out.printf("%n--------------------------------%n");
            return;
        }
        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.print(ast);
        }
    }

    protected CompilationUnit parseAst(File file) {
        if (!file.exists() || !file.getName().endsWith(".java")) {
            return null;
        }
        try {
            ParseResult<CompilationUnit> parseResult = JAVA_PARSER.parse(file);
            if (!parseResult.isSuccessful()) {
                parseResult.getProblems().forEach(System.err::println);
                throw new IllegalStateException("解析AST失败 %s".formatted(file));
            }
            return parseResult.getResult().orElse(null);
        } catch (Exception e) {
            throw new IllegalStateException("解析AST出错 %s".formatted(file), e);
        }
    }

    protected abstract void doHandle(CompilationUnit ast);

}
