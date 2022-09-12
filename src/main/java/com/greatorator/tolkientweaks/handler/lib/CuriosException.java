package com.greatorator.tolkientweaks.handler.lib;

public class CuriosException extends RuntimeException {
    public CuriosException() {}

    public CuriosException(String message) {
        super(message);
    }

    public CuriosException(String message, Throwable cause) {
        super(message, cause);
    }

    public CuriosException(Throwable cause) {
        super(cause);
    }

    public CuriosException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
