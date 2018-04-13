package ru.spbau.fedorov.algo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

/**
 * Interface for class computation md5 hash of directory
 */
public interface Md5Calculator {
    /**
     * Computes md5 hash of directory.
     * @param path path to the directory
     * @return hash
     * @throws IOException when errors on reading from files
     * @throws NoSuchAlgorithmException when md5 algorith isn't available
     * @throws Md5Exception when something bad happened in helper threads or when could not read file
     */
    byte[] getMD5(@NotNull Path path) throws IOException, NoSuchAlgorithmException, Md5Exception;
}
