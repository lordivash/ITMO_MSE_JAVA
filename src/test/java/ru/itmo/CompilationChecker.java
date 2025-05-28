package ru.itmo;

import javax.tools.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CompilationChecker {
    public static boolean checkCompilation(String fromDirectory, String className) {
        String fullPath = fromDirectory + "/" + className.replace(".", "/") + ".java";
        File javaFile = new File(fullPath);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("JDK compiler not available. Ensure using JDK, not JRE.");
        }

        // Коллектор для сбора ошибок
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        try (StandardJavaFileManager fileManager =
                     compiler.getStandardFileManager(diagnostics, null, null)) {

            // Подготовка исходных файлов
            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjects(javaFile);

            // Настройка параметров компиляции
            List<String> options = new ArrayList<>();
            options.add("-classpath");
            options.add(System.getProperty("java.class.path"));

            // Запуск компиляции
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    options,
                    null,
                    compilationUnits
            );

            return task.call();
        } catch (Exception e) {
            return false;
        }
    }
}
