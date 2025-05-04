package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.util.block.BackpackPlacement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface BackpackPlacementBlockEntity {
    BackpackPlacement getBackpackPlacement();
    BlockEntity getBlockEntity();

    default void saveBackpackPlacement(CompoundTag tag) {
        tag.putBoolean("sleepingBag", getBackpackPlacement().sleepingBag);
        tag.putBoolean("campfire", getBackpackPlacement().campfire);
    }

    default void loadBackpackPlacement(CompoundTag tag) {
        getBackpackPlacement().sleepingBag = tag.getBoolean("sleepingBag");
        getBackpackPlacement().campfire = tag.getBoolean("campfire");
    }

    default void setBackpackPlacement(BackpackPlacement backpackData) {
        getBackpackPlacement().sleepingBag = backpackData.sleepingBag;
        getBackpackPlacement().campfire = backpackData.campfire;
        if (getBlockEntity() instanceof BackpackBlockEntity backpackBE)
            backpackBE.markDirtyClient();
    }
}
