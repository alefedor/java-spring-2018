package ru.spbau.fedorov.algo;

import java.util.LinkedList;
import java.util.List;

/**
 * Multithread queue
 * @param <T> type to store
 */
public class ConcurrentQueue<T> {
    private List<T> queue = new LinkedList<T>();

    /**
     * Add element to queue.
     * @param elem element to add
     */
    public synchronized void add(T elem) {
        queue.add(elem);
        if (queue.size() == 1) {
            notify();
        }
    }

    /**
     * Returns and deletes a task from queue.
     * If no task is available, waits for it.
     * @throws InterruptedException if wait is interrupted
     */
    public synchronized T take() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }

        return queue.remove(0);
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}
