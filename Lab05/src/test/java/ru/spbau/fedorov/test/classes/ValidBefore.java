package ru.spbau.fedorov.test.classes;

import ru.spbau.fedorov.algo.annotations.Before;
import ru.spbau.fedorov.algo.annotations.Test;
import lombok.Getter;

public class ValidBefore {
    @Getter private static int cnt1 = 0;
    @Getter private static int cnt2 = 0;

    @Before
    public void before1() {
        cnt1++;
    }

    @Before
    public void before2() {
        cnt2++;
    }

    @Test
    public void test1() {
        //empty
    }

    @Test
    public void test2() {
        //different from the previous one
    }

    @Test(ignore = "don't like this test")
    public void test3() {
        //new way of creating tests
    }
}
