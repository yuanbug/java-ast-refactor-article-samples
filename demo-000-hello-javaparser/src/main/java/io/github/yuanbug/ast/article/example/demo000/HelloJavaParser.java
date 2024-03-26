package io.github.yuanbug.ast.article.example.demo000;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

public class HelloJavaParser {

    private static final String CODE = """
            public class Main {
                private String prefix;
                public String add(int a, int b) {
                    return prefix + ": " + (a + b);
                }
                public void doNothing() {}
            }
            """;

    public static void main(String[] args) {
        // 使用默认配置创建解析器
        JavaParser javaParser = new JavaParser();

        // 这里偷个懒直接传代码字符串，JavaParser还支持传入File、Path、Reader、InputStream
        ParseResult<CompilationUnit> parseResult = javaParser.parse(CODE);

        if (!parseResult.isSuccessful()) {
            parseResult.getProblems().forEach(System.err::println);
            return;
        }

        // CompilationUnit就是整个Java文件的AST
        CompilationUnit compilationUnit = parseResult.getResult().orElseThrow(() -> new IllegalStateException("解析失败"));

        // findAll方法可以找到当前结点下所有指定类型的结点
        compilationUnit.findAll(MethodDeclaration.class)
                // 遍历
                .forEach(methodDeclaration -> {
                    // 原始方法名
                    String methodName = methodDeclaration.getNameAsString();

                    // 如果首字母是小写，改为大写
                    char firstLetter = methodName.charAt(0);
                    if (firstLetter >= 'a' && firstLetter <= 'z') {
                        methodDeclaration.setName(Character.toUpperCase(firstLetter) + methodName.substring(1));
                    }
                });

        System.out.println(compilationUnit);
    }
}
