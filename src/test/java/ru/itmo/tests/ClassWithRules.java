package ru.itmo.tests;

import ru.itmo.StaticLog;
import ru.itmo.aunit.ATest;
import ru.itmo.aunit.AfterEach;
import ru.itmo.aunit.BeforeEach;
import ru.itmo.aunit.Ignore;
import ru.itmo.aunit.Rule;
import ru.itmo.aunit.TestRule;

import java.util.concurrent.Callable;


public class ClassWithRules {
    private int counter;

    public ClassWithRules() {
        StaticLog.log = new StringBuilder();
    }

    @Rule
    public TestRule myTestRule1 = new MyTestRule("Inner rule");

    @Rule
    public MyTestRule myTestRule2 = new MyTestRule("Outer rule");

    @BeforeEach
    public void before() {
		StaticLog.log.append("Before, ").append(++counter).append("\n");
    }

    @AfterEach
    public void after() {
		StaticLog.log.append("After, ").append(++counter).append("\n");
    }

    @ATest
    public void okTest() {
		StaticLog.log.append("Executing test\n");
    }

    @ATest(expected = RuntimeException.class)
    public void expectException() {
		StaticLog.log.append("Executing test\n");
        throw new RuntimeException();
    }

    @ATest
    public void fail() {
		StaticLog.log.append("Executing test\n");
        throw new RuntimeException();
    }

    @ATest
    @Ignore(reason = "ignore")
    public void ignored() {
		StaticLog.log.append("Ignored test shoudln't be invoked!\n");
    }

    public class MyTestRule implements TestRule {
        private final String name;

        public MyTestRule(String name) {
            this.name = name;
        }

        @Override
        public Callable<Void> apply(Callable<Void> testExecutor) {
			StaticLog.log.append(name).append(" apply, ").append(++counter).append("\n");
            return () -> {
				StaticLog.log.append(name).append(" before wrapper, ").append(++counter).append("\n");

                testExecutor.call();

				StaticLog.log.append(name).append(" after wrapper, ").append(++counter).append("\n");
                return null;
            };
        }
    }
}
