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
package com.olexyarm.jfxpnganalyzer.chunk;

import com.olexyarm.jfxpnganalyzer.Settings;
import com.olexyarm.jfxpnganalyzer.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chunk {

    private static final Logger LOGGER = LoggerFactory.getLogger(Chunk.class);

    private final int intSeq;
    private final int intLen;
    private final byte[] bytType;
    private final byte[] bytData;
    private final int intCrc;

    private String strType = null;
    private String strSdata = null;

    private boolean booCrcValid;

    public StringBuilder sbError = new StringBuilder();

    private static final byte BYTE_POS = 5;
    private static final byte MASK = (byte) (1 << BYTE_POS);

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc) {

        this.intSeq = intSeq;
        this.intLen = intLen;
        this.bytType = bytType;
        this.bytData = bytData;
        this.intCrc = intCrc;

        LOGGER.debug("Chunk constructor."
                + " intSeq=" + intSeq
                + " intLen=" + intLen
                + " bytData=\"" + Utils.convertBytesToStringHexLimit(bytType, Settings.INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX) + "\""
                + " bytData=\"" + Utils.convertBytesToStringHexLimit(bytData, Settings.INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX) + "\""
                + " intCrc=" + intCrc
        );
    }

    // -------------------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------------------
    public int getSeq() {
        return this.intSeq;
    }

    public int getLength() {
        return this.intLen;
    }

    public String getType() {

        if (this.strType == null) {
            if (this.bytType == null || this.bytType.length == 0) {
                this.strType = "";
            }
            this.strType = new String(this.bytType);
        }
        return this.strType;
    }

    public boolean isCritical() {

        boolean booCritical = !((this.bytType[0] & MASK) > 0);
        return booCritical;
    }

    public boolean isPublic() {

        boolean booPublic = !((this.bytType[1] & MASK) > 0);
        return booPublic;
    }

    public boolean isPng() {

        boolean booPng = !((this.bytType[2] & MASK) > 0);
        return booPng;
    }

    public boolean isSafeToCopy() {

        boolean booSafeToCopy = (this.bytType[3] & MASK) > 0;
        return booSafeToCopy;
    }

    public int getCrc() {
        return this.intCrc;
    }

    public byte[] getData() {
        return this.bytData;
    }

    public String getSdata() {

        if (this.strSdata == null) {
            if (this.intLen <= 0) {
                return "";
            }
            if (this.bytData == null || this.bytData.length == 0) {
                return "";
            }
            this.strSdata = Utils.convertBytesToStringHexLimit(this.bytData, Settings.INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX);
        }
        return this.strSdata;
    }

    public String getDataValues() {
        return "";
    }

    public StringBuilder getError() {
        return this.sbError;
    }

    public void setValidCrC(boolean booCrcValid) {
        this.booCrcValid = booCrcValid;
    }

    public boolean isValidCrC() {
        return this.booCrcValid;
    }

    // -------------------------------------------------------------------------------------
}
