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

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JfxPngAnalyzerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JfxPngAnalyzerController.class);

    // -------------------------------------------------------------------------------------
    @FXML
    public BorderPane borderPanePngAnalyzer;

    @FXML
    public HBox jfxPngAnalyzerMenu;

    @FXML
    public TabPane tabPanePngAnalyzer;

    @FXML
    public VBox jfxPngAnalyzerBottom;

    // -------------------------------------------------------------------------------------
    @FXML
    public JfxPngAnalyzerMenuController jfxPngAnalyzerMenuController;

    @FXML
    public JfxPngAnalyzerBottomController jfxPngAnalyzerBottomController;

    // -------------------------------------------------------------------------------------
    // JFX constructor
    // -------------------------------------------------------------------------------------
    @FXML
    public void initialize() {

        LOGGER.debug("### Initialize JfxPngAnalyzerController."
                + " borderPanePngAnalyzer=\"" + borderPanePngAnalyzer + "\""
                + " jfxPngAnalyzerMenu=\"" + jfxPngAnalyzerMenu + "\""
                + " tabPanePngAnalyzer=\"" + tabPanePngAnalyzer + "\""
                + " jfxPngAnalyzerBottom=\"" + jfxPngAnalyzerBottom + "\""
                + " jfxPngAnalyzerMenuController=\"" + jfxPngAnalyzerMenuController + "\""
        //+ " jfxPngAnalyzerBottomController=\"" + jfxPngAnalyzerBottomController + "\""
        );
        this.jfxPngAnalyzerMenuController.setParentController(this);
        this.jfxPngAnalyzerBottomController.setParentController(this);

        Settings.load();

    }
    // -------------------------------------------------------------------------------------
}
