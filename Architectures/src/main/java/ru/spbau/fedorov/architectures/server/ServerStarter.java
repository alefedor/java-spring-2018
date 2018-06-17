package ru.spbau.fedorov.architectures.server;

import lombok.Getter;
import ru.spbau.fedorov.architectures.protocol.ArchitectureTypes;
import ru.spbau.fedorov.architectures.protocol.Signals;
import ru.spbau.fedorov.architectures.util.GarbageCollectorRunner;
import ru.spbau.fedorov.architectures.util.Statistics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerStarter {
    @Getter
    private static final int PORT = 4444;

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket listener = new ServerSocket(PORT);
        listener.setSoTimeout(4000);
        while (!Thread.interrupted()) {

            GarbageCollectorRunner.gc();

            try {
                Socket socket = listener.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                int signal = in.readInt();
                if (signal != Signals.KNOCK_KNOCK) {
                    socket.close();
                    return;
                }

                int clients = in.readInt();
                int queries = in.readInt();
                int architecture = in.readInt();

                Server server = null;

                switch (architecture) {
                    case ArchitectureTypes.SEPARATE_THREAD:
                        server = new SeparateThreadServer(socket);
                        break;

                    case ArchitectureTypes.THREAD_POOL:
                        server = new ThreadPoolServer(socket);
                        break;

                    default:
                        server = new NonBlockingServer(socket);
                }

                Statistics stat = server.start(clients, queries);


                if (stat != null) {
                    out.writeInt(Signals.OK);
                    out.writeDouble(stat.getAverageQuery());
                    out.writeDouble(stat.getAverageClient());
                }

                socket.close();
            } catch(SocketTimeoutException e) {
                // check interrupted
            }
        }
    }
}
