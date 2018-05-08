package ru.spbau.fedorov.tictactoe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import ru.spbau.fedorov.tictactoe.bot.Bot;
import ru.spbau.fedorov.tictactoe.bot.EasyBot;
import ru.spbau.fedorov.tictactoe.bot.HardBot;
import ru.spbau.fedorov.tictactoe.logic.GameNotEndedException;
import ru.spbau.fedorov.tictactoe.logic.Model;
import ru.spbau.fedorov.tictactoe.statistics.GameInfo;
import ru.spbau.fedorov.tictactoe.statistics.TableElement;

/**
 * Controller for tic-tac-toe javafx application
 */
public class Controller {
    private static final String EASYBOT = "easyBot";

    @FXML
    private Button stats;
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
    private TableView<TableElement> statistics;
    @FXML
    private TableColumn<TableElement, String> modeColumn;
    @FXML
    private TableColumn<TableElement, String> resultColumn;
    @FXML
    private GridPane board;

    private final ObservableList<TableElement> tableList = FXCollections.observableArrayList();

    private ToggleGroup botLevel;

    private Model model;
    private Bot bot;
    private GameInfo.GameMode gameMode;
    private boolean gameOn = false;
    private boolean isX = true;
    private int boardSize;

    /**
     * Initialization of GUI elements
     */
    public void initialize() {
        botLevel = new ToggleGroup();
        easyBot.setToggleGroup(botLevel);
        hardBot.setToggleGroup(botLevel);

        showGameNotStarted();

        statistics.setItems(tableList);
        statistics.setSelectionModel(null);
        modeColumn.setCellValueFactory(new PropertyValueFactory<>("gameMode"));
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("gameResult"));

        ObservableList<Node> nodes = board.getChildren();
        for (int i = 0; i < nodes.size(); i++) {
            int id = i;
            nodes.get(i).setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (gameOn) {
                        if (model.makeMove(id / boardSize, id % boardSize, isX)) {
                            confirmMove(id, isX);

                            if (!gameOn) {
                                return;
                            }

                            if (gameMode.equals(GameInfo.GameMode.TwoPlayers)) {
                                isX = !isX;
                            } else {
                                int move = bot.getMove();

                                if (model.makeMove(move / boardSize, move % boardSize, !isX)) {
                                    confirmMove(move, !isX);
                                } else {
                                    throw new RuntimeException("bot making incorrect moves");
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
        boardSize = model.getBoardSize();
        showGameStarted();
        gameOn = true;
    }

    private void showGameStarted() {
        message.setText("Game is in progress. Click on field to make move.");
    }

    private void onGameEnd(@NotNull GameInfo.GameResult result) {
        GameInfo gameInfo = new GameInfo(gameMode, result);
        gameOn = false;
        tableList.add(new TableElement(gameInfo.getGameMode(), gameInfo.getGameResult()));
        message.setText(gameInfo.getGameResult());

    }

    /**
     * Start new game with two players
     */
    public void newGameTwoPlayers(MouseEvent mouseEvent) {
        onGameStart();
        gameMode = GameInfo.GameMode.TwoPlayers;
    }

    /**
     * Starts new game with bot
     */
    public void newGameOnePlayer(MouseEvent mouseEvent) {
        onGameStart();
        String id = ((RadioButton) botLevel.getSelectedToggle()).getId();
        if (id.equals(EASYBOT)) {
            bot = new EasyBot(model);
            gameMode = GameInfo.GameMode.OnePlayerEasy;
        } else {
            bot = new HardBot(model);
            gameMode = GameInfo.GameMode.OnePlayerHard;
        }
    }

    /**
     * Show/hide statistics about previous games
     */
    public void showStatistics(MouseEvent mouseEvent) {
        boolean showStat = false;
        if (board.isVisible()) {
            showStat = true;
        }

        board.setVisible(!showStat);
        board.setDisable(showStat);
        statistics.setVisible(showStat);
        statistics.setDisable(!showStat);

        stats.setText(showStat ? "Board" : "statistics");
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
            GameInfo.GameResult result = GameInfo.GameResult.DRAW;
            try {
                result = model.getFinalGameResult();
            } catch (GameNotEndedException e) {
                e.printStackTrace();
                throw new RuntimeException("Guarantee is broken. If gameEnded then GameNotEndedException shouldn't be thrown");
            }
            onGameEnd(result);
        }
    }
}
