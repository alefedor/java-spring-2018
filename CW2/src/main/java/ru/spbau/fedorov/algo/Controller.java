package ru.spbau.fedorov.algo;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import ru.spbau.fedorov.algo.logic.Logic;

/**
 * Controller for Pair javafx application
 */
public class Controller {

    private static int pressedNum = 0;
    private static Logic game;
    private static Label label;

    /**
     * Prepares board for a play.
     * @param boardSize size of board to play in
     * @return Grid Pane with buttons and a label
     */
    public static GridPane getNewGameBoard(int boardSize) {
        GridPane pane = new GridPane();

        game = new Logic(boardSize);

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Button button = new Button();

                final int x = i;
                final int y = j;

                button.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (button.getText().equals("")) {
                            game.push(button, x, y);
                        } else {
                            game.release(button, x, y);
                        }
                    }
                });

                button.setMinHeight(70);
                button.setMinWidth(70);

                pane.add(button, i, j);
            }
        }

        label = new Label();
        label.setText("Game in progress");

        pane.add(label, boardSize, 0);

        return pane;
    }

    /**
     * Congratulates on winning the game.
     */
    public static void onGameEnded() {
        label.setText("You succeeded!\nCongratulations!");
    }


}