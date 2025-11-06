package ru.itmo.util;

import java.util.function.Function;

public interface InvocationCountingFunction<T, R> extends Function<T, R> {
    int getInvocationsCountAt(T argument);

    InvocationCountingFunction<T, R> copy();
}
