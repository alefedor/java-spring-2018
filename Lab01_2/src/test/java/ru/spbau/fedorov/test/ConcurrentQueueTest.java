package ru.spbau.fedorov.test;

import org.junit.Test;
import ru.spbau.fedorov.algo.ConcurrentQueue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConcurrentQueueTest {
    @Test
    public void testAdd() throws InterruptedException {
        ConcurrentQueue<Integer> queue = new ConcurrentQueue<>();

        Thread threads[] = new Thread[1000];
        for (int i = 0; i < threads.length; i++) {
            int toAdd = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    queue.add(toAdd);
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

    @Test
    public void testTake() throws InterruptedException {
        ConcurrentQueue<Integer> queue = new ConcurrentQueue<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                queue.add(10);
            }
        });

        thread.start();
        thread.join();

        assertEquals(10, (int)queue.take());
        assertEquals(true, queue.isEmpty());
    }

    @Test
    public void testDataRaces() throws InterruptedException {
        ConcurrentQueue<Integer> queue = new ConcurrentQueue<>();
        ConcurrentQueue<Integer> taken = new ConcurrentQueue<>();

        Thread threads[] = new Thread[1000];
        for (int i = 0; i < threads.length; i++) {
            int toAdd = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        queue.add(toAdd * 10 + i);
                    }
                    for (int i = 0; i < 10; i++) {
                        try {
                            taken.add(queue.take());
                        } catch (Exception e) {
                            //test will fail by himself
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

        assertEquals(true, queue.isEmpty());

        List<Integer> elems = new ArrayList<>();
        while (!taken.isEmpty()) {
            elems.add(taken.take());
        }

        assertEquals(10000, elems.size());
        elems.sort(Comparator.comparingInt(a -> a));
        for (int i = 0; i < elems.size(); i++)
            if (elems.get(i) != i) {
                fail();
            }
    }
}
