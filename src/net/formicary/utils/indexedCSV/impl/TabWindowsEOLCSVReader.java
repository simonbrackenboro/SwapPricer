package net.formicary.utils.indexedCSV.impl;

import net.formicary.utils.indexedCSV.AbstractCSVList;
import net.formicary.utils.indexedCSV.IndexedCSV;
import org.apache.commons.collections.primitives.IntList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class TabWindowsEOLCSVReader extends AbstractCSVList<List<String>>{
    private final IntList indexEOL, indexEOF;
    private final ByteBuffer bb;
    private byte[] bytes = new byte[128];

    public TabWindowsEOLCSVReader(IndexedCSV indexedCSV) {
        this.indexEOL = indexedCSV.getIndexEOL();
        this.indexEOF = indexedCSV.getIndexEOF();
        this.bb = indexedCSV.getBb();
        bb.rewind();
    }

    private int lines() {
        return indexEOL.size()-1;
    }

    private int fields(int line) {
        checkLine(line);
        int fieldStart = indexEOL.get(line);
        int fieldEnd = indexEOL.get(line + 1);
        return fieldEnd - fieldStart;
    }

    private void checkLine(int line) {
        if (line >= lines())
            throw new IndexOutOfBoundsException("line to large");
    }

    private String get(int line, int field) {
        int lineStart = indexEOL.get(line);
        int lineEnd = indexEOL.get(line + 1);

        int offset = lineStart + field;

        int start = indexEOF.get(offset);
        int end = indexEOF.get(offset + 1);

        if (offset + 1 == lineEnd)
            end--;
        end--;

        int size = end -start;

        if(bytes.length < size)
            bytes = new byte[size];

        bb.position(start);
        bb.get(bytes, 0, size);

        return new String(bytes, 0, size);
    }

    private class Line extends AbstractCSVList<String> {
        private final int line;
        private final int size;

        private Line(int line) {
            checkLine(line);
            this.line = line;
            size = fields(line);
        }

        public int size() {
            return size;
        }

        public Iterator<String> iterator() {
            return new Iterator<String>() {
                private int field = 0;
                public boolean hasNext() {
                    return field < size;
                }

                public String next() {
                    return TabWindowsEOLCSVReader.this.get(line, field++);
                }

                public void remove() {
                    throw new NotImplementedException();
                }
            };
        }

        public String get(int field) {
            if(field >= size())
                throw new IndexOutOfBoundsException("field to large");
            return TabWindowsEOLCSVReader.this.get(line, field);
        }
    }

    public int size() {
        return lines();
    }

    public Iterator<List<String>> iterator() {
        return new Iterator<List<String>>() {
            int line = 0;
            public boolean hasNext() {
                return line < size();
            }

            public List<String> next() {
                return new Line(line++);
            }

            public void remove() {
                throw new NotImplementedException();
            }
        };
    }

    public List<String> get(int i) {
        checkLine(i);
        return new Line(i);
    }
}
