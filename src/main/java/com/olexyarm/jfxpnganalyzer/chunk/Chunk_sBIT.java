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

public class Chunk_sBIT extends Chunk {
// PNG chunk sBIT Significant bits
//
// The sBIT chunk contains:
//
// Color type 0
// significant greyscale bits	1 byte
// Color types 2 and 3
// significant red bits	1 byte
// significant green bits	1 byte
// significant blue bits	1 byte
// Color type 4
// significant greyscale bits	1 byte
// significant alpha bits	1 byte
// Color type 6
// significant red bits	1 byte
// significant green bits	1 byte
// significant blue bits	1 byte
// significant alpha bits	1 byte

    private byte bytSignifGreyscale;
    private byte bytSignifRed;
    private byte bytSignifGreen;
    private byte bytSignifBlue;
    private byte bytSignifAlpha;

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_sBIT(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc,
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
                if (intDataLen != 1) {
                    this.sbError.append("Incorrect Data size for Greyscale bits:").append(intDataLen);
                }
                this.bytSignifGreyscale = Utils.convertByteArrToByte(bytData, 0);
                this.sbDataValues.append("SignifGreyscale=").append(this.bytSignifGreyscale);
                break;
            case 2:
            case 3:
                if (intDataLen != 3) {
                    sbError.append("Incorrect Data size Color bits:").append(intDataLen);
                }
                this.bytSignifRed = Utils.convertByteArrToByte(bytData, 0);
                this.bytSignifGreen = Utils.convertByteArrToByte(bytData, 1);
                this.bytSignifBlue = Utils.convertByteArrToByte(bytData, 2);
                this.sbDataValues.append("SignifRed=").append(this.bytSignifRed).append("\n");
                this.sbDataValues.append("SignifGreen=").append(this.bytSignifGreen).append("\n");
                this.sbDataValues.append("SignifBlue=").append(this.bytSignifBlue);
                break;
            case 4:
                if (intDataLen != 2) {
                    this.sbError.append("Incorrect Data size Palette index:").append(intDataLen);
                }
                this.bytSignifGreyscale = Utils.convertByteArrToByte(bytData, 0);
                this.bytSignifAlpha = Utils.convertByteArrToByte(bytData, 1);

                this.sbDataValues.append("SignifGreyscale=").append(this.bytSignifGreyscale);
                this.sbDataValues.append("SignifAlpha=").append(this.bytSignifAlpha);
                break;
            case 6:
                this.bytSignifRed = Utils.convertByteArrToByte(bytData, 0);
                this.bytSignifGreen = Utils.convertByteArrToByte(bytData, 1);
                this.bytSignifBlue = Utils.convertByteArrToByte(bytData, 2);
                this.sbDataValues.append("SignifGreyscale=").append(this.bytSignifGreyscale);

                this.sbDataValues.append("SignifRed=").append(this.bytSignifRed).append("\n");
                this.sbDataValues.append("SignifGreen=").append(this.bytSignifGreen).append("\n");
                this.sbDataValues.append("SignifBlue=").append(this.bytSignifBlue);
                this.sbDataValues.append("SignifAlpha=").append(this.bytSignifAlpha);
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

        return sbDataValues.toString();
    }
    // -------------------------------------------------------------------------------------
}
