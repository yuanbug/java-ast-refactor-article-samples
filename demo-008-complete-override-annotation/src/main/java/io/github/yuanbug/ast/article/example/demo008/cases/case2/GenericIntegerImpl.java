package io.github.yuanbug.ast.article.example.demo008.cases.case2;

import java.util.function.Function;

/**
 * @author yuanbug
 * @since 2024-04-24
 */
public class GenericIntegerImpl extends BaseGeneric<CharSequence, Integer> {

    public <R> R map(String string, Function<String, R> mapper) {
        return super.map(string, mapper);
    }

    public String doSomething(Integer source) {
        return String.valueOf(source);
    }

    public int doSomething(int i) {
        return i;
    }

    public Integer getX() {
        return 0;
    }
}
