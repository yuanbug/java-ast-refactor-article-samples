package io.github.yuanbug.ast.article.example.demo008;

import io.github.yuanbug.ast.article.example.demo008.script.OverrideAnnotationCompleteScript;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static io.github.yuanbug.ast.article.example.base.utils.DemoRunningUtils.*;

/**
 * @author yuanbug
 * @since 2024-04-23
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Main {

    public static void main(String[] args) {
        executeToAllCases(new OverrideAnnotationCompleteScript(getSrcDir()), false);
    }

}
