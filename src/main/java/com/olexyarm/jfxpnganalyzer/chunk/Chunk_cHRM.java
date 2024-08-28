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

public class Chunk_cHRM extends Chunk {
// PNG chunk cHRM Primary chromaticities and white point
//
// White point x	4 bytes
// White point y	4 bytes
// Red x	4 bytes
// Red y	4 bytes
// Green x	4 bytes
// Green y	4 bytes
// Blue x	4 bytes
// Blue y	4 bytes

    private final int intWhitePointX;
    private final int intWhitePointY;
    private final int intRedX;
    private final int intRedY;
    private final int intGreenX;
    private final int intGreenY;
    private final int intBlueX;
    private final int intBlueY;

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_cHRM(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc,
            ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        if (intDataLen != 32) {
            this.sbError.append("Incorrect Data size:").append(intDataLen);
        }
        this.intWhitePointX = Utils.convertByteArrToInt(bytData, 0);
        this.intWhitePointY = Utils.convertByteArrToInt(bytData, 4);
        this.intRedX = Utils.convertByteArrToInt(bytData, 8);
        this.intRedY = Utils.convertByteArrToInt(bytData, 12);
        this.intGreenX = Utils.convertByteArrToInt(bytData, 16);
        this.intGreenY = Utils.convertByteArrToInt(bytData, 20);
        this.intBlueX = Utils.convertByteArrToInt(bytData, 24);
        this.intBlueY = Utils.convertByteArrToInt(bytData, 28);

        this.sbDataValues.append("WhitePointX=").append(this.intWhitePointX).append("\n");
        this.sbDataValues.append("WhitePointY=").append(this.intWhitePointY).append("\n");
        this.sbDataValues.append("RedX=").append(this.intRedX).append("\n");
        this.sbDataValues.append("RedY=").append(this.intRedY).append("\n");
        this.sbDataValues.append("GreenX=").append(this.intGreenX).append("\n");
        this.sbDataValues.append("GreenY=").append(this.intGreenY).append("\n");
        this.sbDataValues.append("BlueX=").append(this.intBlueX).append("\n");
        this.sbDataValues.append("BlueY=").append(this.intBlueY).append("\n");
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
