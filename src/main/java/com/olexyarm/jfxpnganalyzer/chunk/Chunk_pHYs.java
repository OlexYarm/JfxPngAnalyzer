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

public class Chunk_pHYs extends Chunk {
// PNG chunk  pHYs Physical pixel dimensions
//
// Name	Size
// Pixels per unit, X axis	4 bytes (PNG four-byte unsigned integer)
// Pixels per unit, Y axis	4 bytes (PNG four-byte unsigned integer)
// Unit specifier	1 byte
// 0	unit is unknown
// 1	unit is the metre

    int intPixelsPerUnitX;
    int intPixelsPerUnitY;
    byte bytUnitSpecifier;

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_pHYs(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc, ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        if (intDataLen != 9) {
            this.sbError.append("Incorrect pHYs lenght:").append(intDataLen);
        }

        this.intPixelsPerUnitX = Utils.convertByteArrToInt(bytData, 0);
        this.intPixelsPerUnitY = Utils.convertByteArrToInt(bytData, 4);
        this.bytUnitSpecifier = Utils.convertByteArrToByte(bytData, 8);
        if (this.bytUnitSpecifier != 0 && this.bytUnitSpecifier != 1) {
            sbError.append("Incorrect Unit Specifier:").append(this.bytUnitSpecifier).append("\n");
        }
    }

    // -------------------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------------------
    @Override
    public String getDataValues() {

        this.sbDataValues.append("PixelsPerUnitX=").append(this.intPixelsPerUnitX).append("\n");
        this.sbDataValues.append("PixelsPerUnitY=").append(this.intPixelsPerUnitY).append("\n");
        this.sbDataValues.append("UnitSpecifier=").append(this.bytUnitSpecifier).append("\n");
        return sbDataValues.toString();
    }
    // -------------------------------------------------------------------------------------
}
