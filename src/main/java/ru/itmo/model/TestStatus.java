package ru.itmo.model;

import ru.itmo.aunit.Ignore;
import ru.itmo.aunit.ATest;

public enum TestStatus {
    /**
     * Тест завершился успешно, или же бросил исключение, заявленное в {@link ATest#expected()}
     */
    SUCCESS,

    /**
     * Тест бросил исключение, не объявленное в {@link ATest#expected()}
     */
    FAIL,

    /**
     * Тест был помечен аннотацией {@link Ignore}
     */
    IGNORED
}
