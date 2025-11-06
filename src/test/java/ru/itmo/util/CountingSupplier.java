package ru.itmo.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class CountingSupplier<T> implements InvocationCountingSupplier<T> {
    public AtomicInteger invocations = new AtomicInteger();

    @Override
    public InvocationCountingSupplier<T> copy() {
        return new CountingSupplier<>(supplier);
    }

    public CountingSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    private final Supplier<T> supplier;

    public CountingSupplier(T valueToReturn) {
        supplier = () -> valueToReturn;
    }

    @Override
    public T get() {
        invocations.incrementAndGet();
        return supplier.get();
    }

    @Override
    public int getInvocationsCount() {
        return invocations.get();
    }
}

