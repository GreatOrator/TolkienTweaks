package com.greatorator.tolkienmobs.util;

import java.util.Objects;

public class SpawnerSettings {
    public boolean requirePlayer = true;
    public boolean ignoreSpawnReq = true;
    public boolean spawnerParticles = true;

    public SpawnerSettings() {

    }

    public SpawnerSettings(boolean requirePlayer, boolean ignoreSpawnReq, boolean spawnerParticles) {
        this.requirePlayer = requirePlayer;
        this.ignoreSpawnReq = ignoreSpawnReq;
        this.spawnerParticles = spawnerParticles;
    }


    @Override
    public int hashCode() {
        return Objects.hash(requirePlayer, ignoreSpawnReq, spawnerParticles);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SpawnerSettings that = (SpawnerSettings) object;
        return requirePlayer == that.requirePlayer
                && ignoreSpawnReq == that.ignoreSpawnReq
                && spawnerParticles == that.spawnerParticles;
    }
}
