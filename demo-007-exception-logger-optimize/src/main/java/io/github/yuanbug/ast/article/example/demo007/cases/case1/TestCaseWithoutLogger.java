package io.github.yuanbug.ast.article.example.demo007.cases.case1;

/**
 * @author yuanbug
 * @since 2024-03-31
 */
public class TestCaseWithoutLogger {

    public void func1() {
        int i = 1;
        for (int j = 0; j < 10; j++) {
            if (j % 2 == 0) {
                try {
                    throwException();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                i++;
            }
        }
        System.out.println(i);
    }

    public void func2() {
        try {
            func1();
        } catch (Exception e) {

        }
    }

    public void func3() {
        try {
            func1();
        } catch (Exception ignored) {}
        func2();
    }

    private void throwException() throws Exception {
        throw new Exception("我是异常");
    }

}
