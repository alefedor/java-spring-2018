package ru.spbau.fedorov.test.classes;

import ru.spbau.fedorov.algo.annotations.Test;

public class InvalidExpectedButSuccess {
    @Test(expected = RuntimeException.class)
    public void test() {
        //succesfully doing nothing
    }
}
