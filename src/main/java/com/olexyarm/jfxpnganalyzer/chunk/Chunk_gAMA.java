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
import javafx.collections.ObservableMap;

public class Chunk_gAMA extends Chunk {
// PNG chunk gAMA Image gamma
//
// The value is encoded as a PNG four-byte unsigned integer, representing the gamma value times 100000.

    private int intGama;
    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_gAMA(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc,
            ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        if (intDataLen != 4) {
            this.sbError.append("Incorrect Data size:").append(intDataLen);
        }
        this.intGama = Utils.convertByteArrToInt(bytData, 0);
        this.sbDataValues.append("Gama=").append(this.intGama).append("\n");
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
