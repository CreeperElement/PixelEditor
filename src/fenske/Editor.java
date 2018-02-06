package fenske;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;

public class Editor extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        final Parent fxmlRoot = fxmlLoader.load(new FileInputStream(new File("editor.fxml")));
        primaryStage.setScene(new Scene(fxmlRoot));
        primaryStage.show();
    }
}
