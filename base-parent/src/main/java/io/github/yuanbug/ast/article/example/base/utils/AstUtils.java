package io.github.yuanbug.ast.article.example.base.utils;

import com.github.javaparser.ast.Node;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author yuanbug
 * @since 2024-03-31
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AstUtils {

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


}
