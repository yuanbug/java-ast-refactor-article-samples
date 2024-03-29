package io.github.yuanbug.ast.article.example.demo002.cases.case3;

import lombok.Builder;
import lombok.Data;

/**
 * @author yuanbug
 * @since 2024-03-29
 */
@Data
@Builder
public class EntityWithBuilderAndAllArgsConstructor {

    private String id;

    private final String asd = "asd";

    /**
     * 已存在显式定义的全参构造器
     */
    public EntityWithBuilderAndAllArgsConstructor(String id) {
        this.id = id;
    }

}
