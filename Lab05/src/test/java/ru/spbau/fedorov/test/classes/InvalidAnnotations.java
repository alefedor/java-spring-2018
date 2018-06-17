package ru.spbau.fedorov.test.classes;

import ru.spbau.fedorov.algo.annotations.Before;
import ru.spbau.fedorov.algo.annotations.Test;

public class InvalidAnnotations {
    @Test
    @Before
    public void test() {
        //empty
    }
}
