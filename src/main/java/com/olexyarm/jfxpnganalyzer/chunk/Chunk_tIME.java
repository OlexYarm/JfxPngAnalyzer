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

public class Chunk_tIME extends Chunk {

// Year	2 bytes (complete; for example, 1995, not 95)
// Month	1 byte (1-12)
// Day	1 byte (1-31)
// Hour	1 byte (0-23)
// Minute	1 byte (0-59)
// Second	1 byte (0-60) (to allow for leap seconds)
    private short shtYear;
    private byte bytMonth;
    private byte bytDay;
    private byte bytHour;
    private byte bytMinute;
    private byte bytSecond;

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_tIME(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc,
            ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        if (intDataLen != 7) {
            this.sbError.append("Incorrect Data size for Greyscale image:").append(intDataLen);
            return;
        }
        this.shtYear = Utils.convertByteArrToShort(bytData, 0);
        this.bytMonth = Utils.convertByteArrToByte(bytData, 2);
        this.bytDay = Utils.convertByteArrToByte(bytData, 3);
        this.bytHour = Utils.convertByteArrToByte(bytData, 4);
        this.bytMinute = Utils.convertByteArrToByte(bytData, 5);
        this.bytSecond = Utils.convertByteArrToByte(bytData, 6);

        this.sbDataValues.append("Year=").append(this.shtYear).append("\n");
        this.sbDataValues.append("Month=").append(this.bytMonth).append("\n");
        this.sbDataValues.append("Day=").append(this.bytDay).append("\n");
        this.sbDataValues.append("Hour=").append(this.bytHour).append("\n");
        this.sbDataValues.append("Minute=").append(this.bytMinute).append("\n");
        this.sbDataValues.append("Second=").append(this.bytSecond).append("\n");

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
