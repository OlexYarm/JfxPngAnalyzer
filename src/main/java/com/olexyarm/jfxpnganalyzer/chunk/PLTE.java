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

public class PLTE extends Chunk {
// PNG chunk PLTE Palette
//
// The PLTE chunk contains from 1 to 256 palette entries, each a three-byte series of the form:
// Red 1 byte
// Green 1 byte
// Blue 1 byte

    final byte[] bytRed;
    final byte[] bytGreen;
    final byte[] bytBlue;

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public PLTE(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc, ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        int intEntriesCount = intDataLen / 3;
        if (intEntriesCount > 256) {
            this.sbError.append("To many palette entries:").append(intEntriesCount).append("\n");
            intEntriesCount = 256;
        }
        if (intDataLen % 3 != 0) {
            this.sbError.append("Incorrect PLTE lenght:").append(intDataLen).append("\n");
        }
        int intEntriesToShow = min(intEntriesCount, Settings.INT_PALLETE_ENTRIES_TO_SHOW);

        this.bytRed = new byte[intEntriesCount];
        this.bytGreen = new byte[intEntriesCount];
        this.bytBlue = new byte[intEntriesCount];
        for (int i = 0; i < intEntriesCount; i++) {
            this.bytRed[i] = Utils.convertByteArrToByte(bytData, 0);
            this.bytGreen[i] = Utils.convertByteArrToByte(bytData, 1);
            this.bytBlue[i] = Utils.convertByteArrToByte(bytData, 2);
            if (i < intEntriesToShow) {
                this.sbDataValues.append("Red=").append(this.bytRed[i]).append("\n");
                this.sbDataValues.append("Green=").append(this.bytGreen[i]).append("\n");
                this.sbDataValues.append("Blue=").append(this.bytBlue[i]).append("\n");
            }
        }
        if (intEntriesCount > intEntriesToShow) {
            this.sbDataValues.append("...").append("\n");
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
