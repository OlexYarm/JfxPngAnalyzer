package com.olexyarm.jfxpnganalyzer.chunk;

import javafx.collections.ObservableMap;

public class Chunk_erro extends Chunk {
// Chunk with incorrect values.

    private final StringBuilder sbDataValues = new StringBuilder();

    // -------------------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------------------
    public Chunk_erro(final int intSeq, final int intLen, final byte[] bytType, byte[] bytData, int intCrc, ObservableMap<String, Chunk> omapChunks) {

        super(intSeq, intLen, bytType, bytData, intCrc);

        if (intCrc == 1) {
            this.sbError.append("Error read Len").append("\n").append(intLen).append("\n");
        }
        if (intCrc == 2) {
            this.sbError.append("Incorrect chunk Len:").append("\n").append(intLen).append("\n");
        }
        if (intCrc == 3) {
            this.sbError.append("Error read Type, Len:").append("\n").append(intLen).append("\n");
        }
        if (intCrc == 4) {
            this.sbError.append("Error read Data, Len:").append("\n").append(intLen).append("\n");
        }
        if (intCrc == 4) {
            this.sbError.append("Error read Type and Data, Len:").append("\n").append(intLen).append("\n");
        }
        if (intCrc == 4) {
            this.sbError.append("Error read CRC, Len:").append("\n").append(intLen).append("\n");
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
