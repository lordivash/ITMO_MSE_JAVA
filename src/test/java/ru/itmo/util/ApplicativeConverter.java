package ru.itmo.util;

import ru.itmo.LazyValue;
import ru.itmo.MemoizedFunction;
import ru.itmo.RecursiveComputationException;


public class ApplicativeConverter {
    public static <T, R> LazyValue<R> convertToValue(MemoizedFunction<T, R> function, T point) {
        return new LazyValue<>() {
            @Override
            public R get() throws RecursiveComputationException, InterruptedException {
                return function.apply(point);
            }

            @Override
            public boolean isReady() {
                return function.isComputedAt(point);
            }
        };
    }

    public static <T, R> InvocationCountingSupplier<R> convertToSupplier(InvocationCountingFunction<T, R> function, T point) {
        return new InvocationCountingSupplier<>() {
            @Override
            public int getInvocationsCount() {
                return function.getInvocationsCountAt(point);
            }

            @Override
            public InvocationCountingSupplier<R> copy() {
                return convertToSupplier(function, point);
            }

            @Override
            public R get() {
                return function.apply(point);
            }
        };
    }
}
