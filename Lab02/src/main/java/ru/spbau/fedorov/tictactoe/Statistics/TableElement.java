package ru.spbau.fedorov.tictactoe.Statistics;

import javafx.beans.property.SimpleStringProperty;
import org.jetbrains.annotations.NotNull;

public class TableElement {
    private final SimpleStringProperty gameMode;
    private final SimpleStringProperty gameResult;

    public TableElement(@NotNull String gameMode, @NotNull String gameResult) {
        this.gameMode = new SimpleStringProperty(gameMode);
        this.gameResult = new SimpleStringProperty(gameResult);
    }

    public String getGameMode() {
        return gameMode.get();
    }

    public String getGameResult() {
        return gameResult.get();
    }
}

