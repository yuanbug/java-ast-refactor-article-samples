package io.github.yuanbug.ast.article.example.demo008.cases.case2;

import java.util.function.Function;

/**
 * @author yuanbug
 * @since 2024-04-24
 */
public abstract class BaseGeneric<T, S extends Number> implements GenericInterface<S> {

    public <R> R map(String string, Function<String, R> mapper) {
        return mapper.apply(string);
    }

    public abstract T doSomething(S source);

}
