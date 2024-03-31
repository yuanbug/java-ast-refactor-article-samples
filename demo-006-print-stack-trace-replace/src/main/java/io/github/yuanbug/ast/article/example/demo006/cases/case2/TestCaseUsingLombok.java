package io.github.yuanbug.ast.article.example.demo006.cases.case2;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author yuanbug
 * @since 2024-03-31
 */
@Slf4j
public class TestCaseUsingLombok {

    public void func() {
        try {
            doSomething();
        } catch (IOException ex) {
            log.error("IOException");
        } catch (IllegalArgumentException illegalArgumentException) {
            log.warn("warning");
            illegalArgumentException.printStackTrace();
        } catch (ClassNotFoundException ignored) {
            // 这个异常可以吃掉！
        } catch (Exception e) {
            // 这一行不应该被去掉，因为它调用的并不是异常对象的printStackTrace方法
            this.printStackTrace();
            log.error("报错了呀 {}", e.getMessage());
        }
    }

    private void doSomething() throws Exception {}

    private void printStackTrace() {}

}
