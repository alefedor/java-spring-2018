package ru.spbau.fedorov.tictactoe;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.tictactoe.Bot.Bot;
import ru.spbau.fedorov.tictactoe.Logic.Model;
import ru.spbau.fedorov.tictactoe.Statistics.GameInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Controller {
    @FXML
    private Label message;
    @FXML
    private Button twoPlayersButton;
    @FXML
    private Button onePlayerButton;
    @FXML
    private RadioButton easyBot;
    @FXML
    private RadioButton hardBot;
    @FXML
    private TableView statistics;
    @FXML
    private TableColumn mode;
    @FXML
    private TableColumn result;
    @FXML
    private GridPane board;

    private ToggleGroup botLevel;

    private Model model;
    private Bot bot;
    private GameInfo.GameMode gameMode;
    private List<GameInfo> previousGames = new ArrayList<>();
    private boolean gameOn = false;
    private boolean isX = true;

    public void initialize() {
        botLevel = new ToggleGroup();
        easyBot.setToggleGroup(botLevel);
        hardBot.setToggleGroup(botLevel);

        showGameNotStarted();

        ObservableList<Node> nodes = board.getChildren();
        for (int i = 0; i < nodes.size(); i++) {
            int id = i;
            nodes.get(i).setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (gameOn) {
                        if (model.makeMove(id / 3, id % 3, isX)) {
                            confirmMove(id, isX);

                            if (!gameOn) {
                                return;
                            }

                            if (gameMode.equals(GameInfo.GameMode.TwoPlayers)) {
                                isX = !isX;
                            } else {
                                int move = bot.getMove();

                                if (model.makeMove(move / 3, move % 3, !isX)) {
                                    confirmMove(move, !isX);
                                } else {
                                    throw new RuntimeException("Bot making incorrect moves");
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private void showGameNotStarted() {
        message.setText("Game not started yet. Click button to choose game mode.");
    }

    private void onGameStart() {
        clearBoard();
        isX = true;
        model = new Model();
        showGameStarted();
        gameOn = true;
    }

    private void showGameStarted() {
        message.setText("Game is in progress. Click on field to make move.");
    }

    private void onGameEnd(@NotNull GameInfo.GameResult result) {
        GameInfo gameInfo = new GameInfo(gameMode, result);
        gameOn = false;
        previousGames.add(gameInfo);
        message.setText(gameInfo.getGameResult());
    }

    public void newGameTwoPlayers(MouseEvent mouseEvent) {
        onGameStart();
        gameMode = GameInfo.GameMode.TwoPlayers;
    }

    public void newGameOnePlayer(MouseEvent mouseEvent) {
        onGameStart();
        String id = ((RadioButton) botLevel.getSelectedToggle()).getId();
        if (id.equals("easyBot")) {
            //bot = ...;
            gameMode = GameInfo.GameMode.OnePlayerEasy;
        } else {
            //bot = ...;
            gameMode = GameInfo.GameMode.OnePlayerHard;
        }
    }

    public void showStatistics(MouseEvent mouseEvent) {

    }

    private void clearBoard() {
        for (Node node : board.getChildren())
            if (node instanceof TextField){
                TextField text = (TextField)node;
                text.clear();
            }
    }

    private void confirmMove(int move, boolean isX) {
        TextField text = (TextField)board.getChildren().get(move);
        if (isX) {
            text.setText("X");
        } else {
            text.setText("O");
        }

        if (model.gameEnded()) {
            GameInfo.GameResult result = model.getGameResult();
            onGameEnd(result);
        }
    }
}
