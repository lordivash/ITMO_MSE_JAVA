package ru.itmo;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class LazyValueImpl<R> implements LazyValue<R> {
    private R result;
    private volatile boolean resultNotReady;
    private final Supplier<R> task;
    private final Supplier<R> onRecursion;
    private RuntimeException ex;
    private ReentrantLock resultLock;


    LazyValueImpl(Supplier<R> supplier, Supplier<R> onRecursion) {
        task = supplier;
        this.onRecursion = onRecursion;
        resultNotReady = true;
    }

    @Override
    public R get() throws RecursiveComputationException, InterruptedException {
        // What to do with recursive what does it even mean?

        // Should work because volatile ops is atomic and read-after-set
        if (resultNotReady) {
            resultLock.lockInterruptibly();

            // Is it even correct?
            if (resultLock.getHoldCount() > 1) {
                if (onRecursion != null) {
                    result = onRecursion.get();
                } else {
                    throw new RecursiveComputationException();
                }
            }

            try {
                if (resultNotReady) {
                    result = task.get();
                }
            } catch (RuntimeException e) {
                ex = e;
            } finally {
                resultNotReady = false;
                resultLock.unlock();
            }
        }

        if (ex != null) throw ex;

        return result;
    }

    @Override
    public boolean isReady() {
        return !resultNotReady;
    }
}
