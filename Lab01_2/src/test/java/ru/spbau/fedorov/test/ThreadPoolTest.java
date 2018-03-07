package ru.spbau.fedorov.test;

import org.junit.Test;
import ru.spbau.fedorov.algo.LightFuture;
import ru.spbau.fedorov.algo.LightFutureException;
import ru.spbau.fedorov.algo.ThreadPool;

import java.util.Random;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class ThreadPoolTest {

    private static final Supplier<Integer> randomSupplier = () -> new Random().nextInt();

    @Test
    public void testShutdownWithoutTasks() throws LightFutureException, InterruptedException {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        int threadsOnStart = threadGroup.activeCount();
        ThreadPool threadPool = new ThreadPool(100);
        threadPool.shutdown();

        assertEquals(threadsOnStart, threadGroup.activeCount());
    }

    @Test
    public void testShutdown() throws LightFutureException, InterruptedException {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        int threadsOnStart = threadGroup.activeCount();
        ThreadPool threadPool = new ThreadPool(100);

        @SuppressWarnings("unchecked")
        LightFuture<Integer> tasks[] = new LightFuture[100];

        for (int i = 0; i < 10; i++) {
            tasks[i] = threadPool.addTask(randomSupplier);
        }

        for (int i = 0; i < 1000; i++) {
            threadPool.addTask(randomSupplier);
        }

        for (int i = 0; i < 10; i++) {
            tasks[i].get();
        }

        threadPool.shutdown();
        assertEquals(threadsOnStart, threadGroup.activeCount());
    }

    @Test
    public void testExecution() throws LightFutureException, InterruptedException {
        ThreadPool threadPool = new ThreadPool(100);
        @SuppressWarnings("unchecked")
        LightFuture<Integer> tasks[] = new LightFuture[1000];

        for (int i = 0; i < 1000; i++) {
            tasks[i] = threadPool.addTask(randomSupplier);
        }

        for (int i = 0; i < 1000; i++) {
            tasks[i].get();
        }

        threadPool.shutdown();

        for (int i = 0; i < tasks.length; i++) {
            assertEquals(true, tasks[i].isReady());
        }
    }

    @Test
    public void testExecutionResult() throws LightFutureException, InterruptedException {
        ThreadPool threadPool = new ThreadPool(100);
        @SuppressWarnings("unchecked")
        LightFuture<Integer> tasks[] = new LightFuture[1000];

        for (int i = 0; i < 1000; i++) {
            int toAdd = i;
            tasks[i] = threadPool.addTask(() -> toAdd);
        }

        for (int i = 0; i < 1000; i++) {
            tasks[i].get();
        }

        threadPool.shutdown();

        for (int i = 0; i < tasks.length; i++) {
            assertEquals(true, tasks[i].isReady());
            assertEquals(i, (int)tasks[i].get());
        }
    }

    @Test
    public void testThenApply() throws LightFutureException, InterruptedException {
        ThreadPool threadPool = new ThreadPool(100);
        @SuppressWarnings("unchecked")
        LightFuture<Integer> tasks[] = new LightFuture[1000];

        tasks[0] = threadPool.addTask(() -> 0);
        for (int i = 1; i < 1000; i++) {
            tasks[i] = tasks[i - 1].thenApply((v) -> v + 1);
        }

        for (int i = 0; i < 1000; i++) {
            tasks[i].get();
        }

        threadPool.shutdown();

        for (int i = 0; i < tasks.length; i++) {
            assertEquals(true, tasks[i].isReady());
            assertEquals(i, (int)tasks[i].get());
        }
    }

    @Test
    public void testThenApplyOnSame() throws LightFutureException, InterruptedException {
        ThreadPool threadPool = new ThreadPool(100);
        @SuppressWarnings("unchecked")
        LightFuture<Integer> tasks[] = new LightFuture[1000];

        tasks[0] = threadPool.addTask(() -> 0);
        for (int i = 1; i < 1000; i++) {
            tasks[i] = tasks[0].thenApply((v) -> v + 1);
        }

        for (int i = 0; i < 1000; i++) {
            tasks[i].get();
        }

        threadPool.shutdown();

        for (int i = 0; i < tasks.length; i++) {
            assertEquals(true, tasks[i].isReady());
            assertEquals((i == 0 ? 0 : 1), (int)tasks[i].get());
        }
    }

    @Test(expected = LightFutureException.class)
    public void testRuntimeError() throws LightFutureException, InterruptedException {
        ThreadPool threadPool = new ThreadPool(100);
        @SuppressWarnings("unchecked")
        LightFuture<Integer> tasks[] = new LightFuture[1000];

        for (int i = 0; i < 1000; i++) {
            int toAdd = i;
            if (i == 500)
                tasks[i] = threadPool.addTask(() -> ((Object) null).hashCode());
            else
                tasks[i] = threadPool.addTask(() -> toAdd);
        }

        for (int i = 0; i < 1000; i++) {
            tasks[i].get();
        }

        threadPool.shutdown();
    }
}
