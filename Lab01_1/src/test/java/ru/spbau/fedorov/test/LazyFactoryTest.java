package ru.spbau.fedorov.test;

import org.junit.Test;
import ru.spbau.fedorov.algo.Lazy;
import ru.spbau.fedorov.algo.LazyFactory;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LazyFactoryTest {

    private static final Supplier<Integer> checkedSupplier = new Supplier<Integer>() {
        boolean wasCalled = false;

        @Override
        public Integer get() {
            if (wasCalled) {
                fail();
            }
            wasCalled = true;

            int result = 0;
            for (int i = 1; i < 1000; i++) {
                result += Math.sqrt(i + result);
            }
            return result;
        }
    };

    private static final Supplier<Integer> shouldNotBeCalled = () -> {fail(); return 1;};

    @Test
    void testSingleThreadLazyCreate() {
        LazyFactory.createSingleThreadLazy(() -> 1);
    }

    @Test
    void testMultipleThreadLazyCreate() {
        LazyFactory.createMultipleThreadLazy(() -> 1);
    }

    @Test
    void testSingleThreadLazyResult() {
        Lazy lazy = LazyFactory.createSingleThreadLazy(() -> 1);
        assertEquals(1, (int)lazy.get());
        assertEquals(1, (int)lazy.get());
    }

    @Test
    void testMultipleThreadLazyResult() {
        Lazy lazy = LazyFactory.createMultipleThreadLazy(() -> 1);
        assertEquals(1, (int)lazy.get());
        assertEquals(1, (int)lazy.get());
    }

    @Test
    void testSingleThreadLazyNullResult() {
        Lazy lazy = LazyFactory.createSingleThreadLazy(() -> (String)null);
        assertEquals(null, lazy.get());
    }

    @Test
    void testMultipleThreadLazyNullResult() {
        Lazy lazy = LazyFactory.createMultipleThreadLazy(() -> (String)null);
        assertEquals(null, lazy.get());
    }

    @Test
    void testSingleThreadLazyOnlyOnce() {
        Lazy lazy = LazyFactory.createSingleThreadLazy(checkedSupplier);
        assertEquals(5, (int)lazy.get());
        assertEquals(5, (int)lazy.get());
    }

    @Test
    void testMultipleThreadLazyOnlyOnce() {
        Lazy lazy = LazyFactory.createMultipleThreadLazy(checkedSupplier);
        assertEquals(5, (int)lazy.get());
        assertEquals(5, (int)lazy.get());
    }

    @Test
    void testSingleThreadLaziness() {
        for (int i = 0; i < 10; i++) {
            LazyFactory.createSingleThreadLazy(shouldNotBeCalled);
        }
    }

    @Test
    void testMultipleThreadLaziness()  throws InterruptedException {
        Thread threads[] = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {Lazy lazy = LazyFactory.createMultipleThreadLazy(shouldNotBeCalled);});
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    void testMultipleThreadLazyDataRaces() throws InterruptedException {
        Thread threads[] = new Thread[1000];
        Lazy<Integer> lazy = LazyFactory.createMultipleThreadLazy(checkedSupplier);

        for (int i = 0; i < 1000; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        assertEquals(5, (int)lazy.get());
                    }
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }
}
