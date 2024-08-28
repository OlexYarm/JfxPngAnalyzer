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
import java.io.UnsupportedEncodingException;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chunk_iTXt extends Chunk {
// PNG chunk 
//
// An iTXt chunk contains:
//
// Keyword	1-79 bytes (character string)
// Null separator	1 byte (null character)
// Compression flag	1 byte
// Compression method	1 byte
// Language tag	0 or more bytes (character string)
// Null separator	1 byte (null character)
// Translated keyword	0 or more bytes
// Null separator	1 byte (null character)
// Text	0 or more bytes    

    private static final Logger LOGGER = LoggerFactory.getLogger(Chunk_iTXt.class);

    private String strKeyword;
    private byte bytCompressionFlag;
    private byte bytCompressionMetod;
    private String strLanguageTag;
    private String strKeywordTranslated;
    private String strText;

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_iTXt(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc,
            ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        int intDataLen = bytData.length;
        int intPosStart = 0;
        int intPosFound;
        int intTextLen;

        intPosFound = findNull(bytData, intPosStart, intDataLen);
        if (intPosFound < 0) {
            return;
        }
        intTextLen = intPosFound - intPosStart;
        try {
            this.strKeyword = new String(bytData, 0, intTextLen, "US-ASCII");
            this.sbDataValues.append("Keyword=").append(this.strKeyword).append("\n");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Could not decode Keyword or Text."
                    + " intSeq=\"" + intSeq + "\""
                    + " UnsupportedEncodingException=\"" + e.toString() + "\"");
            sbError.append("UnsupportedEncodingException:").append(e.toString()).append("\n");
            return;
        }
        if (intPosFound == intDataLen - 1) {
            return;
        }

        this.bytCompressionFlag = bytData[++intPosFound];
        this.sbDataValues.append("CompressionFlag=").append(this.bytCompressionFlag).append("\n");
        if (intPosFound == intDataLen - 1) {
            return;
        }
        this.bytCompressionMetod = bytData[++intPosFound];
        this.sbDataValues.append("CompressionMetod=").append(this.bytCompressionMetod).append("\n");

        intPosStart = intPosFound + 1;
        intPosFound = findNull(bytData, intPosStart, intDataLen);
        if (intPosFound < 0) {
            return;
        }
        intTextLen = intPosFound - intPosStart;
        if (intTextLen > 0) {
            // LanguageTag is not empty
            try {
                this.strLanguageTag = new String(bytData, intPosStart, intTextLen, "US-ASCII");
                this.sbDataValues.append("LanguageTag=").append(this.strLanguageTag).append("\n");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Could not decode LanguageTag."
                        + " intSeq=\"" + intSeq + "\""
                        + " UnsupportedEncodingException=\"" + e.toString() + "\"");
                sbError.append("UnsupportedEncodingException:").append(e.toString()).append("\n");
                return;
            }
        }
        intPosStart = intPosFound + 1;
        intPosFound = findNull(bytData, intPosStart, intDataLen);
        if (intPosFound < 0) {
            return;
        }
        intTextLen = intPosFound - intPosStart;
        if (intTextLen > 0) {
            // Translated keyword is not empty
            try {
                this.strKeywordTranslated = new String(bytData, intPosStart, intTextLen, "US-ASCII");
                this.sbDataValues.append("KeywordTranslated=").append("\n").append(this.strKeywordTranslated).append("\n");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Could not decode KeywordTranslated."
                        + " intSeq=\"" + intSeq + "\""
                        + " UnsupportedEncodingException=\"" + e.toString() + "\"");
                sbError.append("UnsupportedEncodingException:").append(e.toString()).append("\n");
                return;
            }
        }
        intPosStart = intPosFound + 1;
        intTextLen = intDataLen - intPosStart;
        if (intTextLen > 0) {
            // Text is not empty
            try {
                this.strText = new String(bytData, intPosStart, intTextLen, "US-ASCII");
                this.sbDataValues.append("Text=").append("\n").append(this.strText).append("\n");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Could not decode Text."
                        + " intSeq=\"" + intSeq + "\""
                        + " UnsupportedEncodingException=\"" + e.toString() + "\"");
                sbError.append("UnsupportedEncodingException:").append(e.toString()).append("\n");
            }
        }
    }

    // -------------------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------------------
    private int findNull(byte[] bytData, int intPosStart, int intDataLen) {

        for (int i = intPosStart; i < intDataLen; i++) {
            if (bytData[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    // -------------------------------------------------------------------------------------
    @Override
    public String getDataValues() {

        return this.sbDataValues.toString();
    }
    // -------------------------------------------------------------------------------------
}
