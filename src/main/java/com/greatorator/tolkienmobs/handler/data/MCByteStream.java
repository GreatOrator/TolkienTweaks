package com.greatorator.tolkienmobs.handler.data;

import java.io.ByteArrayOutputStream;

public class MCByteStream extends MCDataOutputStream {

    private final ByteArrayOutputStream bos;

    public MCByteStream() {
        this(new ByteArrayOutputStream());
    }

    public MCByteStream(ByteArrayOutputStream bos) {
        super(bos);
        this.bos = bos;
    }

    public byte[] getBytes() {
        return bos.toByteArray();
    }
}
