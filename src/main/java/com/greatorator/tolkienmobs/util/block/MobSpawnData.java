package com.greatorator.tolkienmobs.util.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class MobSpawnData {
    private final String entityId;
    private int spawnCount;
    private final ResourceLocation resourceLocation;

    public MobSpawnData(String entityId, int spawnCount) {
        this.entityId = entityId;
        this.spawnCount = spawnCount;
        this.resourceLocation = ResourceLocation.parse(entityId);
    }

    public MobSpawnData(CompoundTag tag) {
        this.entityId = tag.getString("entityID");
        this.spawnCount = tag.getInt("spawnCount");
        this.resourceLocation = ResourceLocation.parse(entityId);
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("entityID", entityId);
        tag.putInt("spawnCount", spawnCount);
        return tag;
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

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }
}
