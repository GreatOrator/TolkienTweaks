package com.greatorator.tolkienmobs.util.block;

import java.util.Objects;

public class BackpackPlacement {
    public boolean sleepingBag = false;
    public boolean campfire = false;

    public BackpackPlacement() {

    }

    public BackpackPlacement(boolean sleepingBag, boolean campfire) {
        this.sleepingBag = sleepingBag;
        this.campfire = campfire;
    }


    @Override
    public int hashCode() {
        return Objects.hash(sleepingBag, campfire);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BackpackPlacement that = (BackpackPlacement) object;
        return sleepingBag == that.sleepingBag
                && campfire == that.campfire;
    }
}
