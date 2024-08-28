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

import com.olexyarm.jfxpnganalyzer.chunk.Chunk;
import com.olexyarm.jfxpnganalyzer.chunk.ChunkFactory;
import com.olexyarm.jfxpnganalyzer.chunk.Chunk_0;
import com.olexyarm.jfxpnganalyzer.chunk.Chunk_erro;
import com.olexyarm.jfxpnganalyzer.chunk.IEND;
import com.olexyarm.jfxpnganalyzer.chunk.IHDR;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PngFileAnalyzer extends VBox {

    private static final Logger LOGGER = LoggerFactory.getLogger(PngFileAnalyzer.class);

    private final String strId;
    private final Path pathFile;
    private String strFilePath;
    private String strFileName;
    private String strFileExt;
    private String strFileDir;
    private long lngFileSize;
    private Task<String> taskFileLoad;

    // ---------- Graphics - Begin -----------------------------------------------------
    private final ImageView imageView = new ImageView();

    private final TableView<TableViewRow> tvChunks = new TableView<>();
    private final ObservableList<Chunk> olstChunks = FXCollections.observableArrayList();
    private final ListChangeListener<Chunk> olstChunksListChangeListener;

    private final HBox hboxState;
    private final Label lblFileName;
    private final Label lblFileState;
    private final ProgressBar progressBar;

    public class TableViewRow {

        // Common chunk properties
        private IntegerProperty ipChunkNumber;
        private StringProperty spChunkType;
        private IntegerProperty ipChunkLen;
        private IntegerProperty ipChunkCrc;

        // Specific chunk properties
        private StringProperty spChunkSdata;
        private StringProperty spChunkDataValues;
        private StringProperty spChunkError;

        private TableViewRow(Integer strChunkNumber, String strChunkType, Integer ipChunkLen, Integer strChunkCrc,
                String strChunkSdata, String strChunkDataValues, String strError) {

            this.ipChunkNumber = new SimpleIntegerProperty(strChunkNumber);
            this.spChunkType = new SimpleStringProperty(strChunkType);
            this.ipChunkLen = new SimpleIntegerProperty(ipChunkLen);
            this.ipChunkCrc = new SimpleIntegerProperty(strChunkCrc);
            this.spChunkSdata = new SimpleStringProperty(strChunkSdata);
            this.spChunkDataValues = new SimpleStringProperty(strChunkDataValues);
            this.spChunkError = new SimpleStringProperty(strError);
        }

        public Integer getChunkNumber() {
            return this.ipChunkNumber.get();
        }

        public String getChunkType() {
            return this.spChunkType.get();
        }

        public Integer getChunkLen() {
            return this.ipChunkLen.get();
        }

        public Integer getChunkCrc() {
            return this.ipChunkCrc.get();
        }

        public String getChunkSdata() {
            return this.spChunkSdata.get();
        }

        public String getChunkDataValues() {
            return this.spChunkDataValues.get();
        }

        public String getChunkError() {
            return this.spChunkError.get();
        }

        public void setChunkNumber(Integer strChunkNumber) {
            this.ipChunkNumber.set(strChunkNumber);
        }

        public void setChunkType(String strChunkType) {
            this.spChunkType.set(strChunkType);
        }

        public void setChunkLen(Integer ipChunkLen) {
            this.ipChunkLen.set(ipChunkLen);
        }

        public void setChunkCrc(Integer strChunkCrc) {
            this.ipChunkCrc.set(strChunkCrc);
        }

        public void setChunkSdata(String strChunkSdata) {
            this.spChunkSdata.set(strChunkSdata);
        }

        public void setChunkDataValues(String strChunkDataValues) {
            this.spChunkDataValues.set(strChunkDataValues);
        }

        public void setChunkError(String strChunkError) {
            this.spChunkError.set(strChunkError);
        }

        public IntegerProperty chunkNumberProperty() {
            if (this.ipChunkNumber == null) {
                this.ipChunkNumber = new SimpleIntegerProperty(this, "chunkNumber");
            }
            return this.ipChunkNumber;
        }

        public StringProperty chunkTypeProperty() {
            if (this.spChunkType == null) {
                this.spChunkType = new SimpleStringProperty(this, "chunkType");
            }
            return this.spChunkType;
        }

        public IntegerProperty chunkLenProperty() {
            if (this.ipChunkLen == null) {
                this.ipChunkLen = new SimpleIntegerProperty(this, "chunkLen");
            }
            return this.ipChunkLen;
        }

        public IntegerProperty chunkCrcProperty() {
            if (this.ipChunkCrc == null) {
                this.ipChunkCrc = new SimpleIntegerProperty(this, "chunkCrc");
            }
            return this.ipChunkCrc;
        }

        public StringProperty chunkSdataProperty() {
            if (this.spChunkSdata == null) {
                this.spChunkSdata = new SimpleStringProperty(this, "chunkSdata");
            }
            return this.spChunkSdata;
        }

        public StringProperty chunkDataValuesProperty() {
            if (this.spChunkDataValues == null) {
                this.spChunkDataValues = new SimpleStringProperty(this, "chunkDataValues");
            }
            return this.spChunkDataValues;
        }

        public StringProperty chunkErrorProperty() {
            if (this.spChunkError == null) {
                this.spChunkError = new SimpleStringProperty(this, "chunkError");
            }
            return this.spChunkError;
        }
    }

    // ---------- Graphics - End -----------------------------------------------------
    // -------------------------------------------------------------------------------------
    // Construstors
    // -------------------------------------------------------------------------------------
    public PngFileAnalyzer(final String strId, final Path pathFile) {

        this.strId = strId;
        this.pathFile = pathFile;
        this.parseFilePath(strId, pathFile);
        if (pathFile == null) {
            this.olstChunksListChangeListener = null;
            this.hboxState = null;
            this.lblFileName = null;
            this.lblFileState = null;
            this.progressBar = null;
            return;
        }

        // ---------- initGraphics - begin -----------------------------------------------------
        try (FileInputStream fis = new FileInputStream(this.strFilePath);) {
            Image image = new Image(fis);
            this.imageView.setImage(image);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }

        // ----------
        Label lblTitle = new Label("PNG Chunks");
        lblTitle.setFont(new Font("Arial", 15));

        this.tvChunks.setId("table-view-chunks");
        this.tvChunks.setEditable(false);

        TableColumn<TableViewRow, String> tcNumber = new TableColumn<>("#");
        tcNumber.setId("tv-seq");
        tcNumber.setCellValueFactory(new PropertyValueFactory<>("chunkNumber"));
        tcNumber.setSortable(false);

        TableColumn<TableViewRow, String> tcType = new TableColumn<>("Type");
        tcType.setId("tv-type");
        tcType.setCellValueFactory(new PropertyValueFactory<>("chunkType"));
        tcType.setSortable(false);

        TableColumn<TableViewRow, String> tcLen = new TableColumn<>("Len");
        tcLen.setId("tv-len");
        tcLen.setCellValueFactory(new PropertyValueFactory<>("chunkLen"));
        tcLen.setSortable(false);

        TableColumn<TableViewRow, String> tcCrc = new TableColumn<>("CRC");
        tcCrc.setId("tv-crc");
        tcCrc.setCellValueFactory(new PropertyValueFactory<>("chunkCrc"));
        tcCrc.setSortable(false);

        TableColumn<TableViewRow, String> tcSdata = new TableColumn<>("Data");
        tcSdata.setId("tv-data");
        tcSdata.setCellValueFactory(new PropertyValueFactory<>("chunkSdata"));
        tcSdata.setSortable(false);

        TableColumn<TableViewRow, String> tcDataValues = new TableColumn<>("Values");
        tcDataValues.setId("tv-data-values");
        tcDataValues.setCellValueFactory(new PropertyValueFactory<>("chunkDataValues"));
        tcDataValues.setSortable(false);

        TableColumn<TableViewRow, String> tcError = new TableColumn<>("Errors");
        tcError.setId("tv-error");
        tcError.setCellValueFactory(new PropertyValueFactory<>("chunkError"));
        tcError.setSortable(false);

        /*
        tcNumber.setStyle("-fx-alignment: CENTER-RIGHT; -fx-pref-width: 10 px;");
        tcType.setStyle("-fx-alignment: CENTER;");
        tcLen.setStyle("-fx-alignment: CENTER-RIGHT; -fx-pref-width: 20 px;");
        tcCrc.setStyle("-fx-alignment: CENTER-RIGHT; -fx-pref-width: 30 px;");
        tcSdata.setStyle("-fx-alignment: CENTER-LEFT; -fx-pref-width: 60 px;");
        tcDataValues.setStyle("-fx-alignment: CENTER-LEFT; -fx-pref-width: 60 px;");
         */
        //this.tvChunks.getStyleClass().add("table-view-chunks");
        this.tvChunks.getColumns().addAll(tcNumber, tcType, tcLen, tcCrc, tcSdata, tcDataValues, tcError);

        // ----------
        this.hboxState = new HBox();
        Insets insHboxPadd = new Insets(5, 5, 5, 20);
        this.hboxState.setPadding(insHboxPadd);
        this.hboxState.setSpacing(5);
        this.hboxState.setVisible(false);

        this.lblFileName = new Label(this.strFileName);
        this.progressBar = new ProgressBar(0);
        this.lblFileState = new Label("");

        this.hboxState.getChildren().addAll(this.lblFileName, this.progressBar, this.lblFileState);
        this.hboxState.managedProperty().bind(this.hboxState.visibleProperty());

        // ----------
        this.setSpacing(5);
        Insets insVboxPadd = new Insets(5, 5, 5, 5);
        this.setPadding(insVboxPadd);

        VBox.setVgrow(this.tvChunks, Priority.ALWAYS);
        this.getChildren().addAll(this.imageView, lblTitle, this.tvChunks, this.hboxState);
        // ----------

        // ---------- initGraphics - end -------------------------------------------------------
        //
        // ---------- registerListeners - begin ------------------------------------------------
        this.olstChunksListChangeListener = new ListChangeListener<Chunk>() {
            @Override
            public void onChanged(Change<? extends Chunk> change) {
                while (change.next()) {
                    int intFrom = change.getFrom();
                    int intTo = change.getTo();
                    boolean booAdded = change.wasAdded();
                    int intChunksListSize = olstChunks.size();
                    LOGGER.debug("olstChunksListChangeListener."
                            + " Id=\"" + strId + "\""
                            + " FileName=\"" + strFileName + "\""
                            + " intFrom=" + intFrom
                            + " intTo=" + intTo
                            + " booAdded=" + booAdded
                            + " change=\"" + change + "\""
                            + " intChunksListSize=" + intChunksListSize);
                    if (intChunksListSize >= intTo) {
                        Chunk chunk = olstChunks.get(intTo - 1);
                        int intChunkSeq = chunk.getSeq();
                        String strChunkType = chunk.getType();
                        int intChunkLen = chunk.getLength();
                        int intChunkCrc = chunk.getCrc();
                        String strChunkSdata = chunk.getSdata();
                        String strChunkDataValues = chunk.getDataValues();
                        StringBuilder sbError = chunk.getError();

                        tvChunks.getItems().add(new TableViewRow(intChunkSeq, strChunkType, intChunkLen,
                                intChunkCrc, strChunkSdata, strChunkDataValues, sbError.toString()));
                        LOGGER.debug("olstChunksListChangeListener."
                                + " Id=\"" + strId + "\""
                                + " intChunkSeq=" + intChunkSeq
                                + " strChunkType=\"" + strChunkType + "\""
                                + " intChunkLen=" + intChunkLen
                                + " intChunkCrc=" + intChunkCrc
                                + " strChunkSdata=\"" + strChunkSdata + "\""
                                + " strChunkDataValues=\"" + strChunkDataValues + "\""
                                + " sbError=\"" + sbError.toString() + "\"");

                        for (Chunk addedItem : change.getAddedSubList()) {
                            LOGGER.debug("olstChunksListChangeListener."
                                    + " Added=\"" + addedItem + "\"");
                        }
                    }
                }
            }
        };
        this.olstChunks.addListener(this.olstChunksListChangeListener);
        // ---------- registerListeners - end ------------------------------------------------
    }

    // -------------------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------------------
    public boolean parse() {

        this.hboxState.visibleProperty().set(true);
        this.progressBar.setProgress(0);

        LOGGER.debug("# Parsing PNG file."
                + " pathFile=\"" + this.pathFile + "\"");

        if (!Files.exists(pathFile)) {
            String strErrMsg = "File Path not found."
                    + " Id=\"" + strId + "\""
                    + " pathFile=\"" + pathFile + "\"";
            LOGGER.error(strErrMsg);
            Utils.showMessage(Alert.AlertType.ERROR, "Open File", strErrMsg, null, null, null);
            return false;
        } else if (Files.isDirectory(pathFile)) {
            String strErrMsg = "File Path is Directory."
                    + " Id=\"" + strId + "\""
                    + " pathFile=\"" + pathFile + "\"";
            LOGGER.error(strErrMsg);
            Utils.showMessage(Alert.AlertType.ERROR, "Open File", strErrMsg, null, null, null);
            return false;
        } else if (!Files.isReadable(pathFile)) {
            String strErrMsg = "File Path not readable."
                    + " Id=\"" + strId + "\""
                    + " pathFile=\"" + pathFile + "\"";
            LOGGER.error(strErrMsg);
            Utils.showMessage(Alert.AlertType.ERROR, "Open File", strErrMsg, null, null, null);
            return false;
        }

        lngFileSize = new File(this.strFilePath).length();
        LOGGER.debug("Reading File."
                + " Id=\"" + strId + "\""
                + " FileSize" + lngFileSize
                + " pathFile=\"" + pathFile + "\"");

        this.taskFileLoad = new Task<>() {
            @Override
            protected String call() throws Exception {

                updateMessage("PNG parsing started.");

                final Map<String, Chunk> mapChunks = new HashMap<>();
                final ObservableMap<String, Chunk> omapChunks = FXCollections.observableMap(mapChunks);

                int intFileSize = (int) lngFileSize;
                int intReadTotal = 0;
                int intChunkCount = 0;
                int intErrors = 0;

                String strMsgEnd = "";
                try (FileInputStream fis = new FileInputStream(strFilePath); FileChannel fc = fis.getChannel();) {
                    MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, lngFileSize);
                    int intRemaining = mbb.remaining();
                    if (intRemaining < 50) {
                        // Check PNG file minimum length (25 + 13 + 12 = 50)
                        // 8 - signature
                        // 25 - IHDR mandatory first chunk
                        // includes:
                        // 4 - length
                        // 4 - type
                        // 13 - data
                        // 4 - CRC
                        // 13 - IDAT
                        // includes:
                        // 4 - length
                        // 4 - type
                        // 1 - data
                        // 4 - CRC
                        // 12 - IEND mandatory first chunk
                        // includes:
                        // 4 - length
                        // 4 - type
                        // 0 - data
                        // 4 - CRC
                        String strErrMsg = "File is too short."
                                + " Id=\"" + strId + "\""
                                + " Remaining=" + intRemaining
                                + " pathFile=\"" + pathFile + "\"";
                        LOGGER.error(strErrMsg);
                        updateMessage("PNG File is too short.");
                        throw new Exception(strErrMsg);
                    }

                    // PNG signature -----------
                    byte[] bytFileSignature = new byte[8];
                    try {
                        mbb.get(bytFileSignature);
                    } catch (BufferUnderflowException e) {
                        String strErrMsg = "Could not read file signature."
                                + " Id=\"" + strId + "\""
                                + " FileSignature=\"" + Utils.convertBytesToStringHex(bytFileSignature) + "\""
                                + " BufferUnderflowException=\"" + e.toString() + "\"";
                        LOGGER.error(strErrMsg);
                        updateMessage("Could not read file signature.");
                        try {
                            Thread.sleep(Settings.INT_SLEEP_DELAY_SHOW_ERROR);
                        } catch (InterruptedException ex) {
                        }
                        throw new Exception(strErrMsg);
                    }
                    intReadTotal += 8;
                    //updateProgress(calculateProgress(mbb, intReadTotal, intFileSize), intFileSize);
                    updateProgress(intReadTotal, intFileSize);

                    byte[] abytCopy = Arrays.copyOfRange(bytFileSignature, 0, 4);
                    final Chunk chunk0 = new Chunk_0(intChunkCount, 8, abytCopy, bytFileSignature, 0, omapChunks);
                    String strChunkType = chunk0.getType();
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            olstChunks.add(chunk0);
                        }
                    });

                    boolean booSignatureValid = chunk0.isValidCrC();
                    if (booSignatureValid) {
                        updateMessage("PNG signatire valid.");
                    } else {
                        updateMessage("PNG signatire invalid.");
                    }
                    if (Settings.INT_SLEEP_DELAY_SHOW_INFO > 0) {
                        try {
                            Thread.sleep(Settings.INT_SLEEP_DELAY_SHOW_INFO);
                        } catch (InterruptedException ex) {
                        }
                    }

                    // Read chunks -----------
                    String strErrMsg;
                    while (true) {
                        intChunkCount++;
                        final Chunk chunk = ChunkFactory.retrieve(intChunkCount, mbb, omapChunks);
                        if (chunk == null) {
                            strErrMsg = "Null chunk."
                                    + " Id=\"" + strId + "\""
                                    + " pathFile=\"" + pathFile + "\"";
                            updateMessage("Null chunk");
                            throw new Exception(strErrMsg);
                        }
                        strChunkType = chunk.getType();
                        if (intChunkCount == 1) {
                            // First Chunk must be IHDR
                            if (!(chunk instanceof IHDR)) {
                                strErrMsg = "First PNG Chunk is not IHDR."
                                        + " Id=\"" + strId + "\""
                                        + " ChunkType=\"" + strChunkType + "\"";
                                LOGGER.error(strErrMsg);
                                updateMessage("First chunk is not IHDR: " + strChunkType);
                                throw new Exception(strErrMsg);
                            }
                        }
                        omapChunks.put(strChunkType, chunk);

                        intReadTotal += (12 + chunk.getLength());
                        //updateProgress(calculateProgress(mbb, intReadTotal, intFileSize), intFileSize);
                        updateProgress(intReadTotal, intFileSize);
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                olstChunks.add(chunk);
                            }
                        });
                        updateMessage("" + intChunkCount + ":" + strChunkType);
                        if (chunk instanceof IEND) {
                            intRemaining = mbb.remaining();
                            strMsgEnd = "End of PNG file.";
                            if (intRemaining > 0) {
                                strMsgEnd += " !!! There is something after IEND chunk !!!";
                            }
                            String strLogMsg = strMsgEnd
                                    + " Id=\"" + strId + "\""
                                    + " ChunkType=\"" + strChunkType + "\""
                                    + " Remaining=" + intRemaining
                                    + " pathFile=\"" + pathFile + "\"";
                            LOGGER.error(strLogMsg);
                            break;
                        }
                        if (chunk instanceof Chunk_erro) {
                            intErrors++;
                            if (intErrors > Settings.INT_CHUNK_ERRORS_MAX) {
                                strMsgEnd = "Too many errors.";
                                String strLogMsg = strMsgEnd
                                        + " Id=\"" + strId + "\""
                                        + " ChunkType=\"" + strChunkType + "\""
                                        + " Remaining=" + intRemaining
                                        + " pathFile=\"" + pathFile + "\"";
                                LOGGER.error(strLogMsg);
                                break;
                            }
                        }
                    }
                } catch (FileNotFoundException ex) {
                    String strErrMsg = "File not found."
                            + " Id=\"" + strId + "\""
                            + " pathFile=\"" + pathFile + "\"";
                    LOGGER.error(strErrMsg + " FileNotFoundException=\"" + ex.toString() + "\"");
                    throw new Exception(strErrMsg);
                } catch (IOException ex) {
                    String strErrMsg = "Read File error."
                            + " Id=\"" + strId + "\""
                            + " pathFile=\"" + pathFile + "\""
                            + " IOException=\"" + ex.toString() + "\"";
                    LOGGER.error(strErrMsg + " IOException=\"" + ex.toString() + "\"");
                    throw new Exception(strErrMsg);
                }
                LOGGER.debug(strMsgEnd);
                updateMessage(strMsgEnd);
                int intChunksMapCount = omapChunks.size();

                LOGGER.debug("# Task finished."
                        + " Id=\"" + strId + "\""
                        + " task=\"" + taskFileLoad + "\""
                        + " intChunksMapCount=" + intChunksMapCount
                );
                return strMsgEnd;
            }
        };
        this.processTask();

        LOGGER.debug("# Task starting."
                + " Id=\"" + strId + "\""
                + " task=\"" + taskFileLoad + "\"");
        new Thread(taskFileLoad).start();
        LOGGER.debug("# Task started."
                + " Id=\"" + strId + "\""
                + " task=\"" + taskFileLoad + "\"");

        int intChunksCount = olstChunks.size();
        int intTableViewRowCount = tvChunks.getItems().size();

        LOGGER.debug("parse finished."
                + " Id=\"" + strId + "\""
                + " intChunksCount=" + intChunksCount
                + " intTableViewRowCount=" + intTableViewRowCount
        );

        return true;
    }

    // -------------------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------------------
    private void processTask() {

        if (this.taskFileLoad == null) {
        } else {
            this.progressBar.progressProperty().bind(this.taskFileLoad.progressProperty());
            this.lblFileState.textProperty().bind(this.taskFileLoad.messageProperty());

            ReadOnlyObjectProperty<Worker.State> stateProperty = this.taskFileLoad.stateProperty();
            Worker.State state = stateProperty.getValue();
            String stateName = state.name();
            LOGGER.debug("Task started."
                    + " Id=\"" + this.strId + "\""
                    + " taskFileLoad=\"" + this.taskFileLoad + "\""
                    + " stateProperty=\"" + stateProperty + "\""
                    + " state=\"" + state + "\""
                    + " stateName=\"" + stateName + "\""
            );

            this.taskFileLoad.onScheduledProperty().set(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    EventType eventType = event.getEventType();
                    LOGGER.debug("onScheduledProperty."
                            + " Id=\"" + strId + "\""
                            + " eventType=\"" + eventType + "\""
                            + " event=\"" + event + "\"");
                    event.consume();
                }
            });

            this.taskFileLoad.onRunningProperty().set(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    EventType eventType = event.getEventType();
                    LOGGER.debug("onRunningProperty."
                            + " Id=\"" + strId + "\""
                            + " eventType=\"" + eventType + "\""
                            + " event=\"" + event + "\"");
                    event.consume();
                }
            });

            this.taskFileLoad.onFailedProperty().set(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    String strErrMsg = lblFileState.textProperty().getValue()
                            + "\nFile=" + lblFileName.getText()
                            + "\nFailed to parse PNG file.";
                    progressBar.progressProperty().unbind();
                    lblFileState.textProperty().unbind();
                    EventType eventType = event.getEventType();
                    LOGGER.debug("onFailedProperty."
                            + " Id=\"" + strId + "\""
                            + " eventType=\"" + eventType + "\""
                            + " event=\"" + event + "\""
                            + " ErrMsg=\"" + strErrMsg + "\"");
                    event.consume();
                }
            });

            this.taskFileLoad.onSucceededProperty().set(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    ReadOnlyObjectProperty<Worker.State> stateProperty = taskFileLoad.stateProperty();
                    Worker.State state = stateProperty.getValue();
                    String stateName = state.name();
                    EventType eventType = event.getEventType();
                    String strText;
                    int intTextLen = 0;
                    try {
                        strText = (String) taskFileLoad.get();
                    } catch (InterruptedException | ExecutionException ex) {
                        LOGGER.error("onSucceededProperty."
                                + " Id=\"" + strId + "\""
                                + " Exception=\"" + ex.toString() + "\"");
                        strText = "";
                    }
                    int intTextLogLimit = Settings.INT_LOG_MESSAGE_LENGTH_MAX;
                    String strTextPart = "";
                    if (strText != null) {
                        intTextLen = strText.length();
                        int intTrim = Math.min(intTextLen, intTextLogLimit);
                        strTextPart = strText.substring(0, intTrim) + "\n...";
                    }
                    String strMsg = taskFileLoad.getMessage();
                    LOGGER.debug("onSucceededProperty."
                            + " Id=\"" + strId + "\""
                            + " event=\"" + event + "\""
                            + " eventType=\"" + eventType + "\""
                            + " stateProperty=\"" + stateProperty + "\""
                            + " state=\"" + state + "\""
                            + " stateName=\"" + stateName + "\""
                            + "\nstrMsg=\"" + strMsg + "\""
                            + "\nTextLen=\"" + intTextLen + "\""
                            + "\nText=\"" + strTextPart + "\""
                    );
                    progressBar.progressProperty().unbind();
                    lblFileState.textProperty().unbind();

                    int intChunksCount = olstChunks.size();
                    int intTableViewRowCount = tvChunks.getItems().size();
                    LOGGER.debug("onSucceededProperty."
                            + " Id=\"" + strId + "\""
                            + " intChunksCount=" + intChunksCount
                            + " intTableViewRowCount=" + intTableViewRowCount
                    );
                    event.consume();
                }
            });

        }
    }

    // -------------------------------------------------------------------------------------
    private void parseFilePath(final String strId, Path pathFile) {

        if (pathFile == null) {
            this.strFilePath = "";
            this.strFileName = "";
            this.strFileExt = "";
            this.strFileDir = "";
            LOGGER.error("File Path is null."
                    + " Id=\"" + strId + "\"");
        } else {
            this.strFilePath = pathFile.toString();
            final int intFileNamePos = this.strFilePath.lastIndexOf(File.separator);
            if (intFileNamePos < 0) {
                this.strFileDir = "";
                this.strFileName = strFilePath;
            } else {
                this.strFileDir = this.strFilePath.substring(0, intFileNamePos);
                this.strFileName = this.strFilePath.substring(intFileNamePos + 1);
            }

            final int intFileNameExtPos = strFileName.lastIndexOf(".");
            final String strFileNameExt;
            if (intFileNameExtPos < 0 || intFileNameExtPos == 0 || intFileNameExtPos == this.strFileName.length()) {
                strFileNameExt = "";
            } else {
                strFileNameExt = this.strFileName.substring(intFileNameExtPos + 1);
            }
            this.strFileExt = strFileNameExt;
            LOGGER.info("Parsed File."
                    + " Id=\"" + strId + "\""
                    + " FilePath=\"" + this.pathFile + "(" + this.strFilePath + ")\""
                    + " FileNamePos=\"" + intFileNamePos + "\""
                    + " FileName=\"" + this.strFileName + "\""
                    + " FileDir=\"" + this.strFileDir + "\""
                    + " FileNameExtPos=\"" + intFileNameExtPos + "\""
                    + " FileNameExt=\"" + this.strFileExt + "\"");
        }
    }

    // -------------------------------------------------------------------------------------
    public int calculateProgress(MappedByteBuffer mbb, int intReadTotal, int intFileSize) {

        int intRemaining = mbb.remaining();
        /*
        double dblProgress;
        if (intRemaining > 0) {
            dblProgress = 1.0 - ((double) intRemaining) / (double) lngFileSize;
        } else {
            dblProgress = 1.0;
        }
         */
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
        int intProgress = intFileSize - intRemaining;
        LOGGER.debug("Parse Progress."
                + " Id=\"" + strId + "\""
                + " intReadTotal=" + intReadTotal
                + " intProgress=" + intProgress
                + " intRemaining=" + intRemaining
                + " intFileSize=" + intFileSize
                //+ " dblProgress=" + dblProgress
                + " FilePath=\"" + this.pathFile + "\"");

        return intRemaining;
    }

    // -------------------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------------------
    public final Path getPathFile() {
        return this.pathFile;
    }

    public final String getFilePath() {
        return this.strFilePath;
    }

    public final String getFileName() {
        return this.strFileName;
    }

    public final String getFileExt() {
        return this.strFileExt;
    }

    public final String getFileDir() {
        return this.strFileDir;
    }

    public final TableView getChunks() {
        return this.tvChunks;
    }

    // -------------------------------------------------------------------------------------
}
