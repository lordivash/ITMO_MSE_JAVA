package ru.itmo;

import org.junit.jupiter.api.Test;
import ru.itmo.tests.ClassWithRules;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Task3Test extends AbstractTest {
    @Test
    public void testClassWithRules() throws Exception {
        doTest("Rules", List.of(ClassWithRules.class));
        String actualLog = StaticLog.log.toString();
        String expectedLog =
                "Inner rule apply, 1\n" +
                "Outer rule apply, 2\n" +
                "Outer rule before wrapper, 3\n" +
                "Inner rule before wrapper, 4\n" +
                "Before, 5\n" +
                "Executing test\n" +
                "After, 6\n" +
                "Inner rule after wrapper, 7\n" +
                "Outer rule after wrapper, 8\n" +
                "Inner rule apply, 9\n" +
                "Outer rule apply, 10\n" +
                "Outer rule before wrapper, 11\n" +
                "Inner rule before wrapper, 12\n" +
                "Before, 13\n" +
                "Executing test\n" +
                "After, 14\n" +
                "Inner rule after wrapper, 15\n" +
                "Outer rule after wrapper, 16\n" +
                "Inner rule apply, 17\n" +
                "Outer rule apply, 18\n" +
                "Outer rule before wrapper, 19\n" +
                "Inner rule before wrapper, 20\n" +
                "Before, 21\n" +
                "Executing test\n" +
                "After, 22\n" +
                "Inner rule after wrapper, 23\n" +
                "Outer rule after wrapper, 24\n";
        assertEquals(expectedLog, actualLog, "Something is wrong with rules methods calls");
    }

    @Test
    public void testClassWithRulesTwice() throws Exception {
        testClassWithRules();
        testClassWithRules();
    }
}
