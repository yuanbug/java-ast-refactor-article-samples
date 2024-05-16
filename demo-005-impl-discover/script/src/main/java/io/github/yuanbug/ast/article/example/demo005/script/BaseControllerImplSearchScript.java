package io.github.yuanbug.ast.article.example.demo005.script;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import io.github.yuanbug.ast.article.example.base.entity.AstScriptIndexContext;
import io.github.yuanbug.ast.article.example.base.entity.JavaFileAstInfo;
import io.github.yuanbug.ast.article.example.base.script.BaseMultiFileScript;
import io.github.yuanbug.ast.article.example.demo005.cases.module.framework.BaseController;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

/**
 * @author yuanbug
 * @since 2024-05-10
 */
public class BaseControllerImplSearchScript extends BaseMultiFileScript {

    public BaseControllerImplSearchScript(File... javaFileRoots) {
        super(javaFileRoots);
    }

    @Override
    protected boolean doHandle(CompilationUnit ast, AstScriptIndexContext indexContext) {
        ast.findAll(ClassOrInterfaceDeclaration.class)
                .forEach(declaration -> printIfImplementsBaseController(declaration, indexContext));
        return false;
    }

    private void printIfImplementsBaseController(ClassOrInterfaceDeclaration declaration, AstScriptIndexContext indexContext) {
        // 跳过抽象类和接口
        if (declaration.isAbstract() || declaration.isInterface()) {
            return;
        }
        if (!isBaseController(declaration, indexContext)) {
            return;
        }
        declaration.getFullyQualifiedName().ifPresent(System.out::println);
    }

    private boolean isBaseController(ClassOrInterfaceDeclaration declaration, AstScriptIndexContext indexContext) {
        if (declaration.isInterface()) {
            return false;
        }
        // 检查extends（由于已经排除了接口，至多只可能有一个）
        var extendedTypes = declaration.getExtendedTypes();
        if (extendedTypes.size() != 1) {
            return false;
        }
        // 对父类进行判断
        return isBaseController(extendedTypes.get(0), indexContext);
    }

    private boolean isBaseController(ClassOrInterfaceType type, AstScriptIndexContext indexContext) {
        // 借助符号解析器获取父类的类限定名
        ResolvedReferenceType referenceType = type.resolve().asReferenceType();
        String qualifiedName = referenceType.getQualifiedName();
        // 如果父类就是BaseController，返回真
        if (BaseController.class.getName().equals(qualifiedName)) {
            return true;
        }
        // 否则继续检查父类的父类
        return Optional.ofNullable(indexContext.getInfoByClassName(qualifiedName))
                .map(JavaFileAstInfo::getAst)
                .flatMap(ast -> ast.findFirst(
                        ClassOrInterfaceDeclaration.class,
                        declaration -> declaration.getFullyQualifiedName()
                                .map(qualifiedName::equals)
                                .orElse(Boolean.FALSE)
                ))
                .map(declaration -> isBaseController(declaration, indexContext))
                .orElse(Boolean.FALSE);
    }

}
