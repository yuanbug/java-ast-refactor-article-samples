package io.github.yuanbug.ast.article.example.demo006.cases.case4;

/**
 * @author yuanbug
 * @since 2024-03-31
 */
public class TestCaseUsingLoggerFromBaseClass extends BaseClassWithLogger {

    public void func() {
        myLog.info("真会玩呀");
        try {
            // do something
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
