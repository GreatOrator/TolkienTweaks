package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.greatorator.tolkienmobs.util.block.SpawnerDelays;
import com.greatorator.tolkienmobs.util.block.SpawnerRanges;
import com.greatorator.tolkienmobs.util.block.SpawnerSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface SpawnerSettingsBlockEntity {
    SpawnerSettings getSpawnerSettings();
    SpawnerDelays getSpawnerDelays();
    SpawnerRanges getSpawnerRanges();
    BlockEntity getBlockEntity();

    default void saveSpawnerSettings(CompoundTag tag) {
        tag.putBoolean("requirePlayer", getSpawnerSettings().requirePlayer);
        tag.putBoolean("ignoreSpawnReq", getSpawnerSettings().ignoreSpawnReq);
        tag.putBoolean("spawnerParticles", getSpawnerSettings().spawnerParticles);
        tag.putShort("minSpawnDelay", (short) getSpawnerDelays().minSpawnDelay);
        tag.putShort("maxSpawnDelay", (short) getSpawnerDelays().maxSpawnDelay);
        tag.putShort("spawnDelay", (short) getSpawnerDelays().spawnDelay);
        tag.putShort("activationRange", (short) getSpawnerRanges().activationRange);
        tag.putShort("spawnRange", (short) getSpawnerRanges().spawnRange);
        tag.putShort("spawnCount", (short) getSpawnerRanges().spawnCount);
        tag.putShort("maxCluster", (short) getSpawnerRanges().maxCluster);
        tag.putShort("clusterRange", (short) getSpawnerRanges().clusterRange);
    }

    default void loadSpawnerSettings(CompoundTag tag) {
        getSpawnerSettings().requirePlayer = tag.getBoolean("requirePlayer");
        getSpawnerSettings().ignoreSpawnReq = tag.getBoolean("ignoreSpawnReq");
        getSpawnerSettings().spawnerParticles = tag.getBoolean("spawnerParticles");
        getSpawnerDelays().minSpawnDelay = tag.getShort("minSpawnDelay");
        getSpawnerDelays().maxSpawnDelay = tag.getShort("maxSpawnDelay");
        getSpawnerDelays().spawnDelay = tag.getShort("spawnDelay");
        getSpawnerRanges().activationRange = tag.getShort("activationRange");
        getSpawnerRanges().spawnRange = tag.getShort("spawnRange");
        getSpawnerRanges().spawnCount = tag.getShort("spawnCount");
        getSpawnerRanges().maxCluster = tag.getShort("maxCluster");
        getSpawnerRanges().clusterRange = tag.getShort("clusterRange");
    }

    default void setSpawnerSettings(SpawnerSettings spawnerData) {
        getSpawnerSettings().requirePlayer = spawnerData.requirePlayer;
        getSpawnerSettings().ignoreSpawnReq = spawnerData.ignoreSpawnReq;
        getSpawnerSettings().spawnerParticles = spawnerData.spawnerParticles;
        if (getBlockEntity() instanceof CamoSpawnerBlockEntity spawnerBE)
            spawnerBE.markDirtyClient();
    }

    default void setSpawnerDelays(SpawnerDelays spawnerData) {
        getSpawnerDelays().minSpawnDelay = spawnerData.minSpawnDelay;
        getSpawnerDelays().maxSpawnDelay = spawnerData.maxSpawnDelay;
        getSpawnerDelays().spawnDelay = spawnerData.spawnDelay;
        if (getBlockEntity() instanceof CamoSpawnerBlockEntity spawnerBE)
            spawnerBE.markDirtyClient();
    }

    default void setSpawnerRanges(SpawnerRanges spawnerData) {
        getSpawnerRanges().activationRange = spawnerData.activationRange;
        getSpawnerRanges().spawnRange = spawnerData.spawnRange;
        getSpawnerRanges().spawnCount = spawnerData.spawnCount;
        getSpawnerRanges().maxCluster = spawnerData.maxCluster;
        getSpawnerRanges().clusterRange = spawnerData.clusterRange;
        if (getBlockEntity() instanceof CamoSpawnerBlockEntity spawnerBE)
            spawnerBE.markDirtyClient();
    }
}
