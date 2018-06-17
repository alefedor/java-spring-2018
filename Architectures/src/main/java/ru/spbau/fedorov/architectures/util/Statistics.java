package ru.spbau.fedorov.architectures.util;

import lombok.Data;

@Data(staticConstructor = "of")
public class Statistics {
    private final double averageQuery;
    private final double averageClient;
}
