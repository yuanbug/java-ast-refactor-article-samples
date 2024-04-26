package io.github.yuanbug.ast.article.example.base.entity;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import lombok.Builder;
import lombok.Getter;

import javax.annotation.Nullable;

/**
 * @author yuanbug
 * @since 2024-04-26
 */
@Getter
public class JavaTypeInfo {

    public static final JavaTypeInfo JAVA_LANG_OBJECT = byByteCode(Object.class);

    private final String name;

    private final String classQualifiedName;

    @Nullable
    private final JavaFileAstInfo sourceFile;

    @Nullable
    private final ClassOrInterfaceDeclaration astDeclaration;

    private final Class<?> byteCode;

    @Builder
    public JavaTypeInfo(String name,
                        String classQualifiedName,
                        @Nullable JavaFileAstInfo sourceFile,
                        @Nullable ClassOrInterfaceDeclaration astDeclaration,
                        Class<?> byteCode) {
        this.name = name;
        this.classQualifiedName = classQualifiedName;
        this.sourceFile = sourceFile;
        this.astDeclaration = astDeclaration;
        this.byteCode = byteCode;
    }

    public static JavaTypeInfo byByteCode(Class<?> byteCode) {
        return JavaTypeInfo.builder()
                .name(byteCode.getName())
                .classQualifiedName(byteCode.getName())
                .byteCode(byteCode)
                .build();
    }

}
