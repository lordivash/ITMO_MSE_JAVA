package ru.itmo.tests;

import ru.itmo.aunit.ATest;

public class SimulatingTests {
    @ATest
    public void testIncrementAndGet() {
        ClassToTest classToTest = new ClassToTest();
        classToTest.inc();
        if (classToTest.get() != 1) {
            throw new AssertionError();
        }
    }

    @ATest(expected = ArithmeticException.class)
    public int testDivisionByZero() {
        ClassToTest classToTest = new ClassToTest();
        return 42 / classToTest.get();
    }

    @ATest
    public void longTest() {
        ClassToTest classToTest = new ClassToTest();
        for (int i = 0; i < 10_000_000; i++) {
            classToTest.inc();
        }

        if (classToTest.get() != 10_000_000) {
            throw new AssertionError();
        }
    }

    private static class ClassToTest {
        private int x = 0;

        public void inc() {
            x++;
        }

        public int get() {
            return x;
        }
    }
}
