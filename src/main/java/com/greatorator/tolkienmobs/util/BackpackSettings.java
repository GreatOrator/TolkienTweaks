package com.greatorator.tolkienmobs.util;

import java.util.Objects;

public class BackpackSettings {
    public boolean sleepingBag = false;
    public boolean campfire = false;
    public boolean upgrade = false;

    public BackpackSettings() {

    }

    public BackpackSettings(boolean sleepingBag, boolean campfire, boolean upgrade) {
        this.sleepingBag = sleepingBag;
        this.campfire = campfire;
        this.upgrade = upgrade;
    }


    @Override
    public int hashCode() {
        return Objects.hash(sleepingBag, campfire, upgrade);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BackpackSettings that = (BackpackSettings) object;
        return sleepingBag == that.sleepingBag
                && campfire == that.campfire
                && upgrade == that.upgrade;
    }
}
