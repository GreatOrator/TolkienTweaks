package com.greatorator.tolkienmobs.util;

import java.util.Objects;

public class KeyStoneSettings {
    public boolean keepKey = false;

    public KeyStoneSettings() {

    }

    public KeyStoneSettings(boolean keepKey) {
        this.keepKey = keepKey;
    }


    @Override
    public int hashCode() {
        return Objects.hash(keepKey);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        KeyStoneSettings that = (KeyStoneSettings) object;
        return keepKey == that.keepKey;
    }
}
