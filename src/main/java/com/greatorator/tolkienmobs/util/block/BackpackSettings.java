package com.greatorator.tolkienmobs.util.block;

import java.util.Objects;

public class BackpackSettings {
    public boolean sleepingBag = false;
    public boolean campfire = false;
    public boolean upgrade = false;
    /** Whether an installed magnet pulls drops in. On by default, so a newly fitted magnet works. */
    public boolean magnet = true;

    public BackpackSettings() {

    }

    public BackpackSettings(boolean sleepingBag, boolean campfire, boolean upgrade, boolean magnet) {
        this.sleepingBag = sleepingBag;
        this.campfire = campfire;
        this.upgrade = upgrade;
        this.magnet = magnet;
    }


    @Override
    public int hashCode() {
        return Objects.hash(sleepingBag, campfire, upgrade, magnet);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BackpackSettings that = (BackpackSettings) object;
        return sleepingBag == that.sleepingBag
                && campfire == that.campfire
                && upgrade == that.upgrade
                && magnet == that.magnet;
    }
}
