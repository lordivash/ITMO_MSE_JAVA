package ru.itmo;

import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public interface LightFuture<R> {
	boolean isReady();

	@NonNull R get() throws LightExecutionException;

	<R1> @NonNull LightFuture<R1> thenApply(Function<R, R1> function);
}
