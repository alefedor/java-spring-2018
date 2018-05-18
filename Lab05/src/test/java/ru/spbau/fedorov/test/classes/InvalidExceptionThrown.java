package ru.spbau.fedorov.test.classes;

import ru.spbau.fedorov.algo.annotations.Test;

public class InvalidExceptionThrown {
    @Test
    public void test() {
        throw new IllegalArgumentException();
    }
}
