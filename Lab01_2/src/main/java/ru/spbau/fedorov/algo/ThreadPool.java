package ru.spbau.fedorov.algo;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Thread pool which provides multithread execution of tasks
 */
public class ThreadPool {
    private ConcurrentQueue<ThreadPoolTask<?>> tasks = new ConcurrentQueue<>();
    private Thread threads[];

    /**
     * Constructor for ThreadPool with several threads
     * @param threadNumber number of worker threads
     */
    public ThreadPool(int threadNumber) {
        threads = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!Thread.interrupted()) {
                            ThreadPoolTask<?> task = tasks.take();
                            try {
                                task.run();
                            } catch (RuntimeException e) {
                                task.setException(e);
                            }
                        }
                    } catch (InterruptedException e) {
                        // end of while
                    }
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }
    }

    /**
     * Interrupts all worker threads and waites for them to finish.
     * @throws InterruptedException if interrupted while waiting for worker threads finishing
     * @throws LightExecutionException if an RuntimeException in worker thread occured
     */
    public void shutdown() throws InterruptedException, LightExecutionException {
        for (Thread thread : threads) {
            thread.interrupt();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    /**
     * Adds task to queue. Returns assigned to it LightFuture object.
     * @param supplier task to perform
     * @param <T> type of res   ult of task
     * @return LightFuture assigned to the task
     */
    @NotNull
    public <T> LightFuture<T> addTask(@NotNull Supplier<T> supplier) {
        ThreadPoolTask<T> task = new ThreadPoolTask<>(supplier);
        tasks.add(task);
        return task;
    }

    /**
     * Implementation of LightFuture for ThreadPool
     * @param <T> type of result
     */
    private class ThreadPoolTask<T> implements LightFuture<T> {
        private T result = null;
        private volatile boolean isReady = false;
        private RuntimeException exception = null;
        private final Supplier<T> supplier;

        /**
         * Constructs ThreadPoolTask.
         * @param supplier task to execute.
         */
        private ThreadPoolTask(@NotNull Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Override
        public synchronized T get()  throws LightExecutionException, InterruptedException {
            while (!isReady) {
                wait();
            }

            if (exception != null) {
                throw new LightExecutionException(exception);
            }

            return result;
        }

        @NotNull
        @Override
        public synchronized <K> LightFuture<K> thenApply(@NotNull Function<T, K> function) {
            return ThreadPool.this.addTask(new Supplier<K>() {
                @Override
                public K get() {
                    try {
                        return function.apply(ThreadPoolTask.this.get());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        /**
         * Handle case when RuntimeException occured.
         * @param exception which occured.
         */
        private synchronized void setException(@NotNull RuntimeException exception) {
            this.exception = exception;
            isReady = true;
            notify();
        }

        /**
         * Execute task.
         */
        private synchronized void run() {
            result = supplier.get();
            isReady = true;
            notify();
        }
    }

}
