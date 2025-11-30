package ru.itmo;

import org.junit.jupiter.api.Test;
import ru.itmo.tests.ClassWithFixtures;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class Task2Test extends AbstractTest {

    @Test
    public void testFixtures() throws Exception {
        doTest("Fixtures", List.of(ClassWithFixtures.class));
        String actualLog = StaticLog.log.toString();
        String expectedLog =
                "Set up\n" +
                "Before 1\n" +
                "Executing test\n" +
                "After 1\n" +
                "Before 2\n" +
                "Executing test\n" +
                "After 2\n" +
                "Before 3\n" +
                "Executing test\n" +
                "After 3\n" +
                "Tear down\n";
        assertEquals(expectedLog, actualLog, "Something is wrong with fixture methods calls");
    }

    @Test
    public void testFixturesTwice() throws Exception {
        // Because ClassWithFixture imitates inner state via hooping around static field,
        // this test checks (well, *probably* checks) that for each test case instance is
        // correctly created and then disposed.
        testFixtures();
        testFixtures();
    }
}
