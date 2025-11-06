package ru.itmo;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import ru.itmo.util.CountingSupplier;
import ru.itmo.util.InvocationCountingSupplier;

public class LazyValueTests extends AbstractLazyValueTest {

    @Test
    public void testTrivialGet() throws Throwable {
        doTest(new CountingSupplier<>(42), 42);
    }

    @Test
    public void testNullReturn() throws Throwable {
        doTest(new CountingSupplier<>(() -> null), null);
    }

    @Test
    public void testThrowingSupplier() throws Throwable {
        RuntimeException fail = new RuntimeException("Fail");
        InvocationCountingSupplier<Object> supplier = new CountingSupplier<>(() -> {
            throw fail;
        });
        LazyValue<Object> lazyValue = storageManager.createLazyValue(supplier);
        stressTestLazyValue(lazyValue, supplier, fail);
    }

    @Test
    public void testRecursionWithoutHandler() {
        assertThrows(
            RecursiveComputationException.class,
            () -> doTestWithRecursiveSupplier(null, null)
        );
    }

    @Test
    public void testRecursionWithHandler() throws Throwable {
        doTestWithRecursiveSupplier(42, new CountingSupplier<>(() -> 42));
    }

    @Test
    public void testRecursionWithNullReturningHandler() throws Throwable {
        doTestWithRecursiveSupplier(null, new CountingSupplier<>(() -> null));
    }

    @Test
    public void testRecursionWithThrowingHandler() throws Throwable {
        RuntimeException runtimeException = new RuntimeException();
        doTestWithRecursiveSupplier(runtimeException, new CountingSupplier<>(() -> {
            throw runtimeException;
        }));
    }
}
