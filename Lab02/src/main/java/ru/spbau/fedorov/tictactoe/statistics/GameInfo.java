package ru.spbau.fedorov.tictactoe.statistics;

import org.jetbrains.annotations.NotNull;

/**
 * Class for containing results of game
 */
public class GameInfo {
    private GameMode mode;
    private GameResult result;

    /**
     * Constructs GameInfo on the finished game
     * @param mode the mode of the game
     * @param result result of the game
     */
    public GameInfo(@NotNull GameMode mode, @NotNull GameResult result) {
        this.mode = mode;
        this.result = result;
    }

    /**
     * Builds String with info about game mode of finished game
     * @return String with info about game mode
     */
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

    /**
     * Builds String with info about game results of finished game
     * @return String with info about game results
     */
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

    public enum GameResult {
        Win,
        Lose,
        Draw;

        @NotNull
        public GameResult invert() {
            if (this.equals(Win)) {
                return Lose;
            } else if (this.equals(Lose)) {
                return Win;
            }
            return Draw;
        }
    }

    public enum GameMode {
        OnePlayerEasy,
        OnePlayerHard,
        TwoPlayers
    }
}
