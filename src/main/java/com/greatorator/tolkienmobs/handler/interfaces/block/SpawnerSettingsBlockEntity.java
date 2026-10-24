package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.greatorator.tolkienmobs.util.block.SpawnerDelays;
import com.greatorator.tolkienmobs.util.block.SpawnerRanges;
import com.greatorator.tolkienmobs.util.block.SpawnerSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface SpawnerSettingsBlockEntity {
    SpawnerSettings getSpawnerSettings();
    SpawnerDelays getSpawnerDelays();
    SpawnerRanges getSpawnerRanges();
    BlockEntity getBlockEntity();

    default void saveSpawnerSettings(ValueOutput output) {
        output.putBoolean("requirePlayer", getSpawnerSettings().requirePlayer);
        output.putBoolean("ignoreSpawnReq", getSpawnerSettings().ignoreSpawnReq);
        output.putBoolean("spawnerParticles", getSpawnerSettings().spawnerParticles);
        output.putShort("minSpawnDelay", (short) getSpawnerDelays().minSpawnDelay);
        output.putShort("maxSpawnDelay", (short) getSpawnerDelays().maxSpawnDelay);
        output.putShort("spawnDelay", (short) getSpawnerDelays().spawnDelay);
        output.putShort("activationRange", (short) getSpawnerRanges().activationRange);
        output.putShort("spawnRange", (short) getSpawnerRanges().spawnRange);
        output.putShort("spawnCount", (short) getSpawnerRanges().spawnCount);
        output.putShort("maxCluster", (short) getSpawnerRanges().maxCluster);
        output.putShort("clusterRange", (short) getSpawnerRanges().clusterRange);
    }

    default void loadSpawnerSettings(ValueInput input) {
        getSpawnerSettings().requirePlayer = input.getBooleanOr("requirePlayer", false);
        getSpawnerSettings().ignoreSpawnReq = input.getBooleanOr("ignoreSpawnReq", false);
        getSpawnerSettings().spawnerParticles = input.getBooleanOr("spawnerParticles", false);
        getSpawnerDelays().minSpawnDelay = input.getShortOr("minSpawnDelay", (short) 0);
        getSpawnerDelays().maxSpawnDelay = input.getShortOr("maxSpawnDelay", (short) 0);
        getSpawnerDelays().spawnDelay = input.getShortOr("spawnDelay", (short) 0);
        getSpawnerRanges().activationRange = input.getShortOr("activationRange", (short) 0);
        getSpawnerRanges().spawnRange = input.getShortOr("spawnRange", (short) 0);
        getSpawnerRanges().spawnCount = input.getShortOr("spawnCount", (short) 0);
        getSpawnerRanges().maxCluster = input.getShortOr("maxCluster", (short) 0);
        getSpawnerRanges().clusterRange = input.getShortOr("clusterRange", (short) 0);
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
