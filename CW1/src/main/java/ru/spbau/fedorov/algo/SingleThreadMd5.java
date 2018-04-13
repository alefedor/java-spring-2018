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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SingleThreadMd5 implements Md5Calculator {
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

        return getMd5FromDir(path);
    }

    @NotNull
    private byte[] getMd5FromDir(@NotNull Path path) throws IOException, NoSuchAlgorithmException, Md5Exception {
        MessageDigest resultMd5 = MessageDigest.getInstance("MD5");
        resultMd5.update(path.getFileName().toString().getBytes());

        final AtomicBoolean failed = new AtomicBoolean(false);

        Consumer<Path> updater = (p) -> {
            try {
                if (Files.isDirectory(p)) {
                    resultMd5.update(getMd5FromDir(p));
                } else {
                    resultMd5.update(getMd5FromFile(p));
                }
            } catch (Exception e) {
                failed.set(true);
            }
        };

        if (failed.get()) {
            throw new Md5Exception("Could not read file");
        }

        Files.list(path).forEach(updater);

        return resultMd5.digest();
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
