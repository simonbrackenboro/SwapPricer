package net.formicary.utils.indexedCSV.impl;

import net.formicary.utils.indexedCSV.IndexedCSVBuilder;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public abstract class AbstractIndexedCSVBuilder  implements IndexedCSVBuilder{
    public static ByteBuffer getByteBuffer(String file) {
        try {
            RandomAccessFile f = new RandomAccessFile(file, "r");
            FileChannel fc = f.getChannel();
            return fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
