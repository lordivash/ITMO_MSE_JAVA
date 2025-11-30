package ru.itmo.aunit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ATest {

	final class NoExpected {}

	Class<?> expected() default NoExpected.class;

}
