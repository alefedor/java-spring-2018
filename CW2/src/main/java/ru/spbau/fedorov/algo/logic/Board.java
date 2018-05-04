package ru.spbau.fedorov.algo.logic;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Board for Pair game
 */
public class Board {
    private int board[][];
    private int boardSize;

    /**
     * Constructs board for Pair game with random distribution.
     * @param boardSize size of board
     */
    public Board(int boardSize) {
        this.boardSize = boardSize;
        board = new int[boardSize][boardSize];
        List<Integer> nums = new ArrayList<Integer>();
        for (int i = 0; i < boardSize * boardSize / 2; i++) {
            for (int j = 0; j < 2; j++) {
                nums.add(i);
            }
        }
        Collections.shuffle(nums);

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = nums.get(i * boardSize + j);
            }
        }
    }

    /**
     * Retrieves number at cell (x, y)
     * @param x first coordinate
     * @param y first coordinate
     * @return number at the cell
     */
    public int get(int x, int y) {
        return board[x][y];
    }
}
