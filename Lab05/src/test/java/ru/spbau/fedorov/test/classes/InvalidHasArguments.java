package ru.spbau.fedorov.test.classes;

import ru.spbau.fedorov.algo.annotations.Test;

public class InvalidHasArguments {
    @Test
    public void test(int i, Object o) {
        //empty
    }
}
