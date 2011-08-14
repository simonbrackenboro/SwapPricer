package net.formicary.utils.indexedCSV;

import org.apache.commons.collections.primitives.IntList;

import java.nio.ByteBuffer;

public class IndexedCSV {
    private final ByteBuffer bb;
    private final IntList indexEOF, indexEOL;

    public IndexedCSV(ByteBuffer bb, IntList indexEOF, IntList indexEOL) {
        this.bb = bb;
        this.indexEOF = indexEOF;
        this.indexEOL = indexEOL;
    }

    public ByteBuffer getBb() {
        return bb;
    }

    public IntList getIndexEOF() {
        return indexEOF;
    }

    public IntList getIndexEOL() {
        return indexEOL;
    }
}
