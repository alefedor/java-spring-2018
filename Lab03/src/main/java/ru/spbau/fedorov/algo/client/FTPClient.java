package ru.spbau.fedorov.algo.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.fedorov.algo.data.FileEntry;
import ru.spbau.fedorov.algo.server.FTPServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Client for FTPServer. Can ask for list of files and download a file.
 */
public class FTPClient {
    private static final int BUFFER_SIZE = 4 * 1024;
    private byte buffer[] = new byte[BUFFER_SIZE];
    private final String host;

    /**
     * Creates client attached to a specific host
     * @param host host to be attached to
     * @throws IOException when IO fails
     */
    public FTPClient(@NotNull String host) throws IOException {
        this.host = host;
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

            outputStream.writeInt(1);
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
     * @throws IOException
     */
    public boolean get(@NotNull String path, @NotNull OutputStream destination) throws IOException {
        try (Socket socket = new Socket(host, FTPServer.getPORT())) {

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeInt(2);
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
