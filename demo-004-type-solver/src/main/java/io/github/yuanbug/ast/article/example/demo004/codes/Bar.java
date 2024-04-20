package io.github.yuanbug.ast.article.example.demo004.codes;

import java.util.stream.IntStream;

/**
 * @author yuanbug
 * @since 2024-04-20
 */
public class Bar {

    private String string;

    private Foo foo;

    public double doSomething() {
        long a = System.currentTimeMillis();
        if (null != foo) {
            return foo.size;
        }
        if (null != string) {
            return string.length();
        }
        var b = Math.random();
        var c = Integer.MAX_VALUE;
        return a + b + c;
    }

    public int addStringLength(int i) {
        if (null == string) {
            return i;
        }
        return i + string.length();
    }

    public void functionWithLambda() {
        IntStream.range(0, 10)
                .map(i -> i + 1)
                .map(this::addStringLength)
                .forEach(System.out::println);
    }

    public void usingOverLoad() {
        overload(1);
        overload(1, 2);
        overload(0L);
    }

    public void overload(int a) {}

    public void overload(int a, int b) {}

    public void overload(long a) {}

}
