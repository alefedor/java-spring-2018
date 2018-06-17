package ru.spbau.fedorov.architectures.util;

import lombok.Data;

@Data(staticConstructor = "of")
public class TestInfo {
    private final int clients;
    private final int queries;
    private final int elements;
    private final int pause;
    private final double averageServerQuery;
    private final double averageClient;
    private final double averageClientQuery;
}
