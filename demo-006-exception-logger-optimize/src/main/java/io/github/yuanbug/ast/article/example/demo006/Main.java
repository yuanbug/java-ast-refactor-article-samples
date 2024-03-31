package io.github.yuanbug.ast.article.example.demo006;

import io.github.yuanbug.ast.article.example.base.utils.DemoRunningUtils;
import io.github.yuanbug.ast.article.example.demo006.script.ExceptionPrintStackTraceReplaceScript;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author yuanbug
 * @since 2024-03-25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Main {

    public static void main(String[] args) {
        DemoRunningUtils.executeToAllCases(new ExceptionPrintStackTraceReplaceScript(), false);
    }

}
