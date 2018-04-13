package ru.spbau.fedorov.algo;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

/**
 * Application for computing md5 hash of a directory.
 * Uses single threaded algorithm and fork-join algorithm and compares them.
 */
public class Main  {
    public static void main(String[] args) {
        if (args.length == 0 || args.length > 1) {
            System.out.println("Usage: {Name of directory}");
            return;
        }

        System.out.println("Single thread:");
        Md5Calculator providerSingle = new SingleThreadMd5();
        calc(providerSingle, args[0]);

        System.out.println("Fork join:");
        Md5Calculator providerFJP = new ForkJoinMd5();
        calc(providerFJP, args[0]);
    }

    @NotNull
    private static String bytesToHex(@NotNull byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private static void calc(@NotNull Md5Calculator md5Calculator, @NotNull String path) {
        try {
            long startTime = System.currentTimeMillis();
            byte[] md5Hash = md5Calculator.getMD5(Paths.get(path));
            System.out.println(bytesToHex(md5Hash));
            long currentTime = System.currentTimeMillis();
            System.out.println(currentTime - startTime);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed");
        }
    }
}
