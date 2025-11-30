package ru.itmo.aunit;

import java.util.concurrent.Callable;

public interface TestRule {
    Callable<Void> apply(Callable<Void> testExecutor);
}
