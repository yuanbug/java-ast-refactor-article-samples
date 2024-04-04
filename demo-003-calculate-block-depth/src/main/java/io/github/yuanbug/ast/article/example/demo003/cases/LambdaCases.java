package io.github.yuanbug.ast.article.example.demo003.cases;

import java.util.stream.IntStream;

/**
 * @author yuanbug
 * @since 2024-04-04
 */
public class LambdaCases {

    public int lambda2(int n, int a, int b) {
        return IntStream.range(a, b)
                .filter(i -> {
                    if (i % 2 == 0) {
                        return false;
                    }
                    return i % n == 9;
                })
                .max()
                .orElse(0);
    }

    public void lambda3(int a, int b) {
        IntStream.range(a, b)
                .filter(i -> i % 2 == 0)
                .map(n -> {
                    for (int i = 0; i < 100; i++) {
                        n += Math.max(a, i * b);
                        n -= Math.min(b, i * a);
                        if (n % 42 == 0) {
                            n *= n;
                        }
                    }
                    return n;
                })
                .forEach(System.out::println);
    }

}
