package ru.spbau.fedorov.tictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * Tic-Tac-Toe desktop application with support of plays
 * both with bots (of different level of difficulty) and real people (hotseat).
 * Saves history of matches.
 */
public class TicTacToeApplication extends Application {

    @Override
    public void start(@NotNull Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/tic-tac-toe.fxml"));
        primaryStage.setTitle("Tic-Tac-Toe");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}