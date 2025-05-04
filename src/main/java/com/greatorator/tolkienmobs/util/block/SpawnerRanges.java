package com.greatorator.tolkienmobs.util.block;

import java.util.Objects;

public class SpawnerRanges {
    public int activationRange = 4;
    public int spawnRange = 4;
    public int spawnCount = 4;
    public int maxCluster = 6;
    public int clusterRange = 16;

    public SpawnerRanges() {

    }

    public SpawnerRanges(int activationRange, int spawnRange, int spawnCount, int maxCluster, int clusterRange) {
        this.activationRange = activationRange;
        this.spawnRange = spawnRange;
        this.spawnCount = spawnCount;
        this.maxCluster = maxCluster;
        this.clusterRange = clusterRange;
    }


    @Override
    public int hashCode() {
        return Objects.hash(activationRange, spawnRange, spawnCount, maxCluster, clusterRange);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SpawnerRanges that = (SpawnerRanges) object;
        return activationRange == that.activationRange
                && spawnRange == that.spawnRange
                && spawnCount == that.spawnCount
                && maxCluster == that.maxCluster
                && clusterRange == that.clusterRange;
    }
}
