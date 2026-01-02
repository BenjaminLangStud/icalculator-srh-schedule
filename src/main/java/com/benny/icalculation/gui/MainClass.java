package com.benny.icalculation.gui;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class MainClass extends Application {


    private static final Logger log = LogManager.getLogger(MainClass.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        FXMLLoader fxmlLoader = new FXMLLoader(MainClass.class.getResource("mainScene.fxml"));

        final int sceneWidth = 800;
        final int sceneHeight = 400;
        Scene scene = new Scene(fxmlLoader.load(), sceneWidth, sceneHeight);

        primaryStage.setTitle("iCalculator GUI");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(MainClass.class.getResourceAsStream("assets/calculator-solid.png"))));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
