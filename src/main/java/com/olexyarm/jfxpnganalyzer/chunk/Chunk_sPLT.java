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

public class Chunk_sPLT extends Chunk {
// PNG chunk sPLT Suggested palette
//
// The four-byte chunk type field contains the hexadecimal values:
//
// Palette name	1-79 bytes (character string)
// Null separator 1 byte (null character)
// Sample depth	1 byte
// Red 1 or 2 bytes
// Green 1 or 2 bytes
// Blue	1 or 2 bytes
// Alpha 1 or 2 bytes
// Frequency 2 bytes    

    private String strPaletteName;
    private byte bytSampleDepth;

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_sPLT(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc,
            ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        if (intDataLen < 9) {
            this.sbError.append("Incorrect Data size:").append(intDataLen);
        }

        int i;
        for (i = 0; i < intDataLen; i++) {
            if (bytData[i] == 0) {
                byte[] abytCopy = Arrays.copyOfRange(bytData, 0, i);
                try {
                    this.strPaletteName = new String(abytCopy, "US-ASCII");
                } catch (UnsupportedEncodingException ex) {
                    this.sbError.append("Incorrect Data size:").append(intDataLen);
                }
                this.bytSampleDepth = Utils.convertByteArrToByte(bytData, i);

                this.sbDataValues.append("PaletteName=").append(this.strPaletteName).append("\n");
                this.sbDataValues.append("SampleDepth=").append(bytSampleDepth).append("\n");
                break;
            }
        }
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
