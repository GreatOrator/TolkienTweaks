package com.greatorator.tolkienmobs.handler.data;

class DataUtils {

    static void checkLen(int arrLen, int off, int len) {
        if ((off < 0) || (off > arrLen) || (len < 0) || ((off + len) > arrLen) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
    }
}
