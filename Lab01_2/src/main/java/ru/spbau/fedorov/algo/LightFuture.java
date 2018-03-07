package ru.spbau.fedorov.algo;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Interface for execution tasks.
 * @param <T>
 */
public interface LightFuture<T> {
    /**
     * Returns true if task is already executed
     */
    boolean isReady();

    /**
     * Returns result of task. Waits if not executed yet.
     * @throws LightFutureException when RuntimeError occured on execution
     * @throws InterruptedException when interrupt while waiting for execution
     */
    T get() throws LightFutureException, InterruptedException;

    /**
     * Add task to thread pool which applies function on the result of this task.
     * @param function function to apply
     * @return LightFuture object assigned to requested task
     */
    @NotNull
    LightFuture<T> thenApply(Function<T, T> function);
}
