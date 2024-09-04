package io.github.yuanbug.ast.article.example.demo006.cases.introduce.case2;

/**
 * @author yuanbug
 * @since 2024-05-18
 */
public class GenericMethodCase {

    public <T extends Number> T genericMethod(T data, String string) {
        return data;
    }

    public static <T> T staticGenericMethod(T data, String string) {
        return data;
    }

    public void call() {
        genericMethod(1, "int");
        genericMethod(1L, "long");
        staticGenericMethod("1", "String");
        staticGenericMethod(null, "null");
        staticGenericMethod(this, "this");
    }

}
