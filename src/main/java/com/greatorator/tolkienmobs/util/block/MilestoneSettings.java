package com.greatorator.tolkienmobs.util.block;

import java.util.Objects;
import java.util.UUID;

public class MilestoneSettings {
    public int distance = 500;
    public int dimension = 1;
    public String name = "Set Name";
    public String uuid = UUID.randomUUID().toString();

    public MilestoneSettings() {

    }

    public MilestoneSettings(int distance, int dimension, String name, String uuid) {
        this.distance = distance;
        this.dimension = dimension;
        this.name = name;
        this.uuid = uuid;
    }


    @Override
    public int hashCode() {
        return Objects.hash(distance, dimension, name, uuid);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MilestoneSettings that = (MilestoneSettings) object;
        return distance == that.distance
                &&  dimension == that.dimension
                && Objects.equals(name, that.name)
                && Objects.equals(uuid, that.uuid);
    }
}
