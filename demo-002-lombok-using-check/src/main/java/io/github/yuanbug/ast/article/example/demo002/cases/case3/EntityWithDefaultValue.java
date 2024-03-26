package io.github.yuanbug.ast.article.example.demo002.cases.case3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yuanbug
 * @since 2024-03-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityWithDefaultValue {

    private String id;

    private String name = "default name";

    @Builder.Default
    private String type = "default type";

}
