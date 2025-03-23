package com.greatorator.tolkienmobs.util;

import java.util.Objects;

public class KeyStoneData {
    public String keyCode = "Enter Code";
    public int blockItemFilter = -1;

    //This is not saved in NBT, and is recreated as needed on demand
//    public final Map<ItemStackKey, Boolean> filterCache = new Object2BooleanOpenHashMap<>();
//    public final WeakHashMap<Entity, Boolean> entityCache = new WeakHashMap<>();

    public KeyStoneData() {

    }

    public KeyStoneData(String keyCode) {
        this.keyCode = keyCode;
//        this.rsToggle = rsToggle;
//        this.rsPulse = rsPulse;
//        this.rsDelay = rsDelay;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyCode);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        KeyStoneData that = (KeyStoneData) object;
        return keyCode == that.keyCode;
    }
}
