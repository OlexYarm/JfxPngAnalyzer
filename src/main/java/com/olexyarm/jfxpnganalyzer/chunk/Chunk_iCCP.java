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

import java.io.UnsupportedEncodingException;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chunk_iCCP extends Chunk {
// PNG chunk iCCP Embedded ICC profile
//
// Profile name	1-79 bytes (character string)
// Null separator	1 byte (null character)
// Compression method	1 byte
// Compressed profile	n bytes

    private static final Logger LOGGER = LoggerFactory.getLogger(Chunk_iCCP.class);

    private String strProfileName;
    private final byte bytCompressionMethod;

    // TODO: uncompress and parse Profile
    private byte[] abytCompressedProfile;

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_iCCP(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc, ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        int i;
        for (i = 0; i < intDataLen; i++) {
            byte bytByte = bytData[i];
            if (bytByte == 0) {
                try {
                    this.strProfileName = new String(bytData, 0, i, "US-ASCII");
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error("Could not decode ICC ProfileName."
                            + " intSeq=\"" + intSeq + "\""
                            + " UnsupportedEncodingException=\"" + e.toString() + "\"");
                    sbError.append("UnsupportedEncodingException:").append(e.toString()).append("\n");
                }
                break;
            }
        }

        this.bytCompressionMethod = bytData[i];
        if (this.bytCompressionMethod != 0) {
            sbError.append("Incorrect Compression Method:").append(this.bytCompressionMethod).append("\n");
        }
        //TO DO: Get abytCompressedProfile
    }

    // -------------------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------------------
    @Override
    public String getDataValues() {

        this.sbDataValues.append("ProfileName=").append(this.strProfileName).append("\n");
        this.sbDataValues.append("CompressionMethod=").append(this.bytCompressionMethod).append("\n");
        return sbDataValues.toString();
    }
    // -------------------------------------------------------------------------------------
}
