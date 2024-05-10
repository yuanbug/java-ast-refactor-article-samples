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
        var extendedTypes = declaration.getExtendedTypes();
        if (extendedTypes.size() != 1) {
            return false;
        }
        return isBaseController(extendedTypes.get(0), indexContext);
    }

    private boolean isBaseController(ClassOrInterfaceType type, AstScriptIndexContext indexContext) {
        ResolvedReferenceType referenceType = type.resolve().asReferenceType();
        String qualifiedName = referenceType.getQualifiedName();
        if (BaseController.class.getName().equals(qualifiedName)) {
            return true;
        }
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
