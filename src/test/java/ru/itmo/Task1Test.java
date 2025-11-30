package ru.itmo;

import org.junit.jupiter.api.Test;
import ru.itmo.tests.SimpleClass;
import ru.itmo.tests.SimulatingTests;

import java.util.List;

public class Task1Test extends AbstractTest {
    @Test
    public void testBasic() throws Throwable {
        doTest("SimpleTest", List.of(SimpleClass.class));
    }

    @Test
    public void testMulticlassTest() throws Throwable {
        doTest("MulticlassTest", List.of(SimpleClass.class, SimulatingTests.class));
    }
}
