package ru.spbau.fedorov.algo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * Pair game desktop application.
 * Board size is passed as a command line argument.
 */
public class PairApplication extends Application {
    private static final String USAGE = "Usage: ./app N";
    private static int boardSize;

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(@NotNull Stage primaryStage) throws Exception {
        primaryStage.setTitle("Pair");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        GridPane pane = Controller.getNewGameBoard(boardSize);
        Scene scene = new Scene(pane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main function of the application. Expects board size as an argument.
     * @param args arguments of command line
     */
    public static void main(String[] args) {
        if (args == null || args.length != 1) {
            System.out.println(USAGE);
            return;
        }

        int n;

        try {
            n = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println(USAGE);
            return;
        }

        if (n % 2 != 0 || n < 2) {
            System.out.println(USAGE);
            return;
        }

        boardSize = n;

        launch(args);
    }
}