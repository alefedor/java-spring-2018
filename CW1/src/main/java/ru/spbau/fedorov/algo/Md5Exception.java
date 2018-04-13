package ru.spbau.fedorov.algo;

/**
 * Exception which is throw when something bad happened in helper threads or when could not read file.
 */
public class Md5Exception extends Exception {
    public Md5Exception(String message) {
        super(message);
    }
}
