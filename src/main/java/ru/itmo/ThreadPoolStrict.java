package ru.itmo;

import org.jspecify.annotations.NonNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.concurrent.locks.ReentrantLock;


public class ThreadPoolStrict implements ThreadPool {
    private final Thread[] threads;
    private final Queue<TaskRecord<?>> taskQueue = new LinkedList<>();
    private final int capacity = Integer.MAX_VALUE;
    private boolean shutdown = false;

    private final Lock queueLock = new ReentrantLock();
    private final Condition notEmptyOrShutdown;

    ThreadPoolStrict(int n) {
        threads = new Thread[n];
        notEmptyOrShutdown = queueLock.newCondition();

        for (int i = 0; i < threads.length; ++i) {
            threads[i] = new Thread(() -> {
                while (true) {
                    try {
                        TaskRecord<?> task = takeBlocking();
                        task.execute();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            });

            threads[i].start();
        }
    }

    private class TaskRecord<R> implements LightFuture<R> {
        private volatile boolean isReady = false;
        private final Supplier<R> supplier;
        R result;
        Exception exception;

        TaskRecord(Supplier<R> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Override
        public @NonNull R get() throws LightExecutionException {
            try {
                // Maybe this can be optimized
                synchronized (this) {
                    while (!isReady) this.wait();
                }

                if (exception != null) {
                    throw new LightExecutionException(exception);
                }

                return result;
            } catch (InterruptedException e) {
                throw new LightExecutionException(e);
            }
        }

        @Override
        public @NonNull <R1> LightFuture<R1> thenApply(Function<R, R1> function) {
            return submit(() -> {
                try {
                    // Maybe this can be optimized
                    R dependency = get();
                    return function.apply(dependency);
                } catch (LightExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        private void execute() {
            R tempResult = null;
            try {
                tempResult = supplier.get();
            } catch (Exception e) {
                exception = e;
            }

            synchronized (this) {
                isReady = true;
                result = tempResult;
                this.notifyAll();
            }
        }
    }

    @Override
    public @NonNull <R> LightFuture<R> submit(Supplier<R> supplier) throws RejectedExecutionException {
        TaskRecord<R> taskRecord = new TaskRecord<>(supplier);
        tryOfferTask(taskRecord);
        return taskRecord;
    }

    @Override
    public void shutdown() {
        // Data race on shutdown shouldn't lead to incorrect behaviour
        // Read happens here
        if (shutdown) return;

        queueLock.lock();
        try {
            // Write happens here
            shutdown = true;
            notEmptyOrShutdown.signalAll();
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public int getNumberOfThreads() {
        return threads.length;
    }

    // Rejects tasks when capacity reached to not make submit wait
    private <R> void tryOfferTask(TaskRecord<R> element) throws RejectedExecutionException {
        // Data race with shutdown shouldn't lead to incorrect behaviour
        if (shutdown) throw new RejectedExecutionException();

        try {
            queueLock.lockInterruptibly();

            if (shutdown || taskQueue.size() == capacity) {
                throw new RejectedExecutionException();
            }

            taskQueue.offer(element);
            notEmptyOrShutdown.signal();
        } catch (InterruptedException e) {
            throw new RejectedExecutionException();
        } finally {
            queueLock.unlock();
        }
    }

    private TaskRecord<?> takeBlocking() throws InterruptedException {
        queueLock.lock();
        TaskRecord<?> task;
        try {
            if (shutdown && taskQueue.isEmpty()) throw new InterruptedException();
            while (taskQueue.isEmpty() && !shutdown) notEmptyOrShutdown.await();
            if (shutdown && taskQueue.isEmpty()) throw new InterruptedException();
            task = taskQueue.poll();
        } finally {
            queueLock.unlock();
        }

        return task;
    }
}