package net.formicary.utils.indexedCSV.impl;

import net.formicary.utils.indexedCSV.IndexedCSV;
import net.formicary.utils.indexedCSV.IndexedCSVBuilder;
import org.apache.commons.collections.primitives.ArrayIntList;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class TabEOFWindowsEOLCSVBuilder extends AbstractIndexedCSVBuilder {
    private final ArrayIntList EOL = new ArrayIntList();
    private final ArrayIntList EOF = new ArrayIntList();
    private ByteBuffer bb = null;

    public IndexedCSVBuilder withFile(String file) {
        bb = getByteBuffer(file);
        return this;
    }

    public IndexedCSV build() {
        assert (bb != null);
        guessSize();
        prime();
        byte b;
        try {
            while (true) {
                b = bb.get();
                switch (b) {
                    case 10:
                        EOL.add(EOF.size());
                    case 9:
                        EOF.add(bb.position());
                }
            }
        } catch (BufferUnderflowException e) {
            bb.rewind();
        }
        return new IndexedCSV(bb, EOF, EOL);
    }

    public void guessSize() {
        int limit = bb.limit();
        int restrict = Math.min(20480, limit);
        bb.limit(restrict);
        int EOF = 0, EOL = 0;
        byte b;
        try {
            while (true) {
                b = bb.get();
                switch (b) {
                    case 9:
                        EOF++;
                        break;
                    case 10:
                        EOF++;
                        EOL++;
                        break;
                }
            }
        } catch (BufferUnderflowException e) {
        }
        EOF *= limit * 1.2 / restrict;
        EOL *= limit * 1.2 / restrict;
        this.EOF.ensureCapacity(EOF);
        this.EOL.ensureCapacity(EOL);
        bb.limit(limit);
        bb.rewind();
    }

    private void prime() {
        EOF.clear();
        EOF.add(0);
        EOL.clear();
        EOL.add(0);
    }
}
