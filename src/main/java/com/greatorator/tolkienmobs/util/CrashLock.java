package com.greatorator.tolkienmobs.util;

public class CrashLock {

    private final String message;
    private boolean locked = false;

    public CrashLock(String message) {
        this.message = message;
    }

    public void lock() {
        if (locked) {
            throw new RuntimeException(message);
        }
        locked = true;
    }

    public void unlock() {
        locked = false;
    }
}
