package io.github.yuanbug.ast.article.example.demo001.cases;

/**
 * @author yuanbug
 * @since 2024-03-26
 */
public class TestCase {

    public int func1(int a, int b, String type) {
        if (null == type || type.isBlank()) return 0;
        if ("add".equals(type)) return a + b;
        if ("max".equals(type))
            return Integer.max(a, b);

        else if ("min".equals(type))


            return Integer.min(a, b);

        if (a == b)
            System.out.println("a == b");
        else
            return a;
        return b;
    }

    public void func2(int a, int b) {
        if (a > 0)
            return;
        if (b > 0) return;
        System.out.printf("a=%s b=%s%n", a, b);
    }

    public void func3(int a, int b) {
        int c = 0;
        if (a % 2 == 0)
            if (a % 3 == 0)
                if (a % 5 == 0)
                    if (a % 7 == 0)
                        if (a % 11 == 0)
                            if (a % 13 == 0)
                                System.out.println("a=" + a);
                            else if (b % 2 == 0) c++;
                            else if (b % 3 == 0) c++;
                            else if (b % 5 == 0) c++;
                            else if (b % 7 == 0) c++;
                            else if (b % 11 == 0) c++;
                            else if (b % 13 == 0) c++;
                            else System.out.println("b=" + b);
        if (a + b == c) a -= 1;
        else if (a + b == c * 5) a -= 2;
        else a -= 3;
        System.out.printf("a=%s; b=%s; c=%s%n", a, b, c);
    }

}
