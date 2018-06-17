package ru.spbau.fedorov.architectures.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.architectures.protocol.ChangingParameter;
import ru.spbau.fedorov.architectures.protocol.Signals;
import ru.spbau.fedorov.architectures.server.ServerStarter;
import ru.spbau.fedorov.architectures.util.GarbageCollectorRunner;
import ru.spbau.fedorov.architectures.util.TestInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ClientRunner {
    @NotNull
    public static List<TestInfo> start(@NotNull String ip, int elements, int clients, int pause, int queries,
                                       int changingParameter, int step, int iterations, int architecture) throws InterruptedException, IOException {

        List<TestInfo> result = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            GarbageCollectorRunner.gc();

            Socket socket = new Socket(ip, ServerStarter.getPORT());

            System.out.println("Iteration " + i + " started");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeInt(Signals.KNOCK_KNOCK);
            out.writeInt(clients);
            out.writeInt(queries);
            out.writeInt(architecture);

            if (in.readInt() != Signals.READY) {
                throw new RuntimeException("Server is broken");
            }

            System.out.println("Clients started sending");

            AtomicLong time = new AtomicLong(0);

            Thread threads[] = new Thread[clients];

            int e = elements;
            int p = pause;

            AtomicBoolean fail = new AtomicBoolean(false);

            for (int t = 0; t < clients; t++) {
                threads[t] = new Thread(() -> {
                    try {
                        time.addAndGet(Client.run(ip, e, p, queries));
                    } catch (Exception ex) {
                        fail.set(true);
                    }
                });
            }

            for (Thread thread : threads) {
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            int res = in.readInt();
            if (res != Signals.OK) {
                throw new RuntimeException("Something bad happened on server");
            }

            double averageServerQuery = in.readDouble();
            double averageServerClient = in.readDouble();
            double averageClientQuery = time.get() / (double) clients / (double) queries;

            System.out.println("average query time on server: "  + averageServerQuery);
            System.out.println("average query time on client: "  + averageClientQuery);
            System.out.println("average client time: "  + averageServerClient);

            socket.close();

            result.add(TestInfo.of(clients, queries, elements, pause,
                    averageServerQuery, averageServerClient, averageClientQuery));

            switch (changingParameter) {
                case ChangingParameter.CLIENT_NUMBER:
                    clients += step;
                    break;

                case ChangingParameter.ELEMENT_NUMBER:
                    elements += step;
                    break;

                default:
                    pause += step;
            }

            Thread.sleep(2000);
        }

        return result;
    }
}
