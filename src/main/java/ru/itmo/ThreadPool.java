package ru.itmo;

import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;

public interface ThreadPool {
	static @NonNull ThreadPool create(int threads) {
		throw new UnsupportedOperationException();
	}

	<R> @NonNull LightFuture<R> submit(Supplier<R> supplier);

	void shutdown();

	int getNumberOfThreads();
}
