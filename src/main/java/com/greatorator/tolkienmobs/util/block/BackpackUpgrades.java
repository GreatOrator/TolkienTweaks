package com.greatorator.tolkienmobs.util.block;

import java.util.Objects;

public class BackpackUpgrades {
    public boolean crafting = false;
    public boolean sleepingBag = false;
    public boolean campfire = false;
    public boolean size_upgrade = false;
    public boolean size_upgrade_2 = false;
    public boolean magnet_1 = false;
    public boolean magnet_2 = false;
    public boolean magnet_3 = false;

    public BackpackUpgrades() {

    }

    public BackpackUpgrades(boolean crafting, boolean sleepingBag, boolean campfire, boolean size_upgrade, boolean size_upgrade_2,
                            boolean magnet_1, boolean magnet_2, boolean magnet_3) {
        this.crafting = crafting;
        this.sleepingBag = sleepingBag;
        this.campfire = campfire;
        this.size_upgrade = size_upgrade;
        this.size_upgrade_2 = size_upgrade_2;
        this.magnet_1 = magnet_1;
        this.magnet_2 = magnet_2;
        this.magnet_3 = magnet_3;
    }

    /** How far the magnet reaches, in blocks: the best installed tier wins, and none installed is none. */
    public int magnetRange() {
        if (magnet_3) {
            return 64;
        }
        if (magnet_2) {
            return 32;
        }
        return magnet_1 ? 16 : 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(crafting, sleepingBag, campfire, size_upgrade, size_upgrade_2, magnet_1, magnet_2, magnet_3);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BackpackUpgrades that = (BackpackUpgrades) object;
        return crafting == that.crafting
                && sleepingBag == that.sleepingBag
                && campfire == that.campfire
                && size_upgrade == that.size_upgrade
                && size_upgrade_2 == that.size_upgrade_2
                && magnet_1 == that.magnet_1
                && magnet_2 == that.magnet_2
                && magnet_3 == that.magnet_3;
    }
}
