package ru.spbau.fedorov.test.classes;

import lombok.Getter;
import ru.spbau.fedorov.algo.annotations.After;
import ru.spbau.fedorov.algo.annotations.Test;

public class ValidAfter {
    @Getter private static int cnt1 = 0;
    @Getter private static int cnt2 = 0;

    @After
    public void before1() {
        cnt1++;
    }

    @After
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
