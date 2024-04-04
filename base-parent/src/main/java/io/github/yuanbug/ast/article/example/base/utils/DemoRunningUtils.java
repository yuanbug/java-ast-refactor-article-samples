package io.github.yuanbug.ast.article.example.base.utils;

import io.github.yuanbug.ast.article.example.base.script.BaseSimpleScript;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author yuanbug
 * @since 2024-03-25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DemoRunningUtils {

    private static final String CURRENT_CLASS_NAME = DemoRunningUtils.class.getName();

    private static final Pattern MAIN_CLASS_PACKAGE_NAME_PATTERN = Pattern.compile("io\\.github\\.yuanbug\\.ast\\.article\\.example\\.demo(\\d+)");

    public static void executeToAllCases(BaseSimpleScript script, boolean writeBack) {
        executeToAllCases(script, writeBack, !writeBack);
    }

    public static void executeToAllCases(BaseSimpleScript script, boolean writeBack, boolean printCode) {
        List<File> files = getSimpleUseCaseClassFiles();
        for (File file : files) {
            script.handleJavaFile(file, writeBack, printCode);
        }
    }

    public static List<File> getSimpleUseCaseClassFiles() {
        String mainClassName = Stream.of(Thread.currentThread().getStackTrace())
                .map(StackTraceElement::getClassName)
                .filter(name -> !name.startsWith("java."))
                .filter(name -> !CURRENT_CLASS_NAME.equals(name))
                .findFirst()
                .orElseThrow();
        String packageName = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        Matcher matcher = MAIN_CLASS_PACKAGE_NAME_PATTERN.matcher(packageName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Main类包名不符合固定形式 %s".formatted(mainClassName));
        }
        String moduleNamePrefix = "demo-%s".formatted(matcher.group(1));
        Path workingPath = Path.of(System.getProperty("user.dir"));
        Path useCasesRelativePath = Path.of("src/main/java", packageName.replace(".", "/"), "cases");
        File useCasesDir = Optional.ofNullable(workingPath.toFile().listFiles())
                .map(Stream::of)
                .stream()
                .flatMap(Function.identity())
                .filter(File::isDirectory)
                .filter(file -> file.getName().startsWith(moduleNamePrefix))
                .map(File::toPath)
                .map(moduleDir -> moduleDir.resolve(useCasesRelativePath))
                .map(Path::toFile)
                .filter(File::exists)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("定位不到Main类所在模块的用例目录 %s".formatted(mainClassName)));

        return getAllFiles(useCasesDir);
    }

    private static List<File> getAllFiles(File parent) {
        List<File> files = new ArrayList<>(16);
        Queue<File> queue = new LinkedList<>();
        queue.add(parent);
        while (!queue.isEmpty()) {
            File current = queue.poll();
            if (current.isDirectory()) {
                Optional.ofNullable(current.listFiles())
                        .map(Arrays::asList)
                        .ifPresent(queue::addAll);
                continue;
            }
            if (current.getName().endsWith(".java")) {
                files.add(current);
            }
        }
        return files;
    }

}
