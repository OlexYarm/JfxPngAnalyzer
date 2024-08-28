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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JfxPngAnalyzerMenuController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JfxPngAnalyzerMenuController.class);

    // -------------------------------------------------------------------------------------
    @FXML
    public HBox hboxMenu;

    @FXML
    public MenuBar mbMenuBar;

    // -------------------------------------------------------------------------------------
    private JfxPngAnalyzerController jfxPngAnalyzerController;

    private BorderPane borderPanePngAnalyzer;

    private TabPane tabPane;
    private ObservableList<Tab> lstTabs;

    private VBox jfxPngAnalyzerBottom;

    // -------------------------------------------------------------------------------------
    // JFX constructor
    // -------------------------------------------------------------------------------------
    public void initialize() {

        LOGGER.debug("### Initialize JfxFileContentEditorMenuController."
                + " this=\"" + this + "\"");
    }

    // -------------------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------------------
    public void setParentController(JfxPngAnalyzerController jfxPngAnalyzerController) {

        this.jfxPngAnalyzerController = jfxPngAnalyzerController;

        this.borderPanePngAnalyzer = this.jfxPngAnalyzerController.borderPanePngAnalyzer;

        this.tabPane = this.jfxPngAnalyzerController.tabPanePngAnalyzer;
        this.lstTabs = this.tabPane.getTabs();

        this.jfxPngAnalyzerBottom = this.jfxPngAnalyzerController.jfxPngAnalyzerBottom;

        LOGGER.debug("### Setting Parent Controller in JfxPngAnalyzerController."
                + " this=\"" + this + "\""
                + " jfxPngAnalyzerController=\"" + jfxPngAnalyzerController + "\""
                + " borderPanePngAnalyzer=\"" + this.borderPanePngAnalyzer + "\""
                + " tabPane=\"" + this.tabPane + "\""
                + " lstTabs=\"" + this.lstTabs + "\""
                + " jfxPngAnalyzerBottom=\"" + jfxPngAnalyzerBottom + "\""
                + " hboxMenu=\"" + hboxMenu + "\""
                + " mbMenuBar=\"" + mbMenuBar + "\""
        );

        Settings.load();
    }

    // -------------------------------------------------------------------------------------
    // FXML Action Methods
    // -------------------------------------------------------------------------------------
    @FXML
    private void openFile(ActionEvent actionEvent) throws IOException {

        actionEvent.consume();

        if (LOGGER.isDebugEnabled()) {
            this.logActionEventId("Open File", actionEvent);
        }

        FileChooser fileChooser = new FileChooser();
        // TODO: Use last open directory and keep it in Settings.
        fileChooser.setInitialDirectory(new File(Settings.STR_DIRECTORY_USER_HOME_PATH));
        fileChooser.setTitle("Select a file to open");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.png", "*.png"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.*", "*.*"));

        File file = fileChooser.showOpenDialog(this.borderPanePngAnalyzer.getScene().getWindow());
        if (file == null) {
            LOGGER.info("File is not selected.");
            Utils.showMessage(Alert.AlertType.WARNING, "Opening File", "", "File is not selected.", null, null);
            return;
        }
        Path pathFile = file.toPath();

        this.openFileinTab(pathFile);
    }

    // -------------------------------------------------------------------------------------
    @FXML
    private void printTab(ActionEvent actionEvent) throws IOException {

        actionEvent.consume();

        Tab tab = this.tabPane.getSelectionModel().getSelectedItem();
        if (tab == null) {
            return;
        }
        String strTabId = tab.getId();

        PngFileAnalyzer pngFileAnalyzer = (PngFileAnalyzer) tab.getContent();
        String strFileName = pngFileAnalyzer.getFileName();
        String strFilePath = pngFileAnalyzer.getFilePath();
        LOGGER.info("Print."
                + " TabId=\"" + strTabId + "\""
                + " FileName=\"" + strFileName + "\""
                + " FilePath=\"" + strFilePath + "\"");

        this.print(strTabId, pngFileAnalyzer);
    }

    // -------------------------------------------------------------------------------------
    @FXML
    private void printChunks(ActionEvent actionEvent) throws IOException {

        actionEvent.consume();

        Tab tab = this.tabPane.getSelectionModel().getSelectedItem();
        if (tab == null) {
            return;
        }
        String strTabId = tab.getId();

        PngFileAnalyzer pngFileAnalyzer = (PngFileAnalyzer) tab.getContent();
        String strFileName = pngFileAnalyzer.getFileName();
        String strFilePath = pngFileAnalyzer.getFilePath();
        TableView tvChunks = pngFileAnalyzer.getChunks();

        LOGGER.info("Print chunks."
                + " TabId=\"" + strTabId + "\""
                + " FileName=\"" + strFileName + "\""
                + " FilePath=\"" + strFilePath + "\""
                + " Chunks=\"" + tvChunks + "\"");
        this.print(strTabId, tvChunks);
    }

    // -------------------------------------------------------------------------------------
    @FXML
    private void about(ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoader = Utils.loadFXML("jfxPngAnalyzerAbout");
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, Settings.INT_WINDOW_ABOUT_WIDTH, Settings.INT_WINDOW_ABOUT_WIDTH);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("About");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    // -------------------------------------------------------------------------------------
    // Helpers 
    // -------------------------------------------------------------------------------------
    private void logActionEventId(String strAction, ActionEvent actionEvent) {

        Object objActionEventSource = actionEvent.getSource();
        String strItemId;
        if (objActionEventSource instanceof javafx.scene.control.Button) {
            Button btn = (Button) objActionEventSource;
            strItemId = btn.getId();
        } else if (objActionEventSource instanceof javafx.scene.control.MenuItem) {
            MenuItem menuitemSource = (MenuItem) objActionEventSource;
            strItemId = menuitemSource.getId();
        } else {
            strItemId = null;
        }

        LOGGER.debug(strAction
                + " ActionEventSource=\"" + objActionEventSource + "\""
                + " ItemId=\"" + strItemId + "\""
        );
    }

    // -------------------------------------------------------------------------------------
    private boolean openFileinTab(Path pathFile) {

        if (!Utils.checkNewTabsAllowed(this.lstTabs)) {
            return false;
        }
        LOGGER.debug("Opening File in Tab."
                + " FILES_OPEN_COUNT=" + Settings.INT_FILES_OPENED_TOTAL_COUNT
                + " pathFile=\"" + pathFile + "\"");
        Tab tab = this.createNewTab(pathFile);
        if (tab == null) {
            return false;
        }

        PngFileAnalyzer pngFileAnalyzer = (PngFileAnalyzer) tab.getContent();
        boolean booOK = pngFileAnalyzer.parse();
        if (!booOK) {
            return false;
        }

        this.lstTabs.add(tab);
        this.tabPane.getSelectionModel().select(tab);

        //this.changeMenuVisibility(true);
        Settings.INT_FILES_OPENED_TOTAL_COUNT++;
        LOGGER.info("Opened File in Tab."
                + " FILES_OPEN_COUNT=\"" + Settings.INT_FILES_OPENED_TOTAL_COUNT
                + " pathFile=\"" + pathFile + "\"");

        return true;
    }

    // -------------------------------------------------------------------------------------
    private Tab createNewTab(Path pathFile) {

        if (pathFile == null) {
            return null;
        }

        Settings.INT_TABS_OPENED_TOTAL_COUNT++;
        Settings.INT_TABS_OPENED_COUNT++;
        String strTabId = "" + Settings.INT_TABS_OPENED_TOTAL_COUNT;
        Settings.INT_FILES_NEW_COUNT++;

        PngFileAnalyzer filePng = new PngFileAnalyzer(strTabId, pathFile);
        String strFilePath = filePng.getFilePath();
        String strFileName = filePng.getFileName();
        String strFileNameExt = filePng.getFileExt();

        Tab tab = new Tab(strFileName, filePng);
        tab.setId(strTabId);

        Tooltip tltp = new Tooltip(strFilePath);
        tab.setTooltip(tltp);

        tab.setClosable(true);
        tab.setOnCloseRequest(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                // Don't consume event here:event.consume();
                EventType eventType = event.getEventType();
                LOGGER.debug("Closing Tab."
                        + " TabId=\"" + strTabId + "\""
                        + " TabsOpenedTotal=" + Settings.INT_TABS_OPENED_TOTAL_COUNT
                        + " TabsOpen=" + Settings.INT_TABS_OPENED_COUNT
                        + " TabsClosed=" + Settings.INT_TABS_CLOSED_COUNT
                        + " FileName=\"" + strFileName + "\""
                        + " FilePath=\"" + strFilePath + "\""
                        + " FileNameExt=\"" + strFileNameExt + "\""
                        + " eventType=\"" + eventType + "\"");
                Settings.INT_TABS_OPENED_COUNT--;
                Settings.INT_TABS_CLOSED_COUNT++;
                if (Settings.INT_TABS_OPENED_COUNT == 0) {
                    // changeMenuVisibility(false);
                }
            }
        });

        LOGGER.info("Created Tab."
                + " TabId=\"" + strTabId + "\""
                + " pathFile=\"" + pathFile + "\"");
        return tab;
    }

    // -------------------------------------------------------------------------------------
    private void print(String strTabId, Node node) {

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null) {
            Window window = this.borderPanePngAnalyzer.getScene().getWindow();
            boolean booPrinterSelected = printerJob.showPrintDialog(window);
            if (!booPrinterSelected) {
                LOGGER.info("Printing canceled."
                        + " TabId=\"" + strTabId + "\"");
                return;
            }
            Printer printer = printerJob.getPrinter();
            String strPrinterName = printer.getName();
            boolean booPageSetup = printerJob.showPageSetupDialog(window);
            if (!booPageSetup) {
                LOGGER.info("Printing canceled."
                        + " TabId=\"" + strTabId + "\""
                        + " PrinterName=\"" + strPrinterName + "\"");
                return;
            }
            boolean booPrinted = printerJob.printPage(node);
            if (booPrinted) {
                printerJob.endJob();
                LOGGER.info("Printed File."
                        + " TabId=\"" + strTabId + "\""
                        + " PrinterName=\"" + strPrinterName + "\"");
            } else {
                LOGGER.error("Printing failed."
                        + " TabId=\"" + strTabId + "\""
                        + " PrinterName=\"" + strPrinterName + "\"");
            }
        } else {
            LOGGER.error("Printing failed, could not create PrinterJob."
                    + " TabId=\"" + strTabId + "\"");
        }
    }
    // -------------------------------------------------------------------------------------
}
