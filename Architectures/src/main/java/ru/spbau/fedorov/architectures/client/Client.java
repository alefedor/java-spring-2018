package ru.spbau.fedorov.architectures.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.architectures.server.Server;
import ru.spbau.fedorov.architectures.util.ArrayHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Client {
    public static long run(@NotNull String ip, int elements, int pause, int queries) throws IOException, InterruptedException {
        long querySum = 0;
        Socket socket = new Socket(ip, Server.getPORT());

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        for (int i = 0; i < queries; i++) {
            querySum -= System.currentTimeMillis();

            int[] query = generateQuery(elements);

            ArrayHandler.writeArray(out, query);

            int size = in.readInt();
            int result[] = ArrayHandler.readArray(in, size);

            querySum += System.currentTimeMillis();

            if (result.length != elements || !checkSorted(result)) {
                System.out.println("Incorrect answer");
                throw new RuntimeException("Incorrect answer");
            }

            Thread.sleep(pause);
        }

        socket.close();

        return querySum;
    }

    private static int[] generateQuery(int size) {
        Random rand = new Random();
        int result[] = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = i;
        }

        for (int i = size - 1; i >= 1; i--) {
            int r = rand.nextInt();
            r %= (i + 1);
            if (r < 0) {
                r += i + 1;
            }


            int tmp = result[r];
            result[r] = result[i];
            result[i] = tmp;
        }

        return result;
    }

    private static boolean checkSorted(@NotNull int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != i) {
                return false;
            }
        }

        return true;
    }
}
