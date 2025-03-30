package com.greatorator.tolkienmobs.util;

import java.util.Objects;

public class BackpackUpgrades {
    public boolean crafting = false;
    public boolean sleepingBag = false;
    public boolean campfire = false;
    public boolean size_upgrade = false;
    public boolean size_upgrade_2 = false;

    public BackpackUpgrades() {

    }

    public BackpackUpgrades(boolean crafting, boolean sleepingBag, boolean campfire, boolean size_upgrade, boolean size_upgrade_2) {
        this.crafting = crafting;
        this.sleepingBag = sleepingBag;
        this.campfire = campfire;
        this.size_upgrade = size_upgrade;
        this.size_upgrade_2 = size_upgrade_2;
    }


    @Override
    public int hashCode() {
        return Objects.hash(crafting, sleepingBag, campfire, size_upgrade, size_upgrade_2);
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
                && size_upgrade_2 == that.size_upgrade_2;
    }
}
