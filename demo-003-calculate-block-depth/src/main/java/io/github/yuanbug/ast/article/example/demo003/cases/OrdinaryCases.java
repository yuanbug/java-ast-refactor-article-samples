package io.github.yuanbug.ast.article.example.demo003.cases;

/**
 * @author yuanbug
 * @since 2024-04-04
 */
public class OrdinaryCases {

    public int if3(int a, int b) {
        if (a < 0) {
            if (b < 0) {
                return 0;
            }
            if (b % 2 == 0) {
                a = b;
            } else {
                a = b + 2;
                if (a + b == 42) {
                    a = 1;
                    b = 2;
                }
            }
        }
        int c = a + b;
        for (int i = 0; i < b; i++) {
            c += a * Math.max(b, c % a);
            if (c % 42 == 0) {
                c = 0;
            }
        }
        return c;
    }

    public int for4(int n, int b) {
        if (n < 0 || b < 0) {
            if (n == b) {
                return 0;
            }
            n = -b;
            b = -n;
        }
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < b; j++) {
                if (i == j) {
                    continue;
                }
                int x = i * j;
                if (x > 42 * 42) {
                    return x;
                }
                if (x % 7 == 0) {
                    x -= 6;
                    if (x % 42 == 0) {
                        x += b + n;
                    }
                    if (x % 42 == 0) {
                        return x;
                    }
                }
            }
        }
        return n * (int) Math.pow(b, 2);
    }

    public void switch1(int n) {
        switch (n) {}
    }

    public void switch3(int n, int m) {
        switch (n) {
            case 0:
                return;
            case 1:
            case 2:
            case 3:
                if (m % 2 == 0) {
                    if (m % 3 == 0) {
                        System.out.println("asd");
                    }
                    System.out.println(m);
                } else if (m == n) {
                    System.out.println("qwe");
                }
            case 4:
            case 5: {
                int x = n + m;
                System.out.println("x=" + x);
            }
            case 6: {
                int x = n + m;
                int y = n - m;
                System.out.printf("x=%s ; y=%s%n", x, y);
            }
            case 7:
                System.out.println("m=" + m);
            default:
                System.out.println("fuck " + n + " " + m);
        }
    }

    public int while2(int n) {
        while (n > 0) {
            if (n == 42) {
                return n;
            }
            n--;
        }
        return n;
    }

    public int doWhile2(int n) {
        do {
            if (n == 42) {
                return n;
            }
            n--;
        } while (n > 0);
        return n;
    }

    public int try2(int n) {
        try {
            if (while2(n) > 0) {
                return n;
            }
            return 0;
        } catch (Exception e) {
            if (n < 2) {
                return -1;
            }
        }
        return n;
    }

    public int try3(int n) {
        try {
            if (while2(n) > 0) {
                return n;
            }
            return 0;
        } catch (Exception e) {
            if (n < 2) {
                return -1;
            } else if (n > 42) {
                if (doWhile2(n) > 0) {
                    return for4(n, 666);
                }
            }
        }
        return n;
    }

    public int try4(int n) {
        try {
            if (while2(n) > 0) {
                return n;
            }
            try {
                int i = if3(n, n);
                if (i > n) {
                    return i;
                } else if (i == n) {
                    if (n > 999) {
                        return if3(999, n);
                    }
                    return n * n;
                }
            } catch (Exception e) {
                return -n;
            }
            return 0;
        } catch (Exception e) {
            if (n < 2) {
                return -1;
            } else if (n > 42) {
                if (doWhile2(n) > 0) {
                    return for4(n, 666);
                }
            }
        }
        return n;
    }

}
