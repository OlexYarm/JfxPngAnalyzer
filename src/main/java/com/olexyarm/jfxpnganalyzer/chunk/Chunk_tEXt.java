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

import java.io.UnsupportedEncodingException;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chunk_tEXt extends Chunk {
// PNG chunk tEXt Textual data
//
// Each tEXt chunk contains a keyword and a text string, in the format:
// Keyword	1-79 bytes (character string)
// Null separator	1 byte (null character)
// Text string	0 or more bytes (character string)

    private static final Logger LOGGER = LoggerFactory.getLogger(Chunk_tEXt.class);

    private String strKeyword;
    private String strText;
    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_tEXt(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc, ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        int i;
        for (i = 0; i < intDataLen; i++) {
            byte bytByte = bytData[i];
            if (bytByte == 0) {
                try {
                    this.strKeyword = new String(bytData, 0, i, "US-ASCII");
                    this.strText = new String(bytData, i + 1, intDataLen - i - 1, "US-ASCII");
                    this.sbDataValues.append("Keyword=").append(this.strKeyword).append("\n");
                    this.sbDataValues.append("Text=").append(this.strText).append("\n");
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error("Could not decode Keyword or Text."
                            + " intSeq=\"" + intSeq + "\""
                            + " UnsupportedEncodingException=\"" + e.toString() + "\"");
                    sbError.append("UnsupportedEncodingException:").append(e.toString()).append("\n");
                }
                break;
            }
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
