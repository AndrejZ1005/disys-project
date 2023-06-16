package com.example.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class JavaFXApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, TimeoutException {
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFXApplication.class.getResource("fuelstation-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("FuelStationUI");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}