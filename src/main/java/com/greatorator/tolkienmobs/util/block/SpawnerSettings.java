package com.greatorator.tolkienmobs.util.block;

import java.util.Objects;

public class SpawnerSettings {
    public boolean requirePlayer = true;
    public boolean ignoreSpawnReq = true;
    public boolean redstoneControl = true;
    public boolean spawnerParticles = true;
    public boolean spawnAllTogether = false; // false = random, true = spawn all

    public SpawnerSettings() {

    }

    public SpawnerSettings(boolean requirePlayer, boolean ignoreSpawnReq, boolean redstoneControl, boolean spawnerParticles) {
        this.requirePlayer = requirePlayer;
        this.ignoreSpawnReq = ignoreSpawnReq;
        this.redstoneControl = redstoneControl;
        this.spawnerParticles = spawnerParticles;
    }

    public SpawnerSettings(boolean requirePlayer, boolean ignoreSpawnReq, boolean redstoneControl, boolean spawnerParticles, boolean spawnAllTogether) {
        this.requirePlayer = requirePlayer;
        this.ignoreSpawnReq = ignoreSpawnReq;
        this.redstoneControl = redstoneControl;
        this.spawnerParticles = spawnerParticles;
        this.spawnAllTogether = spawnAllTogether;
    }


    @Override
    public int hashCode() {
        return Objects.hash(requirePlayer, ignoreSpawnReq, redstoneControl, spawnerParticles, spawnAllTogether);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SpawnerSettings that = (SpawnerSettings) object;
        return requirePlayer == that.requirePlayer
                && ignoreSpawnReq == that.ignoreSpawnReq
                && redstoneControl == that.redstoneControl
                && spawnerParticles == that.spawnerParticles
                && spawnAllTogether == that.spawnAllTogether;
    }
}
