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
import static java.lang.Integer.min;
import javafx.collections.ObservableMap;

public class Chunk_hIST extends Chunk {
// PNG chunk hIST Image histogram
//
// The hIST chunk contains a series of two-byte unsigned integers

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_hIST(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc, ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        if ((intDataLen % 2) != 0) {
            this.sbError.append("Data Len is incorrect.").append("\n").append("Data lenght:").append(intDataLen);
        }
        int intPairsCount = intDataLen / 2;
        int intEntriesToShow = min(intPairsCount, Settings.INT_PALLETE_ENTRIES_TO_SHOW);
        for (int i = 0; i < intEntriesToShow; i++) {
            int intFreq = Utils.convertByteArrToShort(bytData, i * 2);
            this.sbDataValues.append("Freq=").append(intFreq).append("\n");
            if (i >= intEntriesToShow) {
                this.sbDataValues.append("...").append("\n");
                break;
            }
        }
    }

    // -------------------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------------------
    @Override
    public String getDataValues() {

        return this.sbDataValues.toString();
    }
    // -------------------------------------------------------------------------------------
}
