package com.yingu.project.ocr.controller;

import com.abbyy.FREngine.IFileWriter;
import com.abbyy.FREngine.Ref;

public class FileWriterAbbyy implements IFileWriter {
    @Override
    public void Open(String s, Ref<Integer> ref) {

    }

    @Override
    public void Write(byte[] bytes) {
        this.bytes = bytes;
    }

    private byte[] bytes;

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public void Close() {

    }
}
