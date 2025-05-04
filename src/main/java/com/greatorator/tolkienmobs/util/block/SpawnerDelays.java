package com.greatorator.tolkienmobs.util.block;

import java.util.Objects;

public class SpawnerDelays {
    public int minSpawnDelay = 200;
    public int maxSpawnDelay = 800;
    public int spawnDelay = 20;
    public SpawnerDelays() {

    }

    public SpawnerDelays(int minSpawnDelay, int maxSpawnDelay, int spawnDelay) {
        this.minSpawnDelay = minSpawnDelay;
        this.maxSpawnDelay = maxSpawnDelay;
        this.spawnDelay = spawnDelay;
    }


    @Override
    public int hashCode() {
        return Objects.hash(minSpawnDelay, maxSpawnDelay, spawnDelay);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SpawnerDelays that = (SpawnerDelays) object;
        return minSpawnDelay == that.minSpawnDelay
                && maxSpawnDelay == that.maxSpawnDelay
                && spawnDelay == that.spawnDelay;
    }
}
