package ru.itmo.model;

public record TestResult(
        String testCaseName,
        String testName,
        TestStatus testStatus,
        long time,
        Throwable throwable,
        String ignoreReason
) {

    public static TestResult success(String testName, String testCaseName, long time) {
        return new TestResult(testCaseName, testName, TestStatus.SUCCESS, time, null, null);
    }

    public static TestResult failure(String testName, String testCaseName, long time, Throwable throwable) {
        return new TestResult(testCaseName, testName, TestStatus.FAIL, time, throwable, null);
    }

    public static TestResult ignored(String testName, String testCaseName, String ignoreReason) {
        return new TestResult(testCaseName, testName, TestStatus.IGNORED, 0, null, ignoreReason);
    }
}
