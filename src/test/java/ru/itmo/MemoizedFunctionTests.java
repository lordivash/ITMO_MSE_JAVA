package ru.itmo;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import ru.itmo.util.CountingFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class MemoizedFunctionTests extends AbstractMemoizedFunctionTest {
    @Test
    public void testTrivialGet() throws Throwable {
        doTest(
                CountingFunction.TRIVIAL_INVERSION,
                Arrays.asList(42, 0, -123),
                Arrays.asList(-42, 0, 123)
        );
    }

    @Test
    public void testNullReturn() throws Throwable {
        doTest(
                CountingFunction.INVERSION_NULL_AT_ZERO,
                Arrays.asList(42, 0, -123),
                Arrays.asList(-42, null, 123)
        );
    }

    @Test
    public void testThrow() throws Throwable {
        doTest(
                CountingFunction.INVERSION_THROW_AT_ZERO,
                Arrays.asList(42, 0, -123),
                Arrays.asList(-42, CountingFunction.EXCEPTION, 123)
        );
    }

    @Test
    public void testRecursionWithoutHandler() {
        List<MemoizedFunction<Integer, Integer>> referenceToMemoizedFunction = new ArrayList<>();
        Object[] referenceToResult = new Object[1];

        Function<Integer, Integer> recursiveFunction = (argument) -> {
            try {
                referenceToResult[0] = referenceToMemoizedFunction.get(0).apply(argument);
            } catch (Throwable e) {
                referenceToResult[0] = e;
            }
            // Note that even though we return null, it should never be cached and observed
            return null;
        };

        referenceToMemoizedFunction.add(getMemoizedFunction(recursiveFunction));
        try {
            referenceToMemoizedFunction.get(0).apply(42);
        } catch (Throwable t) {
            assertTrue(t instanceof RecursiveComputationException);
            assertTrue(referenceToResult[0] instanceof RecursiveComputationException);
            return;
        }
        fail("Computation didn't threw");
    }

    @Test
    public void testRecursionWithHandler() throws Throwable {
        doTestWithRecursiveFunction(
                CountingFunction.TRIVIAL_INVERSION,
                Arrays.asList(-42, 0, 42),
                Arrays.asList(42, 0, -42)
        );
    }

    @Test
    public void testRecursionWithNullReturningHandler() throws Throwable {
        doTestWithRecursiveFunction(
                CountingFunction.INVERSION_NULL_AT_ZERO,
                Arrays.asList(-42, 0, 123),
                Arrays.asList(42, null, -123)
        );
    }

    @Test
    public void testRecursionWithThrowingHandler() throws Throwable {
        doTestWithRecursiveFunction(
                CountingFunction.INVERSION_THROW_AT_ZERO,
                Arrays.asList(-42, 0, 99),
                Arrays.asList(42, CountingFunction.EXCEPTION, -99)
        );
    }
}
