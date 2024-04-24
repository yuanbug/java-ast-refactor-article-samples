package io.github.yuanbug.ast.article.example.demo008.cases.case1;

/**
 * @author yuanbug
 * @since 2024-04-23
 */
public class Service2 extends AbstractService implements Service {

    public void doSomething() {
        b();
        a();
    }

    protected void a() {

    }

    protected void b() {

    }

    public void qaq() {

    }

    public void tat() {
        super.tat();
    }

    public void overload(int a, String b) {
        System.out.println(b);
    }

    public String toString() {
        return "Service2";
    }
}
