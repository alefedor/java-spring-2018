package ru.spbau.fedorov.tictactoe.Logic;

import org.jetbrains.annotations.Nullable;
import ru.spbau.fedorov.tictactoe.Statistics.GameInfo;

public class Model {
    private Sign[][] board = new Sign[3][3];


    public Model() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++){
               board[i][j] = Sign.N;
            }
    }

    public boolean makeMove(int row, int column, boolean isX) {
        if (!board[row][column].equals(Sign.N)) {
            return false;
        }
        board[row][column] = (isX ? Sign.X : Sign.O);

        return true;
    }

    public boolean gameEnded() {
        return getGameResult() != null;
    }

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

    private GameInfo.GameResult gameResultBySign(Sign s) {
        if (s.equals(Sign.O)) {
            return GameInfo.GameResult.Lose;
        } else {
            return GameInfo.GameResult.Win;
        }
    }

    private enum Sign {
        O,
        X,
        N
    }
}
