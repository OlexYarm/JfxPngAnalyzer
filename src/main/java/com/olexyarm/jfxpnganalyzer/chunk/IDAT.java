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

class IDAT extends Chunk {

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public IDAT(final int intChunkSeq, final int intChunkLen, final byte[] bytType, byte[] bytChunkData, int intChunkCrc,
            ObservableMap<String, Chunk> omapChunks) { //throws IOException {

        super(intChunkSeq, intChunkLen, bytType, bytChunkData, intChunkCrc);
    }
    // -------------------------------------------------------------------------------------
}
