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

public class IHDR extends Chunk {
// PNG chunk IHDR Image header

    private final static String[] STR_FIELD_NAMES = {"Width", "Height", "Depth", "Compression", "Filter", "Interlace", "Color"};

    private int intWidth;
    private int intHeight;
    private int intDepth;
    private byte bytCompression;
    private byte bytFilter;
    private byte bytInterlace;
    private byte bytColor;
    private String strImageType;

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public IHDR(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc, ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        if (intDataLen != 13) {
            this.sbError.append("Incorrect IHDR lenght:").append(intDataLen).append("\n");
        }
        int i = 0;
        try {
            this.intWidth = Utils.convertByteArrToInt(bytData, 0);
            i++;
            this.intHeight = Utils.convertByteArrToInt(bytData, 4);
            i++;
            this.intDepth = Utils.convertByteArrToByte(bytData, 8);
            i++;
            this.bytColor = Utils.convertByteArrToByte(bytData, 9);
            i++;
            this.bytCompression = Utils.convertByteArrToByte(bytData, 10);
            i++;
            this.bytFilter = Utils.convertByteArrToByte(bytData, 11);
            i++;
            this.bytInterlace = Utils.convertByteArrToByte(bytData, 12);
        } catch (Throwable t) {
            String strFieldName = STR_FIELD_NAMES[i];
            sbError.append("Could not get ").append(strFieldName).append("\n");
        }

        switch (this.bytColor) {
            case 0:
            case 4:
                this.strImageType = "Greyscale";
                break;
            case 2:
            case 6:
                this.strImageType = "Color";
                break;
            case 3:
                this.strImageType = "Palette";
                break;
            default:
                this.sbError.append("Incorrect Color:").append(bytColor).append("\n");
                break;
        }

        if (this.intWidth <= 0) {
            sbError.append("Incorrect Width:").append(this.intWidth).append("\n");
        }
        if (this.intHeight <= 0) {
            sbError.append("Incorrect Height:").append(this.intHeight).append("\n");
        }

        if (this.bytCompression != 0) {
            sbError.append("Non zero Compression:").append(this.bytCompression).append("\n");
        }

        if (this.bytFilter != 0) {
            sbError.append("Non zero Filter:").append(this.bytFilter).append("\n");
        }

        if (this.bytInterlace != 0 && this.bytInterlace != 1) {
            sbError.append("Incorrect Interlace:").append(this.bytInterlace).append("\n");
        }
    }

    // -------------------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------------------
    @Override
    public String getDataValues() {

        this.sbDataValues.append("Width=").append(this.intWidth).append("\n");
        this.sbDataValues.append("Height=").append(this.intHeight).append("\n");
        this.sbDataValues.append("Depth=").append(this.intDepth).append("\n");
        this.sbDataValues.append("Color=").append(this.bytColor).append("\n");
        this.sbDataValues.append("ImageType=").append(this.strImageType).append("\n");
        this.sbDataValues.append("Compression=").append(this.bytCompression).append("\n");
        this.sbDataValues.append("Filter=").append(this.bytFilter).append("\n");
        this.sbDataValues.append("Interlace=").append(this.bytInterlace);
        return sbDataValues.toString();
    }

    // -------------------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------------------
    public byte getColor() {

        return this.bytColor;
    }
    // -------------------------------------------------------------------------------------
}
