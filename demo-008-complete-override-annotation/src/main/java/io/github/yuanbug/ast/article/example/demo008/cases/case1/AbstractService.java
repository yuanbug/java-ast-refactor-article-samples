package io.github.yuanbug.ast.article.example.demo008.cases.case1;

/**
 * @author yuanbug
 * @since 2024-04-23
 */
public abstract class AbstractService implements Service {

    public void doSomething() {
        a();
        b();
    }

    protected abstract void a();

    protected abstract void b();

    public void overload(int a) {

    }

    public void overload(int a, java.lang.String b) {

    }

}
