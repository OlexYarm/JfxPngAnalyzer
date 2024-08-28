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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings {

    private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);

    // -------------------------------------------------------------------------------------
    private static final String STR_UNKNOWN = "UNKNOWN";
    public static String STR_VERSION = STR_UNKNOWN;
    public static String STR_BUILD_TIME = STR_UNKNOWN;
    public static String STR_BUILD_JAVA_HOME = STR_UNKNOWN;
    public static String STR_BUILD_OS = STR_UNKNOWN;

    private static final String STR_VERSION_FILENAME = "version.txt";

    static {
        try (InputStream is = Settings.class.getResourceAsStream(STR_VERSION_FILENAME); BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String strLine;
            int intLineCount = 0;
            while ((strLine = br.readLine()) != null) {
                intLineCount++;
                if (strLine.startsWith("Build.version")) {
                    STR_VERSION = strLine;
                } else if (strLine.startsWith("Build.date")) {
                    STR_BUILD_TIME = strLine;
                } else if (strLine.startsWith("Build.JavaHome")) {
                    STR_BUILD_JAVA_HOME = strLine;
                } else if (strLine.startsWith("Build.OS")) {
                    STR_BUILD_OS = strLine;
                } else {
                    LOGGER.info("Unknown line in Version file."
                            + " VersionFileName=\"" + STR_VERSION_FILENAME + "\""
                            + " LineNumber=" + intLineCount
                            + " Line=\"" + strLine + "\""
                    );
                }
            }
            LOGGER.info("Parsed Version file."
                    + " VersionFileName=\"" + STR_VERSION_FILENAME + "\""
                    + " STR_VERSION=\"" + STR_VERSION + "\""
                    + " STR_BUILD_TIME=\"" + STR_BUILD_TIME + "\""
                    + " STR_BUILD_JAVA_HOME=\"" + STR_BUILD_JAVA_HOME + "\""
                    + " STR_BUILD_OS=\"" + STR_BUILD_OS + "\"");
        } catch (IOException ex) {
            LOGGER.error("Could not read Version file."
                    + " VersionFilePath=\"" + STR_VERSION_FILENAME + "\""
                    + " IOException=\"" + ex.toString() + "\"");
        } catch (Throwable t) {
            LOGGER.error("Could not read Version file."
                    + " VersionFilePath=\"" + STR_VERSION_FILENAME + "\""
                    + " Throwable=\"" + t.toString() + "\"");
        }
    }

    // -------------------------------------------------------------------------------------
    // Unmodifiable settings
    // -------------------------------------------------------------------------------------
    private static final String STR_DIRECTORY_USER_HOME = "user.home";
    public static final String STR_DIRECTORY_USER_HOME_PATH = System.getProperty(STR_DIRECTORY_USER_HOME);

    private static final String STR_DIRECTORY_USER_DIR = "user.dir";
    public static final String STR_DIRECTORY_USER_HOME_DIR = System.getProperty(STR_DIRECTORY_USER_DIR);

    public static final String STR_APP_TITLE = "Open JFX PNG Analyzer";
    public static final String STR_JFX_EDITOR_SETTINGS_DIRECTORY = "JfxPngAnalyzer";

    // -------------------------------------------------------------------------------------
    private static final String STR_SETTINGS_FILE_NAME = "Settings.properties";
    private static final String STR_SETTINGS_FILE_PATH = Settings.STR_DIRECTORY_USER_HOME_PATH
            + File.separator + STR_JFX_EDITOR_SETTINGS_DIRECTORY
            + File.separator + STR_SETTINGS_FILE_NAME;

    // -------------------------------------------------------------------------------------
    // Screen sizes
    private static final java.awt.Dimension DIM_SCREEN_SIZE = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    private static final double DOUBLE_SCREEN_WIDTH = DIM_SCREEN_SIZE.getWidth();
    private static final double DOUBLE_SCREEN_HIGHT = DIM_SCREEN_SIZE.getHeight();

    private static final Rectangle2D RECT_SCREEN_BOUNDS = Screen.getPrimary().getBounds();

    private static final double DOUBLE_SCREEN_SCALED_WIDTH = RECT_SCREEN_BOUNDS.getWidth();
    private static final double DOUBLE_SCREEN_SCALED_HIGHT = RECT_SCREEN_BOUNDS.getHeight();

    public static final int INT_WINDOW_WIDTH_MAX = (int) DOUBLE_SCREEN_SCALED_WIDTH; //8000;
    public static final int INT_WINDOW_HIGH_MAX = (int) DOUBLE_SCREEN_SCALED_HIGHT; //4000;

    // -------------------------------------------------------------------------------------
    private static final String STR_SETTINGS_FILE_NAME_TABS = "OpenTabs.properties";
    public static final String STR_SETTINGS_FILE_PATH_TABS = Settings.STR_DIRECTORY_USER_HOME_PATH
            + File.separator + STR_JFX_EDITOR_SETTINGS_DIRECTORY
            + File.separator + STR_SETTINGS_FILE_NAME_TABS;

    // -------------------------------------------------------------------------------------
    public static final int INT_LOG_MESSAGE_LENGTH_MAX = 200;
    public static final int INT_SLEEP_DELAY_SHOW_ERROR = 5000;
    public static final int INT_SLEEP_DELAY_SHOW_INFO = 1000;

    // -------------------------------------------------------------------------------------
    // For future use
    public static final Charset CHARSET_OS_DEFAULT = Charset.defaultCharset();
    public static final String STR_CHARSET_OS_DEFAULT = CHARSET_OS_DEFAULT.name();
    public static final String STR_CHARSET_DEFAULT = "UTF-8";

    // -------------------------------------------------------------------------------------
    // Modifiable settings
    // -------------------------------------------------------------------------------------
    // Main APP window
    private static final String STR_PROP_NAME_WINDOW_WIDTH = "window_widh";
    public static int INT_WINDOW_WIDTH = 800;
    public static int INT_WINDOW_WIDTH_DEFAULT = 800;

    private static final String STR_PROP_NAME_WINDOW_HIGH = "window_high";
    public static int INT_WINDOW_HIGH = 600;
    public static int INT_WINDOW_HIGH_DEFAULT = 600;

    private static final String STR_PROP_NAME_WINDOW_POSITION_X = "window_position_x";
    private static final int INT_WINDOW_POSITION_X_DEFAULT = 100;
    public static int INT_WINDOW_POSITION_X = 100;

    private static final String STR_PROP_NAME_WINDOW_POSITION_Y = "window_position_y";
    private static final int INT_WINDOW_POSITION_Y_DEFAULT = 100;
    public static int INT_WINDOW_POSITION_Y = 100;

    private static final String STR_PROP_NAME_STAGE_MAXIMAZED = "stage_miximized";
    public static boolean BOO_STAGE_MAXIMIZED;

    // -------------------------------------------------------------------------------------
    // About window
    public static final int INT_WINDOW_ABOUT_WIDTH = 350;
    public static final int INT_WINDOW_ABOUT_HIGH = 300;

    // -------------------------------------------------------------------------------------
    private static final String STR_PROP_NAME_TABS_MAX = "Tabs_max";
    private static final int INT_TABS_COUNT_MAX_MAX = 50;
    private static final int INT_TABS_COUNT_MAX_DEFAULT = 3;
    public static int INT_TABS_COUNT_MAX;

    // -------------------------------------------------------------------------------------
    private static final String STR_PROP_NAME_LOG_LEVEL = "Log_level";
    private static final String STR_LOG_LEVEL_DEFAULT = "D";
    private static String STR_LOG_LEVEL;

    // -------------------------------------------------------------------------------------
    private static final String STR_PROP_NAME_CHUNK_ERRORS_MAX = "chunk_errors_max";
    private static final int INT_CHUNK_ERRORS_MAX_DEFAULT = 10;
    private static final int INT_CHUNK_ERRORS_MAX_LIMIT = 50;
    public static int INT_CHUNK_ERRORS_MAX = 10;

    // -------------------------------------------------------------------------------------
    private static final String STR_PROP_NAME_PALLETE_ENTRIES_TO_SHOW = "pallete_entries_to_show";
    private static final int INT_PALLETE_ENTRIES_TO_SHOW_DEFAULT = 5;
    private static final int INT_PALLETE_ENTRIES_TO_SHOW_LIMIT = 256;
    public static int INT_PALLETE_ENTRIES_TO_SHOW = 5;

    // -------------------------------------------------------------------------------------
    private static final String STR_PROP_NAME_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX = "chunk_data_hex_to_string_len_max";
    private static final int INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX_DEFAULT = 10;
    private static final int INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX_LIMIT = 200;
    public static int INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX = 10;

    // -------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------
    public static int INT_TABS_OPENED_TOTAL_COUNT = 0;
    public static int INT_TABS_OPENED_COUNT = 0;
    public static int INT_TABS_CLOSED_COUNT = 0;
    public static int INT_FILES_NEW_COUNT = 0;
    public static int INT_FILES_OPENED_TOTAL_COUNT = 0;

    private static final Properties prop = new Properties();

    // -------------------------------------------------------------------------------------
    public static void load() {

        Path pathFileSettings = FileSystems.getDefault().getPath(STR_SETTINGS_FILE_PATH);

        if (!Utils.checkFileExist("SettingsLoad", pathFileSettings)) {
            LOGGER.info("File Settings does not exist.");
            Utils.createFile(pathFileSettings);
            return;
        }
        LOGGER.info("Window size."
                + " DIM_SCREEN_SIZE=\"" + DIM_SCREEN_SIZE + "\""
                + " DOUBLE_SCREEN_WIDTH=" + DOUBLE_SCREEN_WIDTH
                + " DOUBLE_SCREEN_HIGHT=" + DOUBLE_SCREEN_HIGHT
                + " RECT_SCREEN_BOUNDS=\"" + RECT_SCREEN_BOUNDS + "\""
                + " DOUBLE_SCREEN_SCALED_WIDTH=" + DOUBLE_SCREEN_SCALED_WIDTH
                + " DOUBLE_SCREEN_SCALED_HIGHT=" + DOUBLE_SCREEN_SCALED_HIGHT
                + " INT_WINDOW_WIDTH_MAX=" + DOUBLE_SCREEN_SCALED_WIDTH
                + " INT_WINDOW_HIGH_MAX=" + DOUBLE_SCREEN_SCALED_HIGHT);

        File fileSettings = new File(STR_SETTINGS_FILE_PATH);
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileSettings));) {
            prop.load(bis);
        } catch (Exception ex) {
            LOGGER.error("Could not open Settings file."
                    + " pathFileSettings=\"" + pathFileSettings + "\""
                    + " Exception=\"" + ex.toString() + "\"");
            return;
        }

        String strPropValue;
        try {
            INT_WINDOW_WIDTH = getPropValueInt(STR_PROP_NAME_WINDOW_WIDTH, "" + INT_WINDOW_WIDTH_DEFAULT, INT_WINDOW_WIDTH_MAX);
            INT_WINDOW_HIGH = getPropValueInt(STR_PROP_NAME_WINDOW_HIGH, "" + INT_WINDOW_HIGH_DEFAULT, INT_WINDOW_HIGH_MAX);

            INT_WINDOW_POSITION_X = getPropValueInt(STR_PROP_NAME_WINDOW_POSITION_X, "" + INT_WINDOW_POSITION_X_DEFAULT, INT_WINDOW_WIDTH_MAX / 2);
            INT_WINDOW_POSITION_Y = getPropValueInt(STR_PROP_NAME_WINDOW_POSITION_Y, "" + INT_WINDOW_POSITION_Y_DEFAULT, INT_WINDOW_HIGH_MAX / 2);

            BOO_STAGE_MAXIMIZED = getPropValueBoolean(STR_PROP_NAME_STAGE_MAXIMAZED, "N");

            INT_TABS_COUNT_MAX = getPropValueInt(STR_PROP_NAME_TABS_MAX, "" + INT_TABS_COUNT_MAX_DEFAULT, INT_TABS_COUNT_MAX_MAX);

            strPropValue = prop.getProperty(STR_PROP_NAME_LOG_LEVEL);
            if (strPropValue == null) {
                LOGGER.debug("Could not find property \"" + STR_PROP_NAME_LOG_LEVEL + "\"");
                STR_LOG_LEVEL = STR_LOG_LEVEL_DEFAULT;
            } else {
                STR_LOG_LEVEL = strPropValue;
            }
            applyLogLevel(STR_LOG_LEVEL);

            INT_CHUNK_ERRORS_MAX = getPropValueInt(STR_PROP_NAME_CHUNK_ERRORS_MAX,
                    "" + INT_CHUNK_ERRORS_MAX_DEFAULT, INT_CHUNK_ERRORS_MAX_LIMIT);
            INT_PALLETE_ENTRIES_TO_SHOW = getPropValueInt(STR_PROP_NAME_PALLETE_ENTRIES_TO_SHOW,
                    "" + INT_PALLETE_ENTRIES_TO_SHOW_DEFAULT, INT_PALLETE_ENTRIES_TO_SHOW_LIMIT);
            INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX = getPropValueInt(STR_PROP_NAME_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX,
                    "" + INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX_DEFAULT, INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX_LIMIT);

        } catch (Exception ex) {
            LOGGER.error("Could not process property."
                    + " IOException=\"" + ex.toString() + "\"");
        }

    }

    // -------------------------------------------------------------------------------------
    public static void save() {

        if (!BOO_STAGE_MAXIMIZED) {
            prop.setProperty(STR_PROP_NAME_WINDOW_WIDTH, "" + INT_WINDOW_WIDTH);
            prop.setProperty(STR_PROP_NAME_WINDOW_HIGH, "" + INT_WINDOW_HIGH);

            prop.setProperty(STR_PROP_NAME_WINDOW_POSITION_X, "" + INT_WINDOW_POSITION_X);
            prop.setProperty(STR_PROP_NAME_WINDOW_POSITION_Y, "" + INT_WINDOW_POSITION_Y);
        }
        prop.setProperty(STR_PROP_NAME_STAGE_MAXIMAZED, BOO_STAGE_MAXIMIZED ? "Y" : "N");

        if (INT_TABS_COUNT_MAX <= 0) {
            INT_TABS_COUNT_MAX = 1;
        }
        if (INT_TABS_COUNT_MAX > INT_TABS_COUNT_MAX_MAX) {
            INT_TABS_COUNT_MAX = INT_TABS_COUNT_MAX_MAX;
        }
        prop.setProperty(STR_PROP_NAME_TABS_MAX, "" + INT_TABS_COUNT_MAX);

        prop.setProperty(STR_PROP_NAME_LOG_LEVEL, STR_LOG_LEVEL);
        prop.setProperty(STR_PROP_NAME_CHUNK_ERRORS_MAX, "" + INT_CHUNK_ERRORS_MAX);
        prop.setProperty(STR_PROP_NAME_PALLETE_ENTRIES_TO_SHOW, "" + INT_PALLETE_ENTRIES_TO_SHOW);

        LOGGER.debug("Saving Properties file."
                + " prop=\"" + prop.toString() + "\"");

        try {
            OutputStream os = new FileOutputStream(STR_SETTINGS_FILE_PATH);
            prop.store(os, "");
        } catch (IOException ex) {
            LOGGER.error("Could not save Properties file."
                    + " IOException=\"" + ex.toString() + "\"");
        }

    }

    // -------------------------------------------------------------------------------------
    public static String osName() {
        return System.getProperty("os.name");
    }

    // -------------------------------------------------------------------------------------
    public static String javaVersion() {
        return System.getProperty("java.version");
    }

    // -------------------------------------------------------------------------------------
    public static String javafxVersion() {
        return System.getProperty("javafx.version");
    }

    // -------------------------------------------------------------------------------------
    private static boolean getPropValueBoolean(String strPropName, String strPropValueDefault) throws Exception {

        String strPropValue = prop.getProperty(strPropName, strPropValueDefault);
        boolean booValue;
        if (strPropValue == null) {
            strPropValue = strPropValueDefault;
        }
        booValue = switch (strPropValue) {
            case "Y", "YES" ->
                true;
            case "N", "NO" ->
                false;
            default ->
                true;
        };
        return booValue;
    }

    // -------------------------------------------------------------------------------------
    private static int getPropValueInt(String strPropName, String strPropValueDefault, int IntPropValueMax) throws Exception {

        String strPropValue = prop.getProperty(strPropName, strPropValueDefault);
        if (strPropValue == null) {
            LOGGER.debug("Could not find property \"" + strPropName + "\"");
            strPropValue = strPropValueDefault;
        }
        int intPropValue = Integer.parseInt(strPropValue);
        if (intPropValue <= 0 || intPropValue > IntPropValueMax) {
            LOGGER.error("Properties value is incorrrect."
                    + " PropName=\"" + strPropName + "\""
                    + " IntPropValueMax=\"" + IntPropValueMax + "\""
                    + " strPropValue=\"" + strPropValue + "\""
                    + " intPropValue=\"" + intPropValue + "\"");
            throw new Exception("Properties value is incorrrect.");
        }
        LOGGER.debug("Got int Properties value."
                + " PropName=\"" + strPropName + "\""
                + " IntPropValueMax=\"" + IntPropValueMax + "\""
                + " strPropValue=\"" + strPropValue + "\""
                + " intPropValue=\"" + intPropValue + "\"");
        return intPropValue;
    }

    // -------------------------------------------------------------------------------------
    private static double getPropValueDouble(String strPropName, String strPropValueDefault, double dblPropValueMax) throws Exception {

        String strPropValue = prop.getProperty(strPropName, strPropValueDefault);
        if (strPropValue == null) {
            LOGGER.debug("Could not find property \"" + strPropName + "\"");
            strPropValue = strPropValueDefault;
        }
        double dblPropValue = Double.parseDouble(strPropValue);
        if (dblPropValue <= 0 || dblPropValue > dblPropValueMax) {
            LOGGER.error("Properties value is incorrrect."
                    + " PropName=\"" + strPropName + "\""
                    + " dblPropValueMax=\"" + dblPropValueMax + "\""
                    + " strPropValue=\"" + strPropValue + "\""
                    + " dblPropValue=\"" + dblPropValue + "\"");
            throw new Exception("Properties value is incorrrect.");
        }
        LOGGER.debug("Got double Properties value."
                + " PropName=\"" + strPropName + "\""
                + " dblPropValueMax=\"" + dblPropValueMax + "\""
                + " strPropValue=\"" + strPropValue + "\""
                + " dblPropValue=\"" + dblPropValue + "\"");
        return dblPropValue;
    }

    // -------------------------------------------------------------------------------------
    public static String getLogLevel() {
        return STR_LOG_LEVEL;
    }

    // -------------------------------------------------------------------------------------
    public static void applyLogLevel(String strLogLevel) {

        String strLogLevelBefore = STR_LOG_LEVEL;
        STR_LOG_LEVEL = strLogLevel;
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Level levelLogBefore = loggerContext.getLogger("com.olexyarm.jfxfilecontenteditor").getLevel();
        LOGGER.debug("applyLogLevel before."
                + " strLogLevel=\"" + strLogLevel + "\""
                + " STR_LOG_LEVEL=\"" + STR_LOG_LEVEL + "\""
                + " levelLogBefore=\"" + levelLogBefore + "\"");

        String strLogLevelStartWith;
        if (strLogLevel == null || strLogLevel.isEmpty()) {
            strLogLevelStartWith = "O";
        } else {
            strLogLevelStartWith = strLogLevel.substring(0, 1);
        }
        switch (strLogLevelStartWith) {
            case "T":
                loggerContext.getLogger("com.olexyarm.jfxfilecontenteditor").setLevel(Level.TRACE);
                break;
            case "D":
                loggerContext.getLogger("com.olexyarm.jfxfilecontenteditor").setLevel(Level.DEBUG);
                break;
            case "I":
                loggerContext.getLogger("com.olexyarm.jfxfilecontenteditor").setLevel(Level.INFO);
                break;
            case "W":
                loggerContext.getLogger("com.olexyarm.jfxfilecontenteditor").setLevel(Level.WARN);
                break;
            case "E":
                loggerContext.getLogger("com.olexyarm.jfxfilecontenteditor").setLevel(Level.ERROR);
                break;
            case "O":
                loggerContext.getLogger("com.olexyarm.jfxfilecontenteditor").setLevel(Level.OFF);
                break;
            default:
                loggerContext.getLogger("com.olexyarm.jfxfilecontenteditor").setLevel(Level.OFF);
                break;
        }
        Level levelLogAfter = loggerContext.getLogger("com.olexyarm.jfxfilecontenteditor").getLevel();
        LOGGER.debug("applyLogLevel After."
                + " strLogLevelBefore=\"" + strLogLevelBefore + "\""
                + " STR_LOG_LEVEL=\"" + STR_LOG_LEVEL + "\""
                + " levelLogBefore=\"" + levelLogBefore + "\""
                + " levelLogAfter=\"" + levelLogAfter + "\"");
    }
    // -------------------------------------------------------------------------------------
}
