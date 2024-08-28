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

public class Chunk_bKGD extends Chunk {
// PNG chunk bKGD Background color
// 
// The bKGD chunk specifies a default background color to present the image against.
//
// Color types 0 and 4
// Greyscale 2 bytes
// Color types 2 and 6
// Red 2 bytes
// Green 2 bytes
// Blue	2 bytes
// Color type 3
// Palette index 1 byte

    private short shtGreyscale;
    private short shtBackgroundRed;
    private short shtBackgroundGreen;
    private short shtBackgroundBlue;
    private byte bytPaletteindex;

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_bKGD(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc,
            ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        Chunk chunk = omapChunks.get("IHDR");
        IHDR chunkIHDR;
        if (chunk instanceof IHDR) {
            chunkIHDR = (IHDR) chunk;
        } else {
            this.sbError.append("Missing IHDR.").append("\n");
            return;
        }
        byte bytColor = chunkIHDR.getColor();

        int intDataLen = bytData.length;
        switch (bytColor) {
            case 0:
            case 4:
                if (intDataLen != 2) {
                    this.sbError.append("Incorrect Data size for Greyscale image:").append(intDataLen);
                }
                this.shtGreyscale = Utils.convertByteArrToShort(bytData, 0);
                this.sbDataValues.append("Greyscale=").append(this.shtGreyscale);
                break;
            case 2:
            case 6:
                if (intDataLen != 6) {
                    sbError.append("Incorrect Data size Color image:").append(intDataLen);
                }
                this.shtBackgroundRed = Utils.convertByteArrToShort(bytData, 0);
                this.shtBackgroundGreen = Utils.convertByteArrToShort(bytData, 2);
                this.shtBackgroundBlue = Utils.convertByteArrToShort(bytData, 4);
                this.sbDataValues.append("BackgroundRed=").append(this.shtBackgroundRed).append("\n");
                this.sbDataValues.append("BackgroundGreen=").append(this.shtBackgroundGreen).append("\n");
                this.sbDataValues.append("BackgroundBlue=").append(this.shtBackgroundBlue);
                break;
            case 3:
                if (intDataLen != 1) {
                    this.sbError.append("Incorrect Data size Palette index:").append(intDataLen);
                }
                this.bytPaletteindex = Utils.convertByteArrToByte(bytData, 0);
                this.sbDataValues.append("Paletteindex=").append(this.bytPaletteindex);
                break;
            default:
                this.sbError.append("Incorrect Color:").append(bytColor).append("\n");
                break;
        }
    }

    // -------------------------------------------------------------------------------------
    @Override
    public String getDataValues() {

        return this.sbDataValues.toString();
    }
    // -------------------------------------------------------------------------------------
}
