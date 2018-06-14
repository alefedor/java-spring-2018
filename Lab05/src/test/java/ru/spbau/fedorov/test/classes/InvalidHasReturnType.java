package ru.spbau.fedorov.test.classes;

import ru.spbau.fedorov.algo.annotations.Test;

public class InvalidHasReturnType {
    @Test
    public int test(int i, Object o) {
        return 1337;
    }
}
