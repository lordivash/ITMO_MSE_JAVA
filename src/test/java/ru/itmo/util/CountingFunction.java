package ru.itmo.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class CountingFunction<T, R> implements InvocationCountingFunction<T, R> {
    private final Map<T, Integer> invocations = new ConcurrentHashMap<>();
    private final Function<T, R> function;
    public static final RuntimeException EXCEPTION = new RuntimeException();

    public CountingFunction(Function<T, R> function) {
        this.function = function;
    }

    @Override
    public int getInvocationsCountAt(T argument) {
        return invocations.getOrDefault(argument, 0);
    }

    @Override
    public R apply(T argument) {
        Integer previousCount = invocations.getOrDefault(argument, 0);
        invocations.put(argument, previousCount + 1);
        return function.apply(argument);
    }

    @Override
    public InvocationCountingFunction<T, R> copy() {
        return new CountingFunction<>(function);
    }

    public static final CountingFunction<Integer, Integer> TRIVIAL_INVERSION = new CountingFunction<>((argument) -> -argument);

    public static final CountingFunction<Integer, Integer> INVERSION_NULL_AT_ZERO =
            new CountingFunction<>((argument) -> {
                if (argument == 0) {
                    return null;
                } else {
                    return -argument;
                }
            });

    public static final CountingFunction<Integer, Integer> INVERSION_THROW_AT_ZERO =
            new CountingFunction<>((argument) -> {
                if (argument == 0) {
                    throw EXCEPTION;
                } else {
                    return -argument;
                }
            });

    public static final CountingFunction<Integer, Integer> IDENTITY_WITH_BACKOFF =
            new CountingFunction<>((argument) -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }

                return argument;
            });
}
