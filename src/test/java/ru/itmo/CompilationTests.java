package ru.itmo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CompilationTests {
    private final Implementor implementor = new Implementor("out");

    String implementFromDirectory(String directoryPath, String className) {
        try {
            return implementor.implementFromDirectory(directoryPath, className);
        } catch (ImplementorException e) {
            return null;
        }
    }

    String implementStandard(String className) {
        try {
            return implementor.implementFromStandardLibrary(className);
        } catch (ImplementorException e) {
            return null;
        }
    }

    @Test
    void implementedDefaultValues() {
        String implementedName = implementFromDirectory("src/main/java", "ru.itmo.TestDefaultValues");
        assertTrue(CompilationChecker.checkCompilation("out", implementedName));
    }

    @Test
    void implementTestClass() {
        String implementedName = implementFromDirectory("src/main/java", "ru.itmo.Test");
        assertTrue(CompilationChecker.checkCompilation("out", implementedName));
    }

    @Test
    void implementStandardQueue() {
        String implementedName = implementStandard("java.util.Queue");
        assertTrue(CompilationChecker.checkCompilation("out", implementedName));
    }

    @Test
    void implementStandardList() {
        String implementedName = implementStandard("java.util.List");
        assertTrue(CompilationChecker.checkCompilation("out", implementedName));
    }

    @Test
    void implementStandardMap() {
        String implementedName = implementStandard("java.util.Map");
        assertTrue(CompilationChecker.checkCompilation("out", implementedName));
    }

    @Test
    void implementStandardSet() {
        String implementedName = implementStandard("java.util.Set");
        assertTrue(CompilationChecker.checkCompilation("out", implementedName));
    }

    @Test
    void implementStandardIterable() {
        String implementedName = implementStandard("java.lang.Iterable");
        assertTrue(CompilationChecker.checkCompilation("out", implementedName));
    }

    @Test
    void implementStandardCollection() {
        String implementedName = implementStandard("java.util.AbstractCollection");
        assertTrue(CompilationChecker.checkCompilation("out", implementedName));
    }
}
