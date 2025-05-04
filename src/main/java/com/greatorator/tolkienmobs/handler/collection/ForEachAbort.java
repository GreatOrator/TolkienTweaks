package com.greatorator.tolkienmobs.handler.collection;

class ForEachAbort extends RuntimeException {

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
