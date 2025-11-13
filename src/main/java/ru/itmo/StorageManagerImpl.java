package ru.itmo;

import java.util.function.Function;
import java.util.function.Supplier;

public class StorageManagerImpl implements StorageManager {
    @Override
    public <R> LazyValue<R> createLazyValue(Supplier<R> supplier) {
        return new LazyValueImpl<R>(supplier, null);
    }

    @Override
    public <R> LazyValue<R> createLazyValue(Supplier<R> supplier, Supplier<R> onRecursion) {
        return new LazyValueImpl<R>(supplier, onRecursion);
    }

    @Override
    public <T, R> MemoizedFunction<T, R> createMemoizedFunction(Function<T, R> function) {
//        return new MemoizedFunctionImpl<T, R>(function, null);
        return null;
    }

    @Override
    public <T, R> MemoizedFunction<T, R> createMemoizedFunction(Function<T, R> function, Function<T, R> onRecursion) {
//        return new MemoizedFunctionImpl<T, R>(function, onRecursion);
        return null;
    }
}
