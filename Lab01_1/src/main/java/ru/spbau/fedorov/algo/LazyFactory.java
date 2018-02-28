package ru.spbau.fedorov.algo;


import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Factory for generation objects of {@link Lazy} interface.
 */
public class LazyFactory {
    /**
     * Return {@link Lazy} object for working in single thread.
     * @param supplier object which can compute result of type T.
     * @param <T> type of result of computations.
     */
    public static <T> Lazy<T> createSingleThreadLazy(@NotNull Supplier<T> supplier) {
        return new Lazy<T>() {
            private Optional<T> result;

            @Override
            public T get() {
                if (result == null) {
                    result = Optional.ofNullable(supplier.get());
                }

                return result.orElse(null);
            }
        };
    }

    /**
     * Return {@link Lazy} object for working in multiple threads.
     * @param supplier object which can compute result of type T.
     * @param <T> type of result of computations.
     */
    public static <T> Lazy<T> createMultipleThreadLazy(@NotNull Supplier<T> supplier) {
        return new Lazy<T>() {
            private Optional<T> result;

            @Override
            public T get() {
                synchronized (this) {
                    if (result == null) {
                        result = Optional.ofNullable(supplier.get());
                    }
                }

                return result.orElse(null);
            }
        };
    }
}
