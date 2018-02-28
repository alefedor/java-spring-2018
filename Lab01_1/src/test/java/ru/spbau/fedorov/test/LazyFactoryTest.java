package ru.spbau.fedorov.test;

import org.junit.Before;
import org.junit.Test;
import ru.spbau.fedorov.algo.Lazy;
import ru.spbau.fedorov.algo.LazyFactory;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LazyFactoryTest {

    private static Supplier<Integer> checkedSupplier;

    @Before
    public void setUp() {
        checkedSupplier = new Supplier<Integer>() {
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
    }

    private static final Supplier<Integer> shouldNotBeCalled = () -> {fail(); return 1;};

    @Test
    public void testSingleThreadLazyCreate() {
        LazyFactory.createSingleThreadLazy(() -> 1);
    }

    @Test
    public void testMultipleThreadLazyCreate() {
        LazyFactory.createMultipleThreadLazy(() -> 1);
    }

    @Test
    public void testSingleThreadLazyResult() {
        Lazy lazy = LazyFactory.createSingleThreadLazy(() -> 1);
        assertEquals(1, (int)lazy.get());
        assertEquals(1, (int)lazy.get());
    }

    @Test
    public void testMultipleThreadLazyResult() {
        Lazy lazy = LazyFactory.createMultipleThreadLazy(() -> 1);
        assertEquals(1, (int)lazy.get());
        assertEquals(1, (int)lazy.get());
    }

    @Test
    public void testSingleThreadLazyNullResult() {
        Lazy lazy = LazyFactory.createSingleThreadLazy(() -> (String)null);
        assertEquals(null, lazy.get());
    }

    @Test
    public void testMultipleThreadLazyNullResult() {
        Lazy lazy = LazyFactory.createMultipleThreadLazy(() -> (String)null);
        assertEquals(null, lazy.get());
    }

    @Test
    public void testSingleThreadLazyOnlyOnce() {
        Lazy lazy = LazyFactory.createSingleThreadLazy(checkedSupplier);
        assertEquals(250000, (int)lazy.get());
        assertEquals(250000, (int)lazy.get());
    }

    @Test
    public void testMultipleThreadLazyOnlyOnce() {
        Lazy lazy = LazyFactory.createMultipleThreadLazy(checkedSupplier);
        assertEquals(250000, (int)lazy.get());
        assertEquals(250000, (int)lazy.get());
    }

    @Test
    public void testSingleThreadLaziness() {
        for (int i = 0; i < 10; i++) {
            LazyFactory.createSingleThreadLazy(shouldNotBeCalled);
        }
    }

    @Test
    public void testMultipleThreadLaziness()  throws InterruptedException {
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
    public void testMultipleThreadLazyDataRaces() throws InterruptedException {
        Thread threads[] = new Thread[1000];
        final boolean fail[] = new boolean[1];
        fail[0] = false;
        Lazy<Integer> lazy = LazyFactory.createMultipleThreadLazy(new Supplier<Integer>() {
            boolean wasCalled = false;

            @Override
            public Integer get() {
                if (wasCalled) {
                    fail[0] = true;
                    return 0;
                }
                wasCalled = true;

                int result = 0;
                for (int i = 1; i < 1000; i++) {
                    result += Math.sqrt(i + result);
                }
                return result;
            }
        });

        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        if (250000 != (int)lazy.get()) {
                            fail[0] = true;
                        }
                    }
                }
            });
        }

        for (int i = 100; i < 1000; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    Lazy lazy = LazyFactory.createMultipleThreadLazy(new Supplier<Integer>() {
                                boolean wasCalled = false;

                                @Override
                                public Integer get() {
                                    if (wasCalled) {
                                        fail[0] = true;
                                        return 0;
                                    }
                                    wasCalled = true;

                                    int result = 0;
                                    for (int i = 1; i < 800; i++) {
                                        result += Math.sqrt(i + result);
                                    }
                                    return result;
                                }
                            });
                    for (int i = 0; i < 100; i++) {
                        if (160000 != (int)lazy.get()) {
                            fail[0] = true;
                        }
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

        if (fail[0]) {
            fail();
        }
    }
}
