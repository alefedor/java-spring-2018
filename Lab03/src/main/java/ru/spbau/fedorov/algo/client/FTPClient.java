package ru.spbau.fedorov.algo.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.fedorov.algo.data.FileEntry;
import ru.spbau.fedorov.algo.server.FTPServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Client for FTPServer. Can ask for list of files and download a file.
 */
public class FTPClient {
    private static final int BUFFER_SIZE = 4 * 1024;
    private byte buffer[] = new byte[BUFFER_SIZE];
    private final String host;
    private static final String USAGE = "ARGS: (server hostname : String)";
    private static final String COMMAND_USAGE = "get/list <path : String> or exit";

    /**
     * Creates client attached to a specific host
     * @param host host to be attached to
     * @throws IOException when IO fails
     */
    public FTPClient(@NotNull String host) throws IOException {
        this.host = host;
    }

    /**
     * Runs client connecting to server on port FTPServer.PORT
     * @param args Arguments for server. Should consist of one string -- localhost
     * @throws IOException when IO fails
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(USAGE);
            return;
        }

        try {
            FTPClient client = new FTPClient(args[0]);
            handleQueries(new Scanner(System.in), new PrintWriter(System.out), client);
        } catch (IOException e) {
            System.out.println("Fail in IO");
            e.printStackTrace();
        }
    }

    private static void handleQueries(@NotNull Scanner scanner, @NotNull PrintWriter writer, @NotNull FTPClient client) throws IOException {
        while (scanner.hasNext()) {
            String command = scanner.nextLine();
            String words[] = command.split(" ");

            if (words.length == 1 && words[0].equals("exit")) {
                break;
            }

            if (words.length == 2) {
                switch (words[0]) {
                    case "list":
                        writer.println("Going to path " + words[1]);
                        List<FileEntry> result = client.list(words[1]);
                        if (result == null) {
                            writer.println("No such file");
                        } else {
                            for (FileEntry entry : result) {
                                writer.println((entry.isDirectory() ? "Directory" : "File") + " " + entry.getFilename());
                            }
                        }
                        break;

                    case "get":
                        writer.println("Getting file from path " + words[1]);
                        String filename = new File(words[1]).getName();
                        if (client.get(words[1], new FileOutputStream(filename))) {
                            writer.println("File was downloaded in the current dir");
                        } else {
                            writer.println("No such file");
                        }
                        break;

                    default:
                        writer.println(COMMAND_USAGE);
                }
            } else {
                writer.println(COMMAND_USAGE);
            }
        }
    }


    /**
     * Asks server for list of files.
     * @param path directory path where to list files
     * @return entries about files (names and is or not dir)
     * @throws IOException when IO fails
     */
    @Nullable
    public List<FileEntry> list(@NotNull String path) throws IOException {
        try (Socket socket = new Socket(host, FTPServer.getPORT())){

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeInt(FTPServer.QueryType.LIST);
            outputStream.writeUTF(path);
            outputStream.flush();

            int num = inputStream.readInt();

            if (num == 0) {
                return null;
            }

            List<FileEntry> result = new ArrayList<FileEntry>();

            for (int i = 0; i < num; i++) {
                String filename = inputStream.readUTF();
                boolean isDir = inputStream.readBoolean();
                result.add(new FileEntry(filename, isDir));
            }

            return result;
        }
    }

    /**
     * Downloads file from server.
     * @param path path to file
     * @param destination where to write content of file.
     * @return true if there was file in path
     * @throws IOException when IO fails
     */
    public boolean get(@NotNull String path, @NotNull OutputStream destination) throws IOException {
        try (Socket socket = new Socket(host, FTPServer.getPORT())) {

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeInt(FTPServer.QueryType.GET);
            outputStream.writeUTF(path);
            outputStream.flush();

            long size = inputStream.readLong();

            if (size == 0) {
                return false;
            }

            long loaded = 0;
            int num;

            while (loaded != size && (num = inputStream.read(buffer)) != -1) {
                loaded += num;
                destination.write(buffer, 0, num);
            }

            if (loaded != size) {
                throw new FileTransferException();
            }

            return true;
        }
    }

    private class FileTransferException extends IOException {
        public FileTransferException() {
            super("File was transfered with some missing or extra information");
        }
    }
}
