package ru.spbau.fedorov.architectures.server;

import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.architectures.util.ArrayHandler;
import ru.spbau.fedorov.architectures.util.Statistics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class SeparateThreadServer extends Server {

    SeparateThreadServer(@NotNull Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public Statistics start(int clients, int queries) throws IOException, InterruptedException {
        ServerSocket listener = new ServerSocket(PORT);
        Thread threads[] = new Thread[clients];

        AtomicBoolean fail = new AtomicBoolean(false);

        ready();

        for (int i = 0; i < clients; i++) {
            Socket socket = listener.accept();
            threads[i] = new Thread(() -> {
                startClient();

                try {
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    for (int t = 0; t < queries; t++) {
                        int size = in.readInt();
                        int arr[] = ArrayHandler.readArray(in, size);
                        startQuery();
                        sort(arr);
                        endQuery();
                        ArrayHandler.writeArray(out, arr);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    fail.set(true);
                } finally {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Failed to close socket\n");
                    }
                }

                endClient();
            });

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        listener.close();

        if (fail.get()) {
            fail();
            return null;
        }

        return Statistics.of(querySum.get() / (double)queries / (double)clients, clientSum.get() / (double)clients);
    }
}
