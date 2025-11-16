package ru.itmo;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThreadPoolTest {
    private ThreadPoolStrict pool;
    private final int n = 4;

    @BeforeEach
    void setUp() {
        pool = new ThreadPoolStrict(n);
    }

    @AfterEach
    void tearDown() {
        pool.shutdown();
    }

    @Test
    @Timeout(2)
    void stressCount() {
        int expectedCount = 100000;

        ArrayList<LightFuture<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < expectedCount; ++i) {
            futures.add(pool.submit(() -> 1));
        }

        int count = 0;
        for (LightFuture<Integer> future : futures) {
            try {
                count += future.get();
            } catch (LightExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        assertEquals(expectedCount, count);
    }

    // Check that pool has at least n threads
    // It can't be bigger
    @Test
    @Timeout(5)
    void barrierWithMoreTasksThanThreads() throws Exception {
        CyclicBarrier barrier = new CyclicBarrier(n + 1);

        for (int i = 0; i < n; i++) {
            pool.submit(() -> {
                try {
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
        }

        long startTime = System.currentTimeMillis();
        barrier.await(5000, TimeUnit.MILLISECONDS);
        assertTrue(System.currentTimeMillis() - startTime < 5000);
    }

    @Test
    @Timeout(2)
    void simpleTaskExecution() throws Exception {
        LightFuture<Integer> future = pool.submit(() -> 42);
        assertEquals(42, future.get());
        assertTrue(future.isReady());
    }

    @Test
    @Timeout(2)
    void exceptionInTask() {
        LightFuture<Object> future = pool.submit(() -> {
            throw new RuntimeException("Test exception");
        });

        LightExecutionException ex = assertThrows(
                LightExecutionException.class,
                future::get
        );
        assertEquals(RuntimeException.class, ex.getCause().getClass());
        assertEquals("Test exception", ex.getCause().getMessage());
    }

    @Test
    @Timeout(2)
    void thenApplyChain() throws Exception {
        LightFuture<String> future = pool.submit(() -> "Hello")
                .thenApply(s -> s + " World")
                .thenApply(String::toUpperCase);

        assertEquals("HELLO WORLD", future.get());
    }

    @Test
    @Timeout(2)
    void thenApplyChainTypeChange() {
        String expectedResult = "Final result is " + (2 * 5 - 2) / 2 + 10;

        LightFuture<String> future = pool.submit(() -> 2)
                .thenApply((x) -> x * 5)
                .thenApply((x) -> x - 2)
                .thenApply((x) -> x / 2)
                .thenApply((x) -> "Final result is " + x + 10);

        String result;
        try {
            result = future.get();
        } catch (LightExecutionException e) {
            throw new RuntimeException(e);
        }


        assertEquals(expectedResult, result);
    }

    @Test
    @Timeout(2)
    void thenApplyWithException() {
        LightFuture<Integer> future = pool.submit(() -> {
            throw new RuntimeException("Error");
        });

        LightFuture<String> transformed = future.thenApply(Object::toString);

        LightExecutionException ex = assertThrows(
                LightExecutionException.class,
                transformed::get
        );
        assertEquals(RuntimeException.class, ex.getCause().getClass());
    }

    @Test
    @Timeout(3)
    void parallelExecution() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(4);

        for (int i = 0; i < 4; i++) {
            pool.submit(() -> {
                try {
                    startLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                counter.incrementAndGet();
                endLatch.countDown();
                return null;
            });
        }

        startLatch.countDown();
        endLatch.await(2, TimeUnit.SECONDS);
        assertEquals(4, counter.get());
    }

    @Test
    @Timeout(2)
    void shutdownRejectsNewTasks() {
        pool.shutdown();
        assertThrows(RejectedExecutionException.class, () ->
                pool.submit(() -> 1)
        );
    }

    @Test
    @Timeout(2)
    void shutdownAllowsCompletion() throws Exception {
        CountDownLatch taskStarted = new CountDownLatch(1);
        CountDownLatch taskFinished = new CountDownLatch(1);

        LightFuture<Integer> future = pool.submit(() -> {
            taskStarted.countDown();
            try {
                taskFinished.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 100;
        });

        taskStarted.await();
        pool.shutdown();

        // Task executes after shutdown
        assertFalse(future.isReady());
        taskFinished.countDown();
        assertEquals(100, future.get());
        assertTrue(future.isReady());
    }

    @Test
    @Timeout(2)
    void thenApplyNotBlocking() throws Exception {
        CountDownLatch innerTaskStarted = new CountDownLatch(1);
        CountDownLatch blocker = new CountDownLatch(1);

        LightFuture<Integer> first = pool.submit(() -> {
            innerTaskStarted.countDown();
            try {
                blocker.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 1;
        });

        LightFuture<Integer> second = first.thenApply(x -> x + 1);

        innerTaskStarted.await();
        assertFalse(first.isReady());
        assertFalse(second.isReady());

        blocker.countDown();
        assertEquals(2, second.get());
        assertTrue(second.isReady());
    }
}
