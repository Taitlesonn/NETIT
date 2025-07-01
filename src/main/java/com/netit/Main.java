package com.netit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main  extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Załaduj plik FXML z folderu resources
        Parent root = FXMLLoader.load(getClass().getResource("/first_ui.fxml"));
        
        primaryStage.setTitle("NETIT");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
