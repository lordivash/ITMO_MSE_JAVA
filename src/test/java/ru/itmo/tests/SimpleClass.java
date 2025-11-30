package ru.itmo.tests;

import ru.itmo.aunit.ATest;
import ru.itmo.aunit.Ignore;

public class SimpleClass {
    @ATest
    public void okTest() { }

    @ATest
    public void failTestWithoutExpectedException() {
        int x = (Integer) null;
    }

    @Ignore
    @ATest
    public void ignoreTestNoReason() {
        throw new RuntimeException();
    }

    @Ignore(reason = "reason")
    @ATest
    public void ignoreTestWithReason() {
        throw new RuntimeException();
    }

    @ATest(expected = MyException.class)
    public void testWithExpectedException() throws Exception {
        throw new MyException();
    }

    public static class MyException extends Exception { }
}
