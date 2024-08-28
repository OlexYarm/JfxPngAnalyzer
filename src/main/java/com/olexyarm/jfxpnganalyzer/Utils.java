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

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    // ---------------------------------------------------------------------------
    // Methods
    // ---------------------------------------------------------------------------
    public static FXMLLoader loadFXML(String fxml) throws IOException {
        LOGGER.debug("### Loading fxml. fxml=\"" + fxml + "\"");
        URL urlFxmlFile = App.class.getResource(fxml + ".fxml");
        if (urlFxmlFile == null) {
            LOGGER.error("### Could not get url for fxml file. fxml=\"" + fxml + "\"");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(urlFxmlFile);
        return fxmlLoader;
    }

    // ---------------------------------------------------------------------------
    public static boolean checkNewTabsAllowed(ObservableList<Tab> lstTabs) {

        if (lstTabs == null) {
            // it should never happen, but ...
            LOGGER.error("List of tabs is null.");
            return false;
        }
        int int_tabs_count = lstTabs.size();
        if (int_tabs_count >= Settings.INT_TABS_COUNT_MAX) {
            LOGGER.error("# Too many files opened."
                    + " TABS_COUNT=" + int_tabs_count
                    + " TABS_COUNT_MAX=" + Settings.INT_TABS_COUNT_MAX);
            showMessage(Alert.AlertType.ERROR, "Open File", "Too many files opened (" + int_tabs_count + " files)",
                    "Please close any tab and try again, or change settings for TABS MAX.", null, null);
            return false;
        }
        return true;
    }

    // -------------------------------------------------------------------------------------
    public static boolean showMessage(Alert.AlertType alertType, String strTitle, String strHeader, String strMessage,
            String strButtonTextYes, String strButtonTextNo) {

        ButtonType btYes = new ButtonType(strButtonTextYes, ButtonBar.ButtonData.OK_DONE);
        ButtonType btNo = new ButtonType(strButtonTextNo, ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(alertType);
        alert.setTitle(strTitle);
        alert.setHeaderText(strHeader);
        alert.setContentText(strMessage);
        if ((strButtonTextYes != null && !strButtonTextYes.isEmpty()) || (strButtonTextNo != null && !strButtonTextNo.isEmpty())) {
            alert.getButtonTypes().clear();
        }
        if (strButtonTextYes != null && !strButtonTextYes.isEmpty()) {
            alert.getButtonTypes().add(btYes);
        }
        if (strButtonTextNo != null && !strButtonTextNo.isEmpty()) {
            alert.getButtonTypes().add(btNo);
        }
        LOGGER.debug("Show Alert."
                + " Title=\"" + strTitle + "\""
                + " Header=\"" + strHeader + "\""
                + " Message=\"" + strMessage + "\"");

        boolean booReturn = false;
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(btYes) == btYes) {
            LOGGER.debug("Alert response Yes."
                    + " Title=\"" + strTitle + "\""
                    + " Header=\"" + strHeader + "\""
                    + " Message=\"" + strMessage + "\"");
            booReturn = true;
        }
        return booReturn;
    }

    // -------------------------------------------------------------------------------------
    public static boolean checkFileExist(String strTabID, Path pathFile) {

        if (pathFile == null) {
            LOGGER.error("pathFile is null."
                    + " TabId=\"" + strTabID + "\"");
            return false;
        }

        try {
            if (Files.isDirectory(pathFile)) {
                LOGGER.error("File is directory."
                        + " TabId=\"" + strTabID + "\""
                        + " pathFile=\"" + pathFile + "\"");
                return false;
            }
            if (Files.isReadable(pathFile)) {
                return true;
            }
        } catch (Throwable t) {
            LOGGER.error("Could analize File because of security violation."
                    + " TabId=\"" + strTabID + "\""
                    + " pathFile=\"" + pathFile + "\""
                    + " Throwable=\"" + t.toString() + "\"");
            return false;
        }
        return false;
    }

    // -------------------------------------------------------------------------------------
    public static int createFile(Path pathFile) {

        if (pathFile == null) {
            LOGGER.error("Could not create null File.");
            return 0;
        }

        if (Files.exists(pathFile) && !Files.isDirectory(pathFile) && Files.isReadable(pathFile)) {
            if (Files.isWritable(pathFile)) {
                LOGGER.info("File already exist and writeable."
                        + " FilePath=\"" + pathFile + "\"");
                return 1;
            } else {
                try {
                    Files.setAttribute(pathFile, "readonly", "false");
                } catch (IOException ex) {
                    LOGGER.error("File already exist, could not set writable."
                            + " FilePath=\"" + pathFile + "\""
                            + " IOException=\"" + ex.toString() + "\"");
                    return 0;
                }
            }
            if (!Files.isWritable(pathFile)) {
                LOGGER.info("File already exist and is read-only."
                        + " FilePath=\"" + pathFile + "\"");
                return 0;
            }
        }

        try {
            Path pathFileNew = Files.createFile(pathFile);
            LOGGER.info("Created new File."
                    + " FilePath=\"" + pathFileNew + "\"");
            return 1;
        } catch (IOException e) {
            Path pathParent = pathFile.getParent();
            if (Files.exists(pathParent)) {
                LOGGER.error("Could not create new File."
                        + " FilePath=\"" + pathFile + "\""
                        + " IOException=\"" + e.toString() + "\"");
                return -1;
            }
            try {
                Files.createDirectories(pathParent);
                LOGGER.info("Created new Directories."
                        + " FilePath=\"" + pathFile + "\""
                        + " pathParent=\"" + pathParent + "\"");
            } catch (IOException ex) {
                LOGGER.error("Could not create parent directory."
                        + " FilePath=\"" + pathFile + "\""
                        + " pathParent=\"" + pathParent + "\""
                        + " IOException=\"" + ex.toString() + "\"");
                return -1;
            }
            try {
                Path pathFileNew = Files.createFile(pathFile);
                LOGGER.info("Created new File."
                        + " FilePath=\"" + pathFileNew + "\"");
                return 1;
            } catch (IOException ex) {
                LOGGER.error("Could not create new File."
                        + " FilePath=\"" + pathFile + "\""
                        + " pathParent=\"" + pathParent + "\""
                        + " IOException=\"" + e.toString() + "\"");
                return -1;
            }
        } catch (Throwable t) {
            String strErrMsg = t.toString();
            LOGGER.error("Could not create new File."
                    + " FilePath=\"" + pathFile + "\""
                    + " Throwable=\"" + strErrMsg + "\"");
            return 0;
        }
    }

    // -------------------------------------------------------------------------------------
    public static byte convertByteArrToByte(byte[] bytArray, int intOffset) {

        if (bytArray == null) {
            return -1;
        }
        ByteBuffer bb = ByteBuffer.wrap(bytArray);
        return bb.get(intOffset);
    }

    // -------------------------------------------------------------------------------------
    public static short convertByteArrToShort(byte[] bytArray, int intOffset) {

        if (bytArray == null) {
            return -1;
        }
        ByteBuffer bb = ByteBuffer.wrap(bytArray);
        return bb.getShort(intOffset);
    }

    // -------------------------------------------------------------------------------------
    public static int convertByteArrToInt(byte[] bytArray, int intOffset) {

        if (bytArray == null) {
            return -1;
        }

        ByteBuffer bb = ByteBuffer.wrap(bytArray);
        int intValue = bb.getInt(intOffset);

        if (intValue > Integer.MAX_VALUE) { // (2^31)-1 or 2 147 483 647 or 0x7FFFFFFF
            LOGGER.error("Value is too big."
                    + " bytArray=\"" + Utils.convertBytesToStringHex(bytArray) + "\""
                    + " intValue=" + intValue);
            return -1;
        }
        return intValue;
    }

    // -------------------------------------------------------------------------------------
    public static String convertBytesToStringHex(byte[] byteArray) {

        return convertBytesToStringHexLimit(byteArray, 0);
    }

    // -------------------------------------------------------------------------------------
    public static String convertBytesToStringHexLimit(byte[] byteArray, int intLimit) {

        if (byteArray == null) {
            return null;
        }
        if (byteArray.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int intCount = 0;
        for (byte b : byteArray) {
            if (intCount > 0) {
                sb.append(":");
            }
            intCount++;
            sb.append(String.format("%02X", b));
            if (intLimit > 0 && intCount == intLimit) {
                break;
            }
        }
        return sb.toString();
    }
    // -------------------------------------------------------------------------------------
}
