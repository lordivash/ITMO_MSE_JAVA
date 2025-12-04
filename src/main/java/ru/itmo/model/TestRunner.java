package ru.itmo.model;

import ru.itmo.aunit.ATest;
import ru.itmo.aunit.AfterEach;
import ru.itmo.aunit.BeforeEach;
import ru.itmo.aunit.Ignore;
import ru.itmo.aunit.Rule;
import ru.itmo.aunit.SetUp;
import ru.itmo.aunit.TearDown;
import ru.itmo.aunit.TestRule;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class TestRunner {
    public Collection<TestResult> runTests(List<Class<?>> classesToTest) {
        List<TestResult> allResults = new ArrayList<>();

        for (Class<?> testClass : classesToTest) {
            try {
                List<TestResult> classResults = runTestsInClass(testClass);
                allResults.addAll(classResults);
            } catch (Exception e) {
                String className = testClass.getSimpleName();
                for (Method method : testClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(ATest.class)) {
                        allResults.add(TestResult.failure(
                                method.getName(),
                                className,
                                0,
                                new TestExecutionException(e)
                        ));
                    }
                }
            }
        }

        return allResults;
    }

    private List<TestResult> runTestsInClass(Class<?> testClass) throws Exception {
        List<TestResult> results = new ArrayList<>();

        Method[] allMethods = testClass.getDeclaredMethods();
        Field[] allFields = testClass.getDeclaredFields();

        List<Method> setUpMethods = new ArrayList<>();
        List<Method> tearDownMethods = new ArrayList<>();
        List<Method> beforeEachMethods = new ArrayList<>();
        List<Method> afterEachMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();

        // Easy solution for now
        for (Method method : allMethods) {
            if (method.isAnnotationPresent(SetUp.class)) {
                method.setAccessible(true);
                setUpMethods.add(method);
            }
            if (method.isAnnotationPresent(TearDown.class)) {
                method.setAccessible(true);
                tearDownMethods.add(method);
            }
            if (method.isAnnotationPresent(BeforeEach.class)) {
                method.setAccessible(true);
                beforeEachMethods.add(method);
            }
            if (method.isAnnotationPresent(AfterEach.class)) {
                method.setAccessible(true);
                afterEachMethods.add(method);
            }
            if (method.isAnnotationPresent(ATest.class)) {
                method.setAccessible(true);
                testMethods.add(method);
            }
        }

        List<Field> ruleFieldsList = new ArrayList<>();
        for (Field field : allFields) {
            if (field.isAnnotationPresent(Rule.class)) {
                field.setAccessible(true);
                ruleFieldsList.add(field);
            }
        }

        Object testInstance = testClass.getDeclaredConstructor().newInstance();

        // Init rules
        List<TestRule> rules = new ArrayList<>();
        for (Field ruleField : ruleFieldsList) {
            try {
                TestRule rule = (TestRule) ruleField.get(testInstance);
                if (rule == null) {
                    rule = (TestRule) ruleField.getType().getDeclaredConstructor().newInstance();
                    ruleField.set(testInstance, rule);
                }
                rules.add(rule);
            } catch (Exception e) {
                throw new TestExecutionException(e);
            }
        }

        // Set up
        for (Method setUpMethod : setUpMethods) {
            setUpMethod.invoke(testInstance);
        }

        try {
            // Execute tests
            for (Method testMethod : testMethods) {
                ATest aTest = testMethod.getAnnotation(ATest.class);
                Ignore ignore = testMethod.getAnnotation(Ignore.class);

                String testCaseName = testClass.getSimpleName();
                String testName = testMethod.getName();

                if (ignore != null) {
                    results.add(TestResult.ignored(testName, testCaseName, ignore.reason()));
                    continue;
                }

                Callable<Void> currentCallable = () -> {
                    for (Method beforeEach : beforeEachMethods) {
                        beforeEach.invoke(testInstance);
                    }

                    try {
                        testMethod.invoke(testInstance);
                    } finally {
                        for (Method afterEach : afterEachMethods) {
                            afterEach.invoke(testInstance);
                        }
                    }
                    return null;
                };

                // Apply rules
                for (TestRule rule : rules) {
                    currentCallable = rule.apply(currentCallable);
                }

                long startTime = System.nanoTime();
                try {
                    currentCallable.call();

                    // Check expected exception
                    Class<?> expectedException = aTest.expected();
                    long endTime = System.nanoTime();
                    long duration = endTime - startTime;

                    if (expectedException != ATest.NoExpected.class) {
                        results.add(TestResult.failure(
                                testName,
                                testCaseName,
                                duration,
                                new AssertionError("Expected exception: " + expectedException.getName() + " but none was thrown")
                        ));
                    } else {
                        results.add(TestResult.success(testName, testCaseName, duration));
                    }
                } catch (Exception e) {
                    Throwable actualException = e;
                    if (e instanceof InvocationTargetException) {
                        actualException = ((InvocationTargetException) e).getTargetException();
                    } else if (e.getCause() != null) {
                        actualException = e.getCause();
                    }

                    long endTime = System.nanoTime();
                    long duration = endTime - startTime;

                    Class<?> expectedException = aTest.expected();
                    if (
                            expectedException != ATest.NoExpected.class &&
                            expectedException.isAssignableFrom(actualException.getClass())
                    ) {
                        results.add(TestResult.success(testName, testCaseName, duration));
                    } else {
                        results.add(TestResult.failure(testName, testCaseName, duration, actualException));
                    }
                }
            }
        } finally {
            for (Method tearDownMethod : tearDownMethods) {
                try {
                    tearDownMethod.invoke(testInstance);
                } catch (Exception e) {
                    // Ignore exception in tear down
                }
            }
        }

        return results;
    }
}