package io.github.yuanbug.ast.article.example.demo003;

import io.github.yuanbug.ast.article.example.base.utils.DemoRunningUtils;
import io.github.yuanbug.ast.article.example.demo003.script.CodeComplexityCalculateScript;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author yuanbug
 * @since 2024-04-04
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Main {

    public static void main(String[] args) {
        DemoRunningUtils.executeToAllCases(new CodeComplexityCalculateScript(), false, false);
    }

}
