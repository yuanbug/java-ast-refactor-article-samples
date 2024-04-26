package io.github.yuanbug.ast.article.example.base.script;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import io.github.yuanbug.ast.article.example.base.entity.JavaFileAstInfo;
import io.github.yuanbug.ast.article.example.base.entity.AstScriptIndexContext;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

/**
 * @author yuanbug
 * @since 2024-04-23
 */
public abstract class BaseMultiFileScript implements AstScript {

    protected final JavaParser javaParser;
    protected final AstScriptIndexContext indexContext;

    protected BaseMultiFileScript(File... javaFileRoots) {
        if (null == javaFileRoots || javaFileRoots.length == 0) {
            throw new IllegalArgumentException("未指定源码根目录");
        }
        // 创建JavaParser
        this.javaParser = buildJavaParser(javaFileRoots);
        // 构建索引上下文
        this.indexContext = buildIndexContext(this.javaParser, javaFileRoots);
    }

    private static JavaParser buildJavaParser(File... javaFileRoots) {
        ParserConfiguration configuration = new ParserConfiguration();
        CombinedTypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        for (File javaFileRoot : javaFileRoots) {
            typeSolver.add(new JavaParserTypeSolver(javaFileRoot));
        }
        configuration.setSymbolResolver(new JavaSymbolSolver(typeSolver));
        configuration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        return new JavaParser(configuration);
    }

    /**
     * @param javaFileRoots 各模块的源码根目录
     * @return 索引上下文
     */
    private static AstScriptIndexContext buildIndexContext(JavaParser javaParser, File... javaFileRoots) {
        AstScriptIndexContext context = new AstScriptIndexContext(javaParser);
        Queue<File> queue = new LinkedList<>(Arrays.asList(javaFileRoots));
        while (!queue.isEmpty()) {
            File current = queue.poll();
            if (current.isDirectory()) {
                Optional.ofNullable(current.listFiles())
                        .map(Arrays::asList)
                        .ifPresent(queue::addAll);
                continue;
            }
            if (current.getName().endsWith(".java")) {
                context.addFileToIndex(current);
            }
        }
        return context;
    }

    @Override
    public void handleJavaFile(File file, boolean writeBack, boolean printCode) {
        JavaFileAstInfo info = indexContext.getInfoByFile(file);
        if (null == info) {
            return;
        }
        CompilationUnit ast = info.getAst();
        LexicalPreservingPrinter.setup(ast);
        if (!doHandle(ast, indexContext)) {
            return;
        }
        info.setAstChanged(true);
        output(file, ast, writeBack, printCode);
    }

    /**
     * @return AST是否发生变更
     */
    protected abstract boolean doHandle(CompilationUnit ast, AstScriptIndexContext indexContext);

}
