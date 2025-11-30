package ru.itmo;

import ru.itmo.model.TestResult;
import ru.itmo.model.TestRunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;

public abstract class AbstractTest {

    protected void doTest(String testName, List<Class<?>> classes) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		Path path = Path.of(classLoader.getResource(testName).toURI());
        String expectedData = Files.readString(path);

        Collection<TestResult> actualTestResults = new TestRunner().runTests(classes);
        String actualData = Utils.dumpTestResult(actualTestResults);

        assertLinesMatch(expectedData.lines(), actualData.lines());
    }
}
