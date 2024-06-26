package io.github.yuanbug.ast.article.example.base.utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.types.ResolvedType;
import io.github.yuanbug.ast.article.example.base.entity.AstScriptIndexContext;
import io.github.yuanbug.ast.article.example.base.entity.JavaTypeInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author yuanbug
 * @since 2024-03-31
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AstUtils {

    public static CompilationUnit parseAst(File file, JavaParser javaParser) {
        if (!file.exists() || !file.getName().endsWith(".java")) {
            return null;
        }
        try {
            ParseResult<CompilationUnit> parseResult = javaParser.parse(file);
            if (!parseResult.isSuccessful()) {
                parseResult.getProblems().forEach(System.err::println);
                throw new IllegalStateException("解析AST失败 %s".formatted(file));
            }
            return parseResult.getResult().orElse(null);
        } catch (Exception e) {
            throw new IllegalStateException("解析AST出错 %s".formatted(file), e);
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T extends Node> T findNodeInParent(Node node, @Nonnull Class<T> exceptedNodeType) {
        Optional<Node> current = node.getParentNode();
        while (current.isPresent()) {
            Node parent = current.get();
            if (exceptedNodeType.isInstance(parent)) {
                return (T) parent;
            }
            current = parent.getParentNode();
        }
        throw new IllegalArgumentException("在%s结点的父结点中不存在%s %s".formatted(node.getClass().getSimpleName(), exceptedNodeType.getSimpleName(), node));
    }

    public static ResolvedType tryResolve(ClassOrInterfaceType type) {
        try {
            return type.resolve();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Class<?> forName(String name) {
        try {
            return Class.forName(name, false, AstUtils.class.getClassLoader());
        } catch (Exception ignored) {
            return null;
        }
    }

}
