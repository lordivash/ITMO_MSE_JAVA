package ru.itmo.tests;

import ru.itmo.StaticLog;
import ru.itmo.aunit.ATest;
import ru.itmo.aunit.AfterEach;
import ru.itmo.aunit.BeforeEach;
import ru.itmo.aunit.Ignore;
import ru.itmo.aunit.SetUp;
import ru.itmo.aunit.TearDown;

public class ClassWithFixtures {


    private int counter = 0;

    public ClassWithFixtures() {
        StaticLog.log = new StringBuilder();
    }

    @SetUp
    public void setUp() {
		StaticLog.log.append("Set up\n");
    }

    @TearDown
    public void tearDown() {
		StaticLog.log.append("Tear down\n");
    }

    @BeforeEach
    public void beforeTest() {
		StaticLog.log.append("Before ").append(++counter).append("\n");
    }

    @AfterEach
    public void afterTest() {
		StaticLog.log.append("After ").append(counter).append("\n");
    }

    @ATest
    public String okTest() {
		StaticLog.log.append("Executing test\n");
        return "ok";
    }

    @Ignore
    @ATest
    public String ignoredTest() {
		StaticLog.log.append("IGNORED TEST\n");
        throw new RuntimeException();
    }

    @ATest(expected = RuntimeException.class)
    public void failWithExpected() {
		StaticLog.log.append("Executing test\n");
        throw new RuntimeException();
    }

    @ATest
    public void failWithoutExpected() {
		StaticLog.log.append("Executing test\n");
        throw new RuntimeException();
    }

    public String getLog() {
        return StaticLog.log.toString();
    }
}
