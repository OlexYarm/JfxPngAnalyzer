/*
 * Copyright (c) 2024, Oleksandr Yarmolenko. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details.
 *
 */
package com.olexyarm.jfxpnganalyzer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static Scene SCENE;

    @Override
    public void start(Stage stage) throws IOException {

        LOGGER.debug("### Creating Scene.");

        FXMLLoader fxmlLoader = Utils.loadFXML("jfxPngAnalyzer");
        Parent root = fxmlLoader.load();

        SCENE = new Scene(root, Settings.INT_WINDOW_WIDTH, Settings.INT_WINDOW_HIGH);

        URL urlCssFile = App.class.getResource("jfxPngAnalyzerStyles.css");
        //SCENE.getStylesheets().add(urlCssFile.toString());

        SCENE.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (stage.isMaximized()) {
                    LOGGER.debug("### widthProperty event Stage Maximized.");
                } else {
                    //int intWindowWidthOld = oldValue.intValue();
                    int intWindowWidth = newValue.intValue();
                    Settings.INT_WINDOW_WIDTH = intWindowWidth;
                    Settings.save();
                    /*
                LOGGER.debug("### Changed WindowWidth."
                        + " WindowWidthOld=" + intWindowWidthOld
                        + " WindowWidthNew=" + intWindowWidth);
                     */
                }
            }
        });

        SCENE.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (stage.isMaximized()) {
                    LOGGER.debug("### heightProperty event Stage Maximized.");
                } else {
                    //int intWindowHighOld = oldValue.intValue();
                    int intWindowHigh = newValue.intValue();
                    Settings.INT_WINDOW_HIGH = intWindowHigh;
                    Settings.save();
                    /*
                LOGGER.debug("### Changed WindowHigh."
                        + " WindowHeighOld=" + intWindowHighOld
                        + " WindowHighNew=" + intWindowHigh);
                     */
                }
            }
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                Settings.INT_WINDOW_POSITION_X = (int) stage.getX();
                Settings.INT_WINDOW_POSITION_Y = (int) stage.getY();
                Settings.BOO_STAGE_MAXIMIZED = stage.isMaximized();
                Settings.save();
            }
        });

        stage.setX(Settings.INT_WINDOW_POSITION_X);
        stage.setY(Settings.INT_WINDOW_POSITION_Y);

        stage.setMaximized(Settings.BOO_STAGE_MAXIMIZED);
        stage.setScene(SCENE);
        stage.setTitle(Settings.STR_APP_TITLE);
        stage.show();
        LOGGER.debug("### Started APP.");
    }

    public static void main(String[] args) {
        launch();
    }
}
