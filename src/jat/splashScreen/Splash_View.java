package jat.splashScreen;


import java.util.logging.Logger;

import jat.ServiceLocator;
import jat.abstractClasses.View;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Copyright 2015, FHNW, Prof. Dr. Brad Richards. All rights reserved. This code
 * is licensed under the terms of the BSD 3-clause license (see the file
 * license.txt).
 * 
 * @author Brad Richards
 */
public class Splash_View extends View<Splash_Model> {
    ProgressBar progress;

    public Splash_View(Stage stage, Splash_Model model) {
        super(stage, model);
        stage.initStyle(StageStyle.TRANSPARENT); // also undecorated
    }

    @Override
    protected Scene create_GUI() {
        BorderPane root = new BorderPane();
        root.setId("splash");

        progress = new ProgressBar();
        HBox bottomBox = new HBox();
        bottomBox.setId("progressbox");
        bottomBox.getChildren().add(progress);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 300, 300, Color.TRANSPARENT);
        scene.getStylesheets().addAll(
                this.getClass().getResource("splash.css").toExternalForm());

        return scene;
    }
}