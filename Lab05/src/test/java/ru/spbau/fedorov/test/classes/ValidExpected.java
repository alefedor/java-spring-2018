package ru.spbau.fedorov.test.classes;

import ru.spbau.fedorov.algo.annotations.Test;

public class ValidExpected {
    @Test(expected = RuntimeException.class)
    public void test1() {
        throw new RuntimeException();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2() {
        throw new IllegalArgumentException();
    }
}
