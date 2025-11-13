package ru.itmo;

public class MemoizedFunctionImpl<T, R> implements MemoizedFunction<T, R> {
//    List<LazyValue<R>> cache;

    @Override
    public R apply(T argument) throws RecursiveComputationException, InterruptedException {
        return null;
    }

    @Override
    public boolean isComputedAt(T argument) {
        return false;
    }
}
