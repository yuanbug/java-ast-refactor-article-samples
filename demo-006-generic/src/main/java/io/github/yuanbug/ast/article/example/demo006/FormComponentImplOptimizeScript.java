package io.github.yuanbug.ast.article.example.demo006;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.github.yuanbug.ast.article.example.base.entity.AstScriptIndexContext;
import io.github.yuanbug.ast.article.example.base.script.BaseMultiFileScript;

import java.nio.file.Path;
import java.util.Collection;

import static io.github.yuanbug.ast.article.example.base.utils.DemoRunningUtils.getAllJavaFiles;

/**
 * @author yuanbug
 */
public class FormComponentImplOptimizeScript extends BaseMultiFileScript {

    private static final Path SRC_PATH = Path.of(System.getProperty("user.dir"), "demo-006-generic", "src/main/java");

    public FormComponentImplOptimizeScript() {
        super(SRC_PATH.toFile());
    }

    public static void main(String[] args) {
        FormComponentImplOptimizeScript script = new FormComponentImplOptimizeScript();
        Path workingPath = SRC_PATH.resolve("io/github/yuanbug/ast/article/example/demo006/cases/set");
        getAllJavaFiles(workingPath.toFile()).forEach(file -> script.handleJavaFile(file, false, true));
    }

    @Override
    protected boolean doHandle(CompilationUnit ast, AstScriptIndexContext indexContext) {
        ClassOrInterfaceDeclaration classDeclaration = ast.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow();
        var implTypes = classDeclaration.getImplementedTypes();
        if (implTypes.size() != 1) {
            return false;
        }
        ClassOrInterfaceType superType = implTypes.getFirst().orElseThrow();
        if (!"FormComponent".equals(superType.getNameAsString())) {
            return false;
        }
        int typeArgNum = superType.getTypeArguments().map(NodeList::size).orElse(0);
        if (typeArgNum > 0) {
            return false;
        }

        // 获取data字段
        VariableDeclarator data = classDeclaration.findAll(FieldDeclaration.class).stream()
                .map(FieldDeclaration::getVariables)
                .flatMap(Collection::stream)
                .filter(declarator -> "data".equals(declarator.getNameAsString()))
                .findFirst()
                .orElseThrow();
        // 将data的类型设置到implements上
        superType.setTypeArguments(data.getType());

        return true;
    }

}
