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
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.util.zip.CRC32;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChunkFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkFactory.class);

    public static Chunk retrieve(final int intChunkSeq, final MappedByteBuffer mbb, ObservableMap<String, Chunk> omapChunks) throws IOException {

        // Chunk length
        byte[] bytChunkLength = new byte[4];
        try {
            mbb.get(bytChunkLength);
        } catch (BufferUnderflowException e) {
            LOGGER.error("Could not read Chunk Length."
                    + " ChunkSeq=\"" + intChunkSeq + "\""
                    + " ChunkLength=\"" + Utils.convertBytesToStringHex(bytChunkLength) + "\""
                    + " BufferUnderflowException=\"" + e.toString() + "\"");
            // return error chunk - could not read Len

            byte[] bytType = "erro".getBytes();
            Chunk chunk = new Chunk_erro(intChunkSeq, 0, bytType, bytChunkLength, 1, omapChunks);
            chunk.setValidCrC(false);
            return chunk;
        }
        int intChunkLen = Utils.convertByteArrToInt(bytChunkLength, 0);
        LOGGER.debug("Parsed Chunk Length."
                + " intChunkSeq=\"" + intChunkSeq + "\""
                + " intChunkLen=" + intChunkLen
                + " bytChunkLength=\"" + Utils.convertBytesToStringHex(bytChunkLength) + "\"");

        if (intChunkLen < 0) {
            // return error chunk - incorrect Len
            byte[] bytType = "erro".getBytes();
            Chunk chunk = new Chunk_erro(intChunkSeq, intChunkLen, bytType, bytChunkLength, 2, omapChunks);
            chunk.setValidCrC(false);
            return chunk;
        }

        // Chunk Type
        byte[] bytChunkType = new byte[4];
        int intPosition;
        if (LOGGER.isTraceEnabled()) {
            intPosition = mbb.position();
            LOGGER.trace("Chunk position." + " intPosition=" + intPosition);
        }
        try {
            mbb.mark();
            mbb.get(bytChunkType);
            if (LOGGER.isTraceEnabled()) {
                intPosition = mbb.position();
                LOGGER.trace("Chunk position." + " intPosition=" + intPosition);
            }
        } catch (BufferUnderflowException e) {
            LOGGER.error("Could not read Chunk Type."
                    + " ChunkSeq=\"" + intChunkSeq + "\""
                    + " ChunkType=\"" + Utils.convertBytesToStringHex(bytChunkType) + "\""
                    + " BufferUnderflowException=\"" + e.toString() + "\"");
            // return error chunk - could not read chunk Type
            byte[] bytType = "erro".getBytes();
            Chunk chunk = new Chunk_erro(intChunkSeq, intChunkLen, bytType, bytChunkLength, 3, omapChunks);
            chunk.setValidCrC(false);
            return chunk;
        }
        String strChunkType = new String(bytChunkType);
        LOGGER.debug("Retrieved Chunk Type."
                + " intChunkSeq=\"" + intChunkSeq + "\""
                + " strChunkType=\"" + strChunkType + "\""
                + " bytChunkType=\"" + Utils.convertBytesToStringHex(bytChunkType) + "\"");

        // Chunk Data
        byte[] bytChunkData = null;
        if (intChunkLen > 0) {
            try {
                bytChunkData = new byte[intChunkLen];
                mbb.get(bytChunkData);
                if (LOGGER.isTraceEnabled()) {
                    intPosition = mbb.position();
                    LOGGER.trace("Chunk position." + " intPosition=" + intPosition);
                }
            } catch (Throwable e) {
                LOGGER.error("Could not read all Chunk Data."
                        + " ChunkType=\"" + strChunkType + "\""
                        + " ChunkData=\"" + Utils.convertBytesToStringHexLimit(bytChunkData, Settings.INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX) + "\""
                        + " Throwable=\"" + e.toString() + "\"");
                // return error chunk - could not read chunk Data
                byte[] bytType = "erro".getBytes();
                Chunk chunk = new Chunk_erro(intChunkSeq, intChunkLen, bytType, bytChunkData, 4, omapChunks);
                chunk.setValidCrC(false);
                return chunk;
            }
            //if (bytChunkData == null) {
            //    bytChunkData = " ".getBytes();
            // }
            String strChunkSdata = Utils.convertBytesToStringHexLimit(bytChunkData, Settings.INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX);
            LOGGER.debug("Retrieved Chunk Data."
                    + " strChunkType=\"" + strChunkType + "\""
                    + " bytChunkData=\"" + strChunkSdata + "\"");
        } else {
            LOGGER.debug("No Chunk Data."
                    + " strChunkType=\"" + strChunkType + "\"");
        }

        // Chunk Type and Data for calculating CRC
        mbb.reset(); // Resets buffer position to Chunk Type.
        if (LOGGER.isTraceEnabled()) {
            intPosition = mbb.position();
            LOGGER.trace("Chunk position Type and Data." + " intPosition=" + intPosition);
        }
        byte[] bytChunkTypeAndData = null;
        try {
            bytChunkTypeAndData = new byte[4 + intChunkLen];
            mbb.get(bytChunkTypeAndData);
        } catch (Throwable e) {
            LOGGER.error("Could not read Chunk Type and Data."
                    + " ChunkType=\"" + strChunkType + "\""
                    + " ChunkTypeAndData=\"" + Utils.convertBytesToStringHexLimit(bytChunkTypeAndData, Settings.INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX) + "\""
                    + " Throwable=\"" + e.toString() + "\"");
            // return error chunk - could not read Type and Data
            byte[] bytType = "erro".getBytes();
            Chunk chunk = new Chunk_erro(intChunkSeq, intChunkLen, bytType, bytChunkLength, 5, omapChunks);
            chunk.setValidCrC(false);
            return chunk;
        }

        if (LOGGER.isTraceEnabled()) {
            intPosition = mbb.position();
            LOGGER.trace("Chunk position." + " intPosition=" + intPosition);
        }

        CRC32 crc32 = new CRC32();
        crc32.update(bytChunkTypeAndData);
        long lngCrc32Calculated = crc32.getValue();
        int intCrc32Calculated = (int) lngCrc32Calculated;

        LOGGER.debug("Calculated CRC for Type and Data."
                + " strChunkType=\"" + strChunkType + "\""
                + " lngCrc32Calculated=" + lngCrc32Calculated
                + " intCrc32Calculated=" + intCrc32Calculated
                + " bytChunkTypeAndData=\"" + Utils.convertBytesToStringHexLimit(bytChunkTypeAndData, Settings.INT_CHUNK_DATA_HEX_TO_STRING_LENGTH_MAX) + "\"");

        // Chunk CRC
        byte[] bytChunkCrc = new byte[4];
        try {
            mbb.get(bytChunkCrc);
            if (LOGGER.isTraceEnabled()) {
                intPosition = mbb.position();
                LOGGER.trace("Chunk position." + " intPosition=" + intPosition);
            }
        } catch (BufferUnderflowException e) {
            LOGGER.error("Could not read all Chunk CRC."
                    + " ChunkType=\"" + strChunkType + "\""
                    + " ChunkCrc=\"" + Utils.convertBytesToStringHex(bytChunkCrc) + "\""
                    + " BufferUnderflowException=\"" + e.toString() + "\"");
            // return error chunk - could not read CRC
            byte[] bytType = "erro".getBytes();
            Chunk chunk = new Chunk_erro(intChunkSeq, intChunkLen, bytType, bytChunkCrc, 6, omapChunks);
            chunk.setValidCrC(false);
            return chunk;
        }
        int intChunkCrc = Utils.convertByteArrToInt(bytChunkCrc, 0);

        boolean booValidCrc = false;
        LOGGER.debug("Parsed Chunk CRC."
                + " strChunkType=\"" + strChunkType + "\""
                + " intChunkCrc=" + intChunkCrc
                + " intCrc32Calculated=" + intCrc32Calculated
                + " lngCrc32Calculated=" + lngCrc32Calculated
                + " bytChunkCrc=\"" + Utils.convertBytesToStringHex(bytChunkCrc) + "\"");

        if (intChunkCrc == intCrc32Calculated) {
            booValidCrc = true;
        } else {
            LOGGER.error("CRC does not match."
                    + " ChunkType=\"" + strChunkType + "\""
                    + " ChunkCrc=" + intChunkCrc
                    + " Crc32Calculated=" + lngCrc32Calculated
                    + " ChunkCrc=\"" + Utils.convertBytesToStringHex(bytChunkCrc) + "\"");
        }

        Chunk chunk;
        if (strChunkType.equals("IHDR")) {
            chunk = new IHDR(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("PLTE")) {
            chunk = new PLTE(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("IDAT")) {
            chunk = new IDAT(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("IEND")) {
            chunk = new IEND(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("iCCP")) {
            chunk = new Chunk_iCCP(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("bKGD")) {
            chunk = new Chunk_bKGD(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("tIME")) {
            chunk = new Chunk_tIME(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("pHYs")) {
            chunk = new Chunk_pHYs(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("tRNS")) {
            chunk = new Chunk_tRNS(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("gAMA")) {
            chunk = new Chunk_gAMA(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("cHRM")) {
            chunk = new Chunk_cHRM(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("eXIf")) {
            chunk = new Chunk_eXIf(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("sPLT")) {
            chunk = new Chunk_sPLT(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("sBIT")) {
            chunk = new Chunk_sBIT(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("hIST")) {
            chunk = new Chunk_hIST(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("tEXt")) {
            chunk = new Chunk_tEXt(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("zTXt")) {
            chunk = new Chunk_zTXt(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else if (strChunkType.equals("iTXt")) {
            chunk = new Chunk_iTXt(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        } else {
            chunk = new Chunk_unkn(intChunkSeq, intChunkLen, bytChunkType, bytChunkData, intChunkCrc, omapChunks);
        }

        chunk.setValidCrC(booValidCrc);
        return chunk;
    }

}
