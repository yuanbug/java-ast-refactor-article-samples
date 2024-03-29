package io.github.yuanbug.ast.article.example.demo002.cases.case2;

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

    private final String group = "default group";

    @Builder.Default
    private String type = "default type";

    private String a1, a2 = "a2", a3;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InnerClass {

        private String name = "inner";

    }

}
