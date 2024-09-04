package io.github.yuanbug.ast.article.example.demo006.cases.get.case1;

/**
 * @author yuanbug
 * @since 2024-05-18
 */
public interface Service<T, C, R> {

    R apply(T data, C context);

}
