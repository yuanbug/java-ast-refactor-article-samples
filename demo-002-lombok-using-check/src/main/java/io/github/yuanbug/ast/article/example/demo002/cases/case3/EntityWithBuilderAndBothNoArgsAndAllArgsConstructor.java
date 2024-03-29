package io.github.yuanbug.ast.article.example.demo002.cases.case3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author yuanbug
 * @since 2024-03-29
 */
@Data
@Builder
@AllArgsConstructor
public class EntityWithBuilderAndBothNoArgsAndAllArgsConstructor {

    private String id;

    /**
     * 已存在显式定义的无参构造器
     */
    public EntityWithBuilderAndBothNoArgsAndAllArgsConstructor() {}

}
