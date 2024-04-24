package io.github.yuanbug.ast.article.example.base.script;

import com.github.javaparser.ast.CompilationUnit;
import lombok.SneakyThrows;

import java.io.File;
import java.io.PrintWriter;

/**
 * @author yuanbug
 * @since 2024-04-23
 */
public interface AstScript {

    void handleJavaFile(File file, boolean writeBack, boolean printCode);

    @SneakyThrows
    default void output(File file, CompilationUnit ast, boolean writeBack, boolean printCode) {
        if (printCode) {
            System.out.printf("%n------- %s : %n", file.getName());
            System.out.println(ast);
            System.out.printf("%n--------------------------------%n");
        }
        if (writeBack) {
            try (PrintWriter printWriter = new PrintWriter(file)) {
                printWriter.print(ast);
            }
        }
    }

}
