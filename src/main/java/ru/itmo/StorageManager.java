package ru.itmo;

import java.util.function.Function;
import java.util.function.Supplier;

public interface StorageManager {
    static StorageManager getInstance() {
        return new StorageManagerImpl();
    }

    <R> LazyValue<R> createLazyValue(Supplier<R> supplier);
    <R> LazyValue<R> createLazyValue(Supplier<R> supplier, Supplier<R> onRecursion);

    <T, R> MemoizedFunction<T, R> createMemoizedFunction(Function<T, R> function);
    <T, R> MemoizedFunction<T, R> createMemoizedFunction(Function<T, R> function, Function<T, R> onRecursion);
}
