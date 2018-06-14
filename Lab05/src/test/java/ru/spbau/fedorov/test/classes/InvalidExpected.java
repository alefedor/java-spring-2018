package ru.spbau.fedorov.test.classes;

import ru.spbau.fedorov.algo.annotations.Test;

public class InvalidExpected {
    @Test(expected = RuntimeException.class)
    public void test() {
        throw new IllegalArgumentException();
    }
}
