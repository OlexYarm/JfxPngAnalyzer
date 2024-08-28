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
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JfxPngAnalyzerBottomController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JfxPngAnalyzerBottomController.class);

    // -------------------------------------------------------------------------------------
    @FXML
    public VBox vboxBottom;

    @FXML
    public HBox hboxBottomVersion;

    @FXML
    private Label hboxBottomLabelVersion;

    // -------------------------------------------------------------------------------------
    private JfxPngAnalyzerController jfxPngAnalyzerController;

    // -------------------------------------------------------------------------------------
    // JFX constructor
    // -------------------------------------------------------------------------------------
    public void initialize() {

        LOGGER.debug("### Initialize JfxPngAnalyzerBottomController."
                + " this=\"" + this + "\"");
    }

    // -------------------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------------------
    public void setParentController(JfxPngAnalyzerController jfxPngAnalyzerController) {

        this.jfxPngAnalyzerController = jfxPngAnalyzerController;

        LOGGER.debug("### Setting Parent Controller in JfxPngAnalyzerController."
                + " this=\"" + this + "\""
                + " jfxPngAnalyzerController=\"" + jfxPngAnalyzerController + "\""
                + " vboxBottom=\"" + vboxBottom + "\""
                + " hboxBottomVersion=\"" + hboxBottomVersion + "\""
                + " hboxBottomLabelVersion=\"" + hboxBottomLabelVersion + "\"");

        this.hboxBottomLabelVersion.setText(Settings.STR_APP_TITLE + " " + Settings.STR_VERSION);

    }
    // -------------------------------------------------------------------------------------
}
