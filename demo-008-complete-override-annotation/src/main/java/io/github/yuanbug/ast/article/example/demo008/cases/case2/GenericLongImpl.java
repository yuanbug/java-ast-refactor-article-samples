package io.github.yuanbug.ast.article.example.demo008.cases.case2;

/**
 * @author yuanbug
 * @since 2024-04-24
 */
public class GenericLongImpl extends BaseGeneric<String, Long> {

    public String doSomething(Long source) {
        return String.valueOf(source);
    }

    public Long getX() {
        return 0L;
    }
}
