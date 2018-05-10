package ru.spbau.fedorov.algo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * FTP desktop application.
 */
public class FTP extends Application {

    @Override
    public void start(@NotNull Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/FTP.fxml"));
        primaryStage.setTitle("FTP");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}