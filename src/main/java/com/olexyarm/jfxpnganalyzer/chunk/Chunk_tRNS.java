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

public class Chunk_tRNS extends Chunk {
// PNG chunk PLTE Palette
//
// The tRNS chunk specifies either alpha values that are associated with palette entries
// (for indexed-color images) or a single transparent color (for greyscale and truecolor images).
// The tRNS chunk contains:
// Color type 0
// Grey sample value	2 bytes
// Color type 2
// Red sample value	2 bytes
// Green sample value	2 bytes
// Blue sample value	2 bytes
// Color type 3
// Alpha for palette index 0	1 byte
// Alpha for palette index 1	1 byte
// ...etc...	1 byte

    private short shtGreySample;
    private short shtRedSample;
    private short shtGreenSample;
    private short shtBlueSample;
    private byte[] abytAlpha;

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_tRNS(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc, ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;

        Chunk chunk = omapChunks.get("IHDR");
        IHDR chunkIHDR;
        if (chunk instanceof IHDR) {
            chunkIHDR = (IHDR) chunk;
        } else {
            this.sbError.append("Missing IHDR.").append("\n");
            return;
        }

        byte bytColor = chunkIHDR.getColor();
        switch (bytColor) {
            case 0:
                if (intDataLen != 2) {
                    this.sbError.append("Incorrect Data size for Greyscale image:").append(intDataLen);
                }
                this.shtGreySample = Utils.convertByteArrToShort(bytData, 0);
                this.sbDataValues.append("Greyscale=").append(this.shtGreySample);
                break;
            case 2:
                if (intDataLen != 6) {
                    this.sbError.append("Incorrect Data size for Greyscale image:").append(intDataLen);
                }
                this.shtRedSample = Utils.convertByteArrToShort(bytData, 0);
                this.shtGreenSample = Utils.convertByteArrToShort(bytData, 2);
                this.shtBlueSample = Utils.convertByteArrToShort(bytData, 4);
                this.sbDataValues.append("RedSample=").append(this.shtRedSample);
                this.sbDataValues.append("GreenSample=").append(this.shtGreenSample);
                this.sbDataValues.append("BlueSample=").append(this.shtBlueSample);
                break;
            case 3:
                int intEntriesToShow = min(intDataLen, Settings.INT_PALLETE_ENTRIES_TO_SHOW);
                this.abytAlpha = new byte[intDataLen];
                for (int i = 0; i < intDataLen; i++) {
                    this.abytAlpha[i] = bytData[i];
                    if (i < intEntriesToShow) {
                        this.sbDataValues.append("Alpha=").append(bytData[i]).append("\n");
                    }
                }
                if (intDataLen > intEntriesToShow) {
                    this.sbDataValues.append("...").append("\n");
                }
                break;
            case 4:
            case 6:
                this.sbError.append("PLTE not allowed for color:").append(bytColor).append("\n");
                break;
            default:
                this.sbError.append("Incorrect Color:").append(bytColor).append("\n");
                break;
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
