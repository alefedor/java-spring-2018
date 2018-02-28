package ru.spbau.fedorov.algo;

import org.jetbrains.annotations.Nullable;

/**
 * Interface for objects with lazy computation of result.
 * It is guarantied that computations will no more than one time.
 * @param <T> type of result of computation.
 */
public interface Lazy<T> {
    /**
     * Get the result of computations.
     * Will run computations only for the first call.
     * Later will just return result of previous computations.
     * @return result of computation.
     */
    @Nullable
    public T get();
}
