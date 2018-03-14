package ru.spbau.fedorov.tictactoe.Statistics;

import org.jetbrains.annotations.NotNull;

public class GameInfo {
    public enum GameResult {
        Win,
        Lose,
        Draw
    }

    public enum GameMode {
        OnePlayerEasy,
        OnePlayerHard,
        TwoPlayers
    }

    private GameMode mode;
    private GameResult result;

    public GameInfo(@NotNull GameMode mode, @NotNull GameResult result) {
        this.mode = mode;
        this.result = result;
    }

    @NotNull
    public String getGameMode() {
        if (mode.equals(GameMode.OnePlayerEasy)) {
            return "Game with an easy bot";
        } else if (mode.equals(GameMode.OnePlayerHard)) {
            return "Game with an hard bot";
        } else {
            return "Game with another player";
        }
    }

    @NotNull
    public String getGameResult() {
        if (result.equals(GameResult.Draw)) {
            return "Played in a draw";
        } else if (result.equals(GameResult.Win)) {
            if (mode.equals(GameMode.OnePlayerHard) || mode.equals(GameMode.OnePlayerEasy)) {
                return "Was a winner";
            } else {
                return "First player won";
            }
        } else {
            if (mode.equals(GameMode.OnePlayerHard) || mode.equals(GameMode.OnePlayerEasy)) {
                return "Losed the match";
            } else {
                return "Second player won";
            }
        }
    }
}
