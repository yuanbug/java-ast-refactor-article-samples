package io.github.yuanbug.ast.article.example.demo007.cases.case3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuanbug
 * @since 2024-03-31
 */
public class TestCaseUsingLoggerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseUsingLoggerFactory.class);

    public void func1() {
        LOGGER.info("进入方法");
        try {
            // do something
        } catch (IllegalStateException | IllegalArgumentException e) {
            LOGGER.info("这种小问题不用打error的啦");
        } catch (Exception fuckingStupidName) {
            LOGGER.error("出错啦，但是我把堆栈吃了，打我啊！");
        }
    }

    public void func2() {
        try {
            // do something
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("出错啦 {} {}", e.getMessage(), e.getStackTrace());
        }
    }

}
