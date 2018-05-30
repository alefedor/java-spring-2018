package ru.spbau.fedorov.tictactoe.logic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.fedorov.tictactoe.statistics.GameInfo;

/**
 * Class with logic for tic-tac-toe game.
 */
public class Model {
    private static final int BOARD_SIZE = 3;
    private Sign[][] board = new Sign[BOARD_SIZE][BOARD_SIZE];

    /**
     * Constructs Model with 3x3 board.
     */
    public Model() {
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++){
               board[i][j] = Sign.EMPTY;
            }
    }

    /**
     * Size of board for game
     */
    public int getBoardSize() {
        return BOARD_SIZE;
    }

    /**
     * @return true if cell is empty
     */
    public boolean canMakeMove(int id) {
        return board[id / 3][id % 3].equals(Sign.EMPTY);
    }

    /**
     * Makes move into a specific cell
     * @param row number of row of the cell
     * @param column number of column of the cell
     * @param isX does X makes move this turn
     * @return true if move is correct
     */
    public boolean makeMove(int row, int column, boolean isX) {
        if (!board[row][column].equals(Sign.EMPTY)) {
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
        GameInfo.GameResult result;

        result = getMatchingInColumns();
        if (result != null) {
            return result;
        }

        result = getMatchingInRows();
        if (result != null) {
            return result;
        }


        result = getMatchingOnDiagonal();
        if (result != null) {
            return result;
        }

        result = getMatchingOnBackDiagonal();
        if (result != null) {
            return result;
        }

        boolean hasEmpty = isNotFull();

        if (hasEmpty) {
            return null;
        } else {
            return GameInfo.GameResult.DRAW;
        }
    }

    /**
     * Get game result. It is guaranteed that if gameEnded then GameNotEndedException wouldn't be thrown.
     * @return GameResult of the finished game
     * @throws GameNotEndedException if game not ended yet.
     */
    @NotNull
    public GameInfo.GameResult getFinalGameResult() throws GameNotEndedException {
        GameInfo.GameResult result = getGameResult();

        if (result == null) {
            throw new GameNotEndedException("Game still in process");
        }

        return result;
    }

    private boolean isNotFull() {
        boolean hasEmpty = false;
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++) {
                hasEmpty |= board[i][j].equals(Sign.EMPTY);
            }
        return hasEmpty;
    }

    /**
     * Check for end of game on main diagonal.
     * Returns null if nothing has been matched
     */
    @Nullable
    private GameInfo.GameResult getMatchingOnDiagonal() {
        if (!board[0][0].equals(Sign.EMPTY)) {
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

        return null;
    }

    /**
     * Check for end of game on back diagonal.
     * Returns null if nothing has been matched
     */
    @Nullable
    private GameInfo.GameResult getMatchingOnBackDiagonal() {
        if (!board[0][2].equals(Sign.EMPTY)) {
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

        return null;
    }

    /**
     * Check for end of game in columns.
     * Returns null if nothing has been matched
     */
    @Nullable
    private GameInfo.GameResult getMatchingInColumns() {
        for (int i = 0; i < 3; i++) {
            if (board[0][i].equals(Sign.EMPTY)) {
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

        return null;
    }

    /**
     * Check for end of game in rows.
     * Returns null if nothing has been matched
     */
    @Nullable
    private GameInfo.GameResult getMatchingInRows() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(Sign.EMPTY)) {
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

        return null;
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
            return GameInfo.GameResult.LOSE;
        } else {
            return GameInfo.GameResult.WIN;
        }
    }

    public enum Sign {
        O,
        X,
        EMPTY
    }
}
