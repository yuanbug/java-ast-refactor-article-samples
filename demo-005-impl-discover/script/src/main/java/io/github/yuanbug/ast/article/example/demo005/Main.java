package io.github.yuanbug.ast.article.example.demo005;

import io.github.yuanbug.ast.article.example.base.utils.DemoRunningUtils;
import io.github.yuanbug.ast.article.example.demo005.script.BaseControllerImplSearchScript;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Path;

import static io.github.yuanbug.ast.article.example.base.utils.DemoRunningUtils.*;

/**
 * @author yuanbug
 * @since 2024-05-10
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Main {

    public static void main(String[] args) {
        File[] caseModules = getMavenModules(getCurrentDemoRoot()).stream()
                .filter(dir -> dir.getName().matches("^case-.+$"))
                .map(File::toPath)
                .map(DemoRunningUtils::toSrcDir)
                .map(Path::toFile)
                .toArray(File[]::new);
        BaseControllerImplSearchScript script = new BaseControllerImplSearchScript(caseModules);

        for (File caseModule : caseModules) {
            getAllJavaFiles(caseModule).forEach(file -> script.handleJavaFile(file, false, false));
        }
    }

}
