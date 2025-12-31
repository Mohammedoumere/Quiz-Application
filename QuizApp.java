package com.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class QuizApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // The start method should only be responsible for loading the initial UI.
            // All networking logic should be handled by the controllers.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/client/login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setTitle("Quiz App - Login");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            // This will catch FXML loading errors, not network errors.
            e.printStackTrace();
            // You can show an alert here if the FXML file itself is missing.
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
