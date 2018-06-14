package ru.spbau.fedorov.test.classes;

import ru.spbau.fedorov.algo.annotations.Test;

public class ValidIgnored {
    @Test(ignore = "Empty test is too bad")
    public void test1() {
        //empty
    }

    @Test(ignore = "Can't see any difference with the previous one")
    public void test2() {
        //different from the previous one
    }

    @Test(expected = IllegalArgumentException.class, ignore = "I hate exceptions")
    public void test3() {
        throw new IllegalArgumentException();
    }
}
