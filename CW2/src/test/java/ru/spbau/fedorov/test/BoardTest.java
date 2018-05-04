package ru.spbau.fedorov.test;

import org.junit.Test;
import ru.spbau.fedorov.algo.logic.Board;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class BoardTest {
    private static final int BOARD_SIZE = 4;

    @Test
    public void testCorrectness() {
        Board board = new Board(BOARD_SIZE);

        Set<Integer> numbers = new HashSet<>();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int a = board.get(i, j);

                if (numbers.contains(a)) {
                    numbers.remove(a);
                } else {
                    numbers.add(a);
                }
            }
        }

        assertTrue(numbers.isEmpty());
    }
}
