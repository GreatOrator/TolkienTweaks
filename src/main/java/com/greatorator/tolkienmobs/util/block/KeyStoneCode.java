package com.greatorator.tolkienmobs.util.block;

import java.util.Objects;

public class KeyStoneCode {
    public String keyCode = "Enter Code";

    public KeyStoneCode() {

    }

    public KeyStoneCode(String keyCode) {
        this.keyCode = keyCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyCode);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        KeyStoneCode that = (KeyStoneCode) object;
        return Objects.equals(keyCode, that.keyCode);
    }
}