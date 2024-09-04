package io.github.yuanbug.ast.article.example.demo006.cases.introduce.case1;

/**
 * @author yuanbug
 * @since 2024-05-18
 */
public class MyServiceImpl<S extends CharSequence> extends VoidService<S, String> {

    @Override
    public Void apply(S data, String context) {
        return null;
    }

}
