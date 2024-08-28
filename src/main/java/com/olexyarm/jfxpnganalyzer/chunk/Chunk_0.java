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

import com.olexyarm.jfxpnganalyzer.Utils;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chunk_0 extends Chunk {
// PNG Signature

    private static final Logger LOGGER = LoggerFactory.getLogger(Chunk_0.class);

    private final static byte[] bytPngSignature = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    // private static int[] intPngSignature ={137, 80, 78, 71, 13, 10, 26, 10};
    private final static String STR_FIRTS_BYTES = "PNG";

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_0(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc,
            ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        if (intDataLen != 8) {
            this.sbError.append("Incorrect Data size:").append(intDataLen);
        }

        // Analyze First bytes
        byte[] abytCopy = Arrays.copyOfRange(bytData, 1, 4);
        String strFirstbytes = "";
        try {
            strFirstbytes = new String(abytCopy, "US-ASCII");
            this.sbDataValues.append("FirstBytes=").append(strFirstbytes).append("\n");
        } catch (UnsupportedEncodingException e) {
            String strErrMsg = "Could not convert first bites file signature."
                    + " FileSignature=\"" + Utils.convertBytesToStringHex(bytData) + "\""
                    + " UnsupportedEncodingException=\"" + e.toString() + "\"";
            LOGGER.error(strErrMsg);
            this.sbError.append("First bytes convertion eror.");
            // Using CRC value to indicate incorrect PNG signature
            this.setValidCrC(false);
        }

        if (!STR_FIRTS_BYTES.equals(strFirstbytes)) {
            String strErrMsg = "First bytes are incorrect."
                    + " FirstBytes=\"" + strFirstbytes + "\""
                    + " FileSignature=\"" + Utils.convertBytesToStringHex(bytData) + "\"";
            LOGGER.error(strErrMsg);
            this.sbError.append("Bad first bytes:").append(strFirstbytes);
            // Using CRC value to indicate incorrect PNG signature
            this.setValidCrC(false);
        }
        for (int i = 0; i < 8; i++) {
            if (bytData[i] != bytPngSignature[i]) {
                String strErrMsg = "File has incorrect signature."
                        + " i=" + i
                        + " bytFileSignature=\"" + Utils.convertBytesToStringHex(bytData) + "\""
                        + " bytPngSignature=\"" + Utils.convertBytesToStringHex(bytPngSignature) + "\"";
                LOGGER.error(strErrMsg);
                this.sbError.append("Bad file signature:\n").append(Utils.convertBytesToStringHex(bytData));
                this.setValidCrC(false);
                return;
            }
        }
        this.sbDataValues.append("File signature:=").append("\n").append(Utils.convertBytesToStringHex(bytData)).append("\n");
        this.setValidCrC(true);
        LOGGER.debug("File has correct PNG signature."
                + " bytFileSignature=\"" + Utils.convertBytesToStringHex(bytData) + "\""
                + " bytPngSignature=\"" + Utils.convertBytesToStringHex(bytPngSignature) + "\"");
    }

    // -------------------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------------------
    @Override
    public String getDataValues() {

        return sbDataValues.toString();
    }
    // -------------------------------------------------------------------------------------
}
