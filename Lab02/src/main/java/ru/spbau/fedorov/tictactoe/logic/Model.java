package ru.spbau.fedorov.tictactoe.logic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.fedorov.tictactoe.statistics.GameInfo;

/**
 * Class with logic for tic-tac-toe game.
 */
public class Model {
    private Sign[][] board = new Sign[3][3];


    /**
     * Constructs Model with 3x3 board.
     */
    public Model() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++){
               board[i][j] = Sign.N;
            }
    }

    /**
     * @return true if cell is empty
     */
    public boolean canMakeMove(int id) {
        return board[id / 3][id % 3].equals(Sign.N);
    }

    /**
     * Makes move into a specific cell
     * @param row number of row of the cell
     * @param column number of column of the cell
     * @param isX does X makes move this turn
     * @return true if move is correct
     */
    public boolean makeMove(int row, int column, boolean isX) {
        if (!board[row][column].equals(Sign.N)) {
            return false;
        }
        board[row][column] = (isX ? Sign.X : Sign.O);

        return true;
    }

    /**
     * @return true if the game ended
     */
    public boolean gameEnded() {
        return getGameResult() != null;
    }

    /**
     * @return GameResult of the finished game. If game is not finished yet, returns null
     */
    @Nullable
    public GameInfo.GameResult getGameResult() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(Sign.N)) {
                continue;
            }
            boolean sequence =  true;
            for (int j = 1; j < 3; j++) {
                if (!board[i][j].equals(board[i][0])) {
                    sequence = false;
                }
            }

            if (sequence) {
                return gameResultBySign(board[i][0]);
            }
        }

        for (int i = 0; i < 3; i++) {
            if (board[0][i].equals(Sign.N)) {
                continue;
            }
            boolean sequence =  true;
            for (int j = 1; j < 3; j++) {
                if (!board[j][i].equals(board[0][i])) {
                    sequence = false;
                }
            }

            if (sequence) {
                return gameResultBySign(board[0][i]);
            }
        }

        if (!board[0][0].equals(Sign.N)) {
            boolean sequence =  true;
            for (int j = 1; j < 3; j++) {
                if (!board[j][j].equals(board[0][0])) {
                    sequence = false;
                }
            }

            if (sequence) {
                return gameResultBySign(board[0][0]);
            }
        }

        if (!board[0][2].equals(Sign.N)) {
            boolean sequence =  true;
            for (int j = 1; j < 3; j++) {
                if (!board[j][2 - j].equals(board[0][2])) {
                    sequence = false;
                }
            }

            if (sequence) {
                return gameResultBySign(board[0][2]);
            }
        }

        boolean hasEmpty = false;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                hasEmpty |= board[i][j].equals(Sign.N);
            }

        if (hasEmpty) {
            return null;
        } else {
            return GameInfo.GameResult.Draw;
        }
    }

    /**
     * Get 3x3 board for tic-tac-toe
     * @return the 3x3 board
     */
    @NotNull
    public Sign[][] getBoard() {
        return board;
    }

    private GameInfo.GameResult gameResultBySign(Sign s) {
        if (s.equals(Sign.O)) {
            return GameInfo.GameResult.Lose;
        } else {
            return GameInfo.GameResult.Win;
        }
    }

    public enum Sign {
        O,
        X,
        N
    }
}
