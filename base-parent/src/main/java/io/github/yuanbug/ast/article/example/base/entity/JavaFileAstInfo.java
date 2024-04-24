package io.github.yuanbug.ast.article.example.base.entity;

import com.github.javaparser.ast.CompilationUnit;
import lombok.Builder;
import lombok.Data;

import java.io.File;

/**
 * @author yuanbug
 * @since 2024-04-23
 */
@Data
public class JavaFileAstInfo {

    /**
     * 对应的Java文件
     */
    private File file;

    /**
     * 代码对应的抽象语法树
     */
    private CompilationUnit ast;

    /**
     * 抽象语法树是否已发生变更
     */
    private boolean astChanged;

    @Builder
    public JavaFileAstInfo(File file, CompilationUnit ast) {
        this.file = file;
        this.ast = ast;
        this.astChanged = false;
    }

}
