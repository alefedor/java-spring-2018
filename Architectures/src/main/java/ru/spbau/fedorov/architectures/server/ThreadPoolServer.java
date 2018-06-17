package ru.spbau.fedorov.architectures.server;

import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.architectures.util.ArrayHandler;
import ru.spbau.fedorov.architectures.util.Statistics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolServer extends Server {

    public ThreadPoolServer(@NotNull Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public Statistics start(int clients, int queries) throws IOException, InterruptedException {
        ServerSocket listener = new ServerSocket(PORT);
        ExecutorService executor = Executors.newFixedThreadPool(4);

        Thread[] threads = new Thread[clients];
        ExecutorService[] executors = new ExecutorService[clients];

        ready();

        AtomicBoolean fail = new AtomicBoolean(false);

        for (int i = 0; i < clients; i++) {
            Socket socket = listener.accept();

            executors[i] = Executors.newSingleThreadExecutor();
            final ExecutorService responser = executors[i];
            AtomicInteger handled = new AtomicInteger(0);

            threads[i] = new Thread(() -> {
                startClient();
                try {
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    for (int t = 0; t < queries; t++) {
                        int size = in.readInt();
                        int arr[] = ArrayHandler.readArray(in, size);
                        startQuery();
                        executor.submit(() -> {
                           sort(arr);
                           responser.submit(() -> {
                                try {
                                    endQuery();
                                    ArrayHandler.writeArray(out, arr);
                                    if (handled.incrementAndGet() == queries) {
                                        endClient();
                                        try {
                                            socket.close();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            System.out.println("Failed to close socket\n");
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    fail.set(true);
                                }
                           });
                        });
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    fail.set(true);
                }
            });

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        executor.shutdown();

        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        for (ExecutorService e : executors) {
            e.shutdown();
        }

        for (ExecutorService e : executors) {
            e.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }

        listener.close();

        if (fail.get()) {
            fail();
            return null;
        }


        return Statistics.of(querySum.get() / (double)queries / (double) clients, clientSum.get() / (double)clients);
    }
}
