package ru.itmo;

import ru.itmo.LazyValue;
import ru.itmo.MemoizedFunction;
import ru.itmo.util.ApplicativeConverter;
import ru.itmo.util.InvocationCountingFunction;
import ru.itmo.util.InvocationCountingSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public abstract class AbstractMemoizedFunctionTest extends AbstractLazyValueTest {
    protected <T, R> void doTest(InvocationCountingFunction<T, R> basicFunction, List<T> points, List<Object> expectedResults) throws ExecutionException {
        for (int i = 0; i < ITERATIONS_COUNT; i++) {
            InvocationCountingFunction<T, R> functionForThisIteration = basicFunction.copy();
            MemoizedFunction<T, R> memoizedFunction = storageManager.createMemoizedFunction(functionForThisIteration);
            for (int j = 0; j < points.size(); j++) {
                LazyValue<R> lazyValueAtPoint = ApplicativeConverter.convertToValue(memoizedFunction, points.get(j));

                InvocationCountingSupplier<R> supplierAtPoint = ApplicativeConverter.convertToSupplier(
                        functionForThisIteration, points.get(j)
                );

                stressTestLazyValue(lazyValueAtPoint, supplierAtPoint, expectedResults.get(j));
            }
        }
    }

    protected <T, R> void doTestWithRecursiveFunction(
            InvocationCountingFunction<T, R> onRecursion, List<T> points, List<Object> expectedResults
    ) throws Throwable {
        List<MemoizedFunction<T, R>> referenceToMemoizedFunction = new ArrayList<>();
        Object[] referenceToResult = new Object[1];

        Function<T, R> recursiveFunction = (argument) -> {
            try {
                referenceToResult[0] = referenceToMemoizedFunction.get(0).apply(argument);
            } catch (Throwable e) {
                referenceToResult[0] = e;
            }
            // Note that even though we return null, it should never be cached and observed
            return null;
        };

        referenceToMemoizedFunction.add(getMemoizedFunction(recursiveFunction, onRecursion));

        for (int i = 0; i < points.size(); i++) {
            LazyValue<R> lazyValueAtPoint = ApplicativeConverter.convertToValue(
                    referenceToMemoizedFunction.get(0), points.get(i)
            );

            InvocationCountingSupplier<R> recursionHandlerAtPoint = ApplicativeConverter.convertToSupplier(
                    onRecursion, points.get(i)
            );

            stressTestLazyValue(lazyValueAtPoint, recursionHandlerAtPoint, expectedResults.get(i));
        }
    }

    protected <T, R> MemoizedFunction<T, R> getMemoizedFunction(Function<T, R> function) {
        return storageManager.createMemoizedFunction(function);
    }

    protected <T, R> MemoizedFunction<T, R> getMemoizedFunction(Function<T, R> function, Function<T, R> onRecursion) {
        return storageManager.createMemoizedFunction(function, onRecursion);
    }

}
