package ru.spbau.fedorov.tictactoe.bot;

import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.tictactoe.logic.Model;

/**
 * Never loosing bot for tic-tac-toe
 */
public class HardBot implements Bot {
    private static final int ALLY_COEFF = 5;
    private static final int ENEMY_COEFF = 4;

    private final Model.Sign[][] board;
    private final int boardSize;


    /**
     * Constructs a bot.
     * @param model game model for which the bot is created
     */
    public HardBot(@NotNull Model model) {
        this.board = model.getBoard();
        boardSize = model.getBoardSize();

    }

    @Override
    public int getMove() {
        int maxMoveNum = boardSize * boardSize;
        int pos = -1;
        int best = -1;
        for (int i = 0; i < maxMoveNum; i++)
            if (board[i / boardSize][i % boardSize].equals(Model.Sign.EMPTY)) {
                int priority = getPriority(i / boardSize, i % boardSize);
                if (best < priority) {
                    best = priority;
                    pos = i;
                }
            }
        return pos;
    }

    /**
     * Estimates goodness of cell
     * @param row number of row in which the cell is
     * @param column number of column in which the cell is
     * @return int representing estimate of goodness of cell (large is better)
     */
    private int getPriority(int row, int column) {
        int priority = 0;
        if (row == 1 && column == 1) {
            priority += ALLY_COEFF;
        }

        priority += checkPriority(row, 0, row, 1, row, 2); // same row
        priority += checkPriority(0, column,  1, column, 2, column); // same column

        if (row == column) { // on main diagonal
            priority += checkPriority(0, 0, 1, 1, 2, 2);
        }

        if (row == 2 - column) { // on back diagonal
            priority += checkPriority(0, 2, 1, 1, 2, 0);
        }

        return priority;
    }

    private int checkPriority(int x1, int y1, int x2, int y2, int x3, int y3) {
        return checkForSelfPlans(x1, y1, x2, y2, x3, y3) + checkForRuiningEnemyPlans(x1, y1, x2, y2, x3, y3);
    }

    private int checkForSelfPlans(int x1, int y1, int x2, int y2, int x3, int y3) {
        if (getNumber(x1, y1, x2, y2, x3, y3, Model.Sign.X) > 0) {
            return 0;
        }

        int num = getNumber(x1, y1, x2, y2, x3, y3, Model.Sign.O);

        int result = 1;
        for (int i = 0; i < num; i++) {
            result *= ALLY_COEFF;
        }

        return result;
    }

    private int checkForRuiningEnemyPlans(int x1, int y1, int x2, int y2, int x3, int y3) {
        if (getNumber(x1, y1, x2, y2, x3, y3, Model.Sign.O) > 0) {
            return 0;
        }

        int num = getNumber(x1, y1, x2, y2, x3, y3, Model.Sign.X);

        int result = 1;
        for (int i = 0; i < num; i++) {
            result *= ENEMY_COEFF;
        }

        return result;
    }

    private int getNumber(int x1, int y1, int x2, int y2, int x3, int y3, Model.Sign sign) {
        int num = 0;
        if (board[x1][y1].equals(sign)) {
            num++;
        }

        if (board[x2][y2].equals(sign)) {
            num++;
        }

        if (board[x3][y3].equals(sign)) {
            num++;
        }

        return num;
    }
}
