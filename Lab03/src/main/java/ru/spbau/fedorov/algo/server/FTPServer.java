package ru.spbau.fedorov.algo.server;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

/**
 * Server for dowloading and listing files
 */
public class FTPServer {
    @Getter private static final int PORT = 1234;
    private static final int LIST = 1;
    private static final int GET = 2;
    private static final int BUFFER_SIZE = 4 * 1024;
    private static Logger logger = Logger.getGlobal();
    private static byte[] buffer = new byte[BUFFER_SIZE];
    @Setter @Getter private static volatile boolean running = false;

    /**
     * Runs server on port PORT. Listens to clients. May be interrupted for shuting down.
     * @param args Arguments for server (currently ignored)
     * @throws IOException when IO fails
     */
    public static void main(String[] args) throws IOException {
        running = true;

        logger.info("Server has started working");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            serverSocket.setSoTimeout(1000);

            while (!Thread.interrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    Thread newThread = new Thread(() -> {
                        try {
                            processSocket(socket);
                            socket.close();
                        } catch (IOException e) {
                            logger.warning("Error in connection: " + e.getMessage());
                        }
                    });

                    newThread.setDaemon(false);
                    newThread.start();

                } catch (SocketTimeoutException e) {
                    //check for interrupted
                }
            }
        }

        logger.info("Server has stopped working");

        running = false;
    }

    /**
     * Works with a connection. Parses query and responds.
     * @param socket connection to work with
     * @throws IOException when IO fails
     */
    private static void processSocket(@NotNull Socket socket) throws IOException {
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {

            socket.setSoTimeout(2000);

            int query;
            String path;


            query = inputStream.readInt();
            switch (query) {
                case LIST:
                    path = inputStream.readUTF();
                    logger.info("List query: " + path);
                    sendList(path, outputStream);
                    break;

                case GET:
                    path = inputStream.readUTF();
                    logger.info("Get query: " + path);
                    sendFile(path, outputStream);
                    break;

                default:
                    logger.warning("Incorrect query: " + query);
            }
        } catch (SocketTimeoutException e) {
            // End of connection
        }
    }

    /**
     * Sends list of files in path
     * @param path where to search
     * @param output where to send result
     * @throws IOException when IO fails
     */
    private static void sendList(@NotNull String path, @NotNull DataOutputStream output) throws IOException {
        File[] files = new File(path).listFiles();
        if (files == null) {
            output.writeInt(0);
            return;
        }

        output.writeInt(files.length);

        for (File file : files) {
            output.writeUTF(file.getName());
            output.writeBoolean(file.isDirectory());
        }

        output.flush();
    }

    /**
     * Sends file determined by path
     * @param path where to search
     * @param output where to send result
     * @throws IOException when IO fails
     */
    private static void sendFile(@NotNull String path, @NotNull DataOutputStream output) throws IOException {
        File file = new File(path);

        if (!file.exists() || file.isDirectory()) {
            output.writeLong(0);
            return;
        }

        output.writeLong(file.length());

        try (FileInputStream input = new FileInputStream(path)) {
            int num;
            while ((num = input.read(buffer)) != -1) {
                output.write(buffer, 0, num);
            }
        }

        output.flush();
    }
}
