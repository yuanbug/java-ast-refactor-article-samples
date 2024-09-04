package io.github.yuanbug.ast.article.example.demo006.cases.introduce.case0;

import lombok.Data;

import java.util.Objects;

/**
 * @author yuanbug
 */
@Data
public class BinaryTreeNode<T extends Number & Comparable<T>> {

    private T data;

    private BinaryTreeNode<T> leftChild;

    private BinaryTreeNode<T> rightChild;

    public String toJsonString() {
        return """
                {"data":%s,"leftChild":%s,"rightChild":%s}""".formatted(
                Objects.toString(data),
                null == leftChild ? null : leftChild.toJsonString(),
                null == rightChild ? null : rightChild.toJsonString()
        );
    }

}
