/*
 * SE 1021
 * Winter 2018
 * Lab Eight Image Viewer
 * Seth Fenske
 * Created 2/7/18
 */
package fenske;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;

public class Editor extends Application{

    /**
     * Use FX framework to launch program
     * @param args User supplied args
     */
    public static void main(String[] args) {
        Application.launch(Editor.class, args);
    }

    @Override
    /**
     * FX method loads the editor.fxml file and displays the GUI
     */
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        final Parent fxmlRoot = fxmlLoader.load(new FileInputStream(new File("editor.fxml")));
        primaryStage.setScene(new Scene(fxmlRoot));
        primaryStage.show();
    }
}
