package ru.itmo;

import ru.itmo.LazyValue;
import ru.itmo.RecursiveComputationException;
import ru.itmo.StorageManager;
import ru.itmo.util.InvocationCountingSupplier;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractLazyValueTest {
    protected static final StorageManager storageManager = StorageManager.getInstance();
    protected static final ExecutorService threadPool = Executors.newWorkStealingPool();
    protected static final int WORKERS_COUNT = 16;
    protected static final int ITERATIONS_COUNT = 100;

    protected <T> void doTest(InvocationCountingSupplier<T> supplier, Object expected) throws Throwable {
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            InvocationCountingSupplier<T> copiedSupplier = supplier.copy();
            LazyValue<T> lazyValue = storageManager.createLazyValue(copiedSupplier);
            stressTestLazyValue(lazyValue, copiedSupplier, expected);
        }
    }

    protected <T> void stressTestLazyValue(
            LazyValue<T> lazyValue, InvocationCountingSupplier<T> supplier, Object expectedResult
    ) throws ExecutionException {
        assertEquals(0, supplier.getInvocationsCount());
        assertFalse(lazyValue.isReady());

        final Object firstResult = getResult(lazyValue);

        assertEquals(1, supplier.getInvocationsCount());
        assertTrue(lazyValue.isReady());

        Future<?>[] futures = new Future[WORKERS_COUNT];

        for (int i = 0; i < WORKERS_COUNT; i++) {
            futures[i] = threadPool.submit(() -> {
                assertSame(firstResult, getResult(lazyValue));
                assertEquals(1, supplier.getInvocationsCount());
                assertTrue(lazyValue.isReady());
            });
        }

        assertSame(expectedResult, firstResult);

        for (Future<?> future : futures) {
            while (!future.isDone()) {
                try {
                    future.get();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    protected <T> void doTestWithRecursiveSupplier(
            Object expectedResult, InvocationCountingSupplier<T> onRecursion
    ) throws Throwable {
        @SuppressWarnings("unchecked")
        LazyValue<T>[] referenceToLazyValue = (LazyValue<T>[]) new LazyValue<?>[1];
        Object[] referenceToResult = new Object[1];

        Supplier<T> recursiveSupplier = () -> {
            try {
                referenceToResult[0] = referenceToLazyValue[0].get();
            } catch (Throwable e) {
                referenceToResult[0] = e;
            }
            // Note that even though we return null, it should never be cached and observed
            return null;
        };

        referenceToLazyValue[0] = getLazyValue(recursiveSupplier, onRecursion);

        // Special case, test should throw, so it's pointless to check anything except that
        if (onRecursion == null) {
            Object result = getResult(referenceToLazyValue[0]);
            assertTrue(result instanceof RecursiveComputationException);
            throw (RecursiveComputationException) result;
        }

        // Otherwise, just do the usual stress testing
        stressTestLazyValue(referenceToLazyValue[0], onRecursion, expectedResult);

        // Note that now we've checked that all outer calls to lazyValue.get returned 'expectedResult'.
        // We still have to check that *inner* (i.e. second call that lead to recursion) call to lazyValue.get
        // returned the same value - i.e. we have to check that 'referenceToResult == expectedResult'
        assertSame(expectedResult, referenceToResult[0]);
    }

    protected <T> LazyValue<T> getLazyValue(Supplier<T> supplier) {
        return storageManager.createLazyValue(supplier);
    }

    protected <T> LazyValue<T> getLazyValue(Supplier<T> supplier, Supplier<T> onRecursion) {
        return storageManager.createLazyValue(supplier, onRecursion);
    }

    private <T> Object getResult(LazyValue<T> lazyValue) {
        try {
            return lazyValue.get();
        } catch (Throwable e) {
            return e;
        }
    }
}
