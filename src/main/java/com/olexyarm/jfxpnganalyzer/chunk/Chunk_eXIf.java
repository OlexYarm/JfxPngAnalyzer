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

import javafx.collections.ObservableMap;

public class Chunk_eXIf extends Chunk {
// PNG chunk eXIf Exchangeable Image File (Exif) Profile
//
// The eXIf chunk contains metadata concerning the original image data
// The eXIf chunk size is constrained only by the maximum of 231-1 bytes    
// The first two bytes of data are either "II" for little-endian (Intel) or "MM" for big-endian (Motorola) byte order.
// First four bytes should have the following hexadecimal values:
// 49 49 2A 00 (ASCII "II", 16-bit little-endian integer 42)
// 4D 4D 00 2A (ASCII "MM", 16-bit big-endian integer 42)
// All other values are reserved for possible future definition.

    private final StringBuilder sbDataValues = new StringBuilder();

    public Chunk_eXIf(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc,
            ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        if (intDataLen > Integer.MAX_VALUE) {
            this.sbError.append("Data size is too big:").append(intDataLen);
        }

        if (bytData[0] == 0x49 && bytData[1] == 0x49 && bytData[2] == 0x2A && bytData[3] == 0x00) {
            this.sbDataValues.append("Little-endian").append("\n");
        } else if (bytData[0] == 0x4D && bytData[1] == 0x4D && bytData[2] == 0x00 && bytData[3] == 0x2A) {
            this.sbDataValues.append("Big-endian").append("\n");
        } else {
            this.sbError.append("Incorrect Endian code:").append("\n");
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
