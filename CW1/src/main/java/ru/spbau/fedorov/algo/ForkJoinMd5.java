package ru.spbau.fedorov.algo;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class ForkJoinMd5 implements Md5Calculator {

    private final AtomicBoolean failed = new AtomicBoolean(false);

    /**
     * {@link Md5Calculator}
     */
    public byte[] getMD5(@NotNull Path path) throws IOException, NoSuchAlgorithmException, Md5Exception {
        if (!Files.exists(path)) {
            throw new FileNotFoundException();
        }

        if (!Files.isDirectory(path)) {
            throw new Md5Exception("Not a directory");
        }

        Md5Task task = new Md5Task(path);
        ForkJoinPool pool = new ForkJoinPool();
        byte[] result = pool.invoke(task);

        if (failed.get()) {
            throw new Md5Exception("Could not read file");
        }

        return result;
    }

    private class Md5Task extends RecursiveTask<byte[]> {

        private final Path path;

        Md5Task(@NotNull Path path) {
            this.path = path;

        }

        @Override
        protected byte[] compute() {
            try {
                if (Files.isDirectory(path)) {
                    MessageDigest resultMd5 = MessageDigest.getInstance("MD5");
                    resultMd5.update(path.getFileName().toString().getBytes());

                    List<Md5Task> subtasks = new ArrayList<>();
                    Files.list(path).forEach((p) -> subtasks.add(new Md5Task(p)));

                    for (Md5Task subtask : subtasks) {
                        subtask.fork();
                    }

                    for (Md5Task subtask : subtasks) {
                        resultMd5.update(subtask.join());
                    }

                    return resultMd5.digest();
                } else {
                    return getMd5FromFile(path);
                }
            } catch(Exception e) {
                failed.set(true);
            }
            return new byte[0];
        }


    }

    @NotNull
    private byte[] getMd5FromFile(@NotNull Path path) throws NoSuchAlgorithmException, IOException {
        MessageDigest resultMd5 = MessageDigest.getInstance("MD5");
        try (DigestInputStream in = new DigestInputStream(new FileInputStream(path.toFile()), resultMd5)) {
            while (in.read() != -1);
        }
        return resultMd5.digest();
    }
}
