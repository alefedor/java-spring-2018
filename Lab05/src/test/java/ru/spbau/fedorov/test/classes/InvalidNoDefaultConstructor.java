package ru.spbau.fedorov.test.classes;

import ru.spbau.fedorov.algo.annotations.Test;

public class InvalidNoDefaultConstructor {
    private InvalidNoDefaultConstructor(int a) {

    }

    @Test
    public void test() {
        //empty
    }
}
