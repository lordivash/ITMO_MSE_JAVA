package ru.itmo;

import ru.itmo.model.TestResult;
import ru.itmo.model.TestStatus;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;


public class Utils {
    public static String dumpTestResult(Collection<TestResult> results) {
        return results.stream().sorted(Comparator.comparing(TestResult::testName))
                .map(Utils::renderTestResult)
                .collect(Collectors.joining("\n"));
    }

    private static String renderTestResult(TestResult tr) {
        StringBuilder sb = new StringBuilder();

        sb.append("Test case: ").append(tr.testCaseName()).append("\n")
                .append("Test: ").append(tr.testName()).append("\n")
                .append("Result ").append(tr.testStatus()).append("\n");

        if (tr.testStatus() == TestStatus.IGNORED) {
            sb.append("Ignore reason: '").append(tr.ignoreReason()).append("'\n");
        } else {
            if (tr.time() == 0) {
                sb.append("Run for 0 nanoseconds, WTF?!\n");
            } else {
                sb.append("Run for some time\n");
            }

            if (tr.testStatus() == TestStatus.FAIL) {
                sb.append("Failed with: ").append(tr.throwable()).append("\n");
            }
        }

        return sb.toString();
    }
}
