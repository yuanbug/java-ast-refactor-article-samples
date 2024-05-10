package io.github.yuanbug.ast.article.example.base.utils;

import io.github.yuanbug.ast.article.example.base.script.AstScript;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
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

    public static void executeToAllCases(AstScript script, boolean writeBack) {
        executeToAllCases(script, writeBack, !writeBack);
    }

    public static void executeToAllCases(AstScript script, boolean writeBack, boolean printCode) {
        List<File> files = getAllJavaFiles(getUseCasesDir());
        for (File file : files) {
            script.handleJavaFile(file, writeBack, printCode);
        }
    }

    public static File getUseCasesDir() {
        return getDirs((moduleRoot, packageName) -> moduleRoot.resolve(Path.of("src/main/java", packageName.replace(".", "/"), "cases")))[0];
    }

    public static File getSrcDir() {
        return getDirs((moduleRoot, packageName) -> toSrcDir(moduleRoot))[0];
    }

    public static Path toSrcDir(Path mavenModuleRoot) {
        return mavenModuleRoot.resolve(Path.of("src/main/java"));
    }

    public static File getCurrentDemoRoot() {
        return getDirs((moduleRoot, packageName) -> moduleRoot)[0];
    }

    public static File[] getDirs(BiFunction<Path, String, Path> consumer) {
        return getDirs(consumer, (moduleRoot, packageName) -> true);
    }

    public static File[] getDirs(BiFunction<Path, String, Path> consumer, BiPredicate<Path, String> filter) {
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
        return Optional.ofNullable(workingPath.toFile().listFiles())
                .map(Stream::of)
                .stream()
                .flatMap(Function.identity())
                .filter(File::isDirectory)
                .filter(file -> file.getName().startsWith(moduleNamePrefix))
                .map(File::toPath)
                .filter(moduleDir -> filter.test(moduleDir, packageName))
                .map(moduleDir -> consumer.apply(moduleDir, packageName))
                .map(Path::toFile)
                .filter(File::exists)
                .toArray(File[]::new);
    }

    public static List<File> getMavenModules(File root) {
        return getAllFiles(root, file -> {
            if (!file.isDirectory()) {
                return false;
            }
            File[] children = file.listFiles();
            if (null == children) {
                return false;
            }
            return Stream.of(children).anyMatch(child -> "pom.xml".equals(child.getName()));
        });
    }

    public static List<File> getAllJavaFiles(File parent) {
        return getAllFiles(parent, file -> {
            if (file.isDirectory()) {
                return false;
            }
            return file.getName().endsWith(".java");
        });
    }

    public static List<File> getAllFiles(File parent, Predicate<File> filter) {
        List<File> files = new ArrayList<>(16);
        Queue<File> queue = new LinkedList<>();
        queue.add(parent);
        while (!queue.isEmpty()) {
            File current = queue.poll();
            if (current.isDirectory()) {
                Optional.ofNullable(current.listFiles())
                        .map(Arrays::asList)
                        .ifPresent(queue::addAll);
            }
            if (filter.test(current)) {
                files.add(current);
            }
        }
        return files;
    }

}
