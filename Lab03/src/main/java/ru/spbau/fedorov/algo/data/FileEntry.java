package ru.spbau.fedorov.algo.data;

import lombok.Data;

/**
 * Data structure for storing entries about files and directories
 */
@Data
public class FileEntry {
    private final String filename;
    private final boolean directory;

    @Override
    public String toString() {
        return filename;
    }
}
