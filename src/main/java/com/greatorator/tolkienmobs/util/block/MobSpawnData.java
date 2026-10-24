package com.greatorator.tolkienmobs.util.block;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MobSpawnData {
    private final String entityId;
    private int spawnCount;
    private final Identifier resourceLocation;

    public MobSpawnData(String entityId, int spawnCount) {
        this.entityId = entityId;
        this.spawnCount = spawnCount;
        this.resourceLocation = Identifier.parse(entityId);
    }

    public MobSpawnData(ValueInput input) {
        this.entityId = input.getStringOr("entityID", "");
        this.spawnCount = input.getIntOr("spawnCount", 0);
        this.resourceLocation = Identifier.parse(entityId);
    }

    public void save(ValueOutput output) {
        output.putString("entityID", entityId);
        output.putInt("spawnCount", spawnCount);
    }

    public String getEntityId() {
        return entityId;
    }

    public int getSpawnCount() {
        return spawnCount;
    }

    public void setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
    }

    public Identifier getResourceLocation() {
        return resourceLocation;
    }
}
