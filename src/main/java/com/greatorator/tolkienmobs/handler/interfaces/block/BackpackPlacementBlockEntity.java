package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.util.block.BackpackPlacement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface BackpackPlacementBlockEntity {
    BackpackPlacement getBackpackPlacement();
    BlockEntity getBlockEntity();

    default void saveBackpackPlacement(ValueOutput output) {
        output.putBoolean("sleepingBag", getBackpackPlacement().sleepingBag);
        output.putBoolean("campfire", getBackpackPlacement().campfire);
    }

    default void loadBackpackPlacement(ValueInput input) {
        getBackpackPlacement().sleepingBag = input.getBooleanOr("sleepingBag", false);
        getBackpackPlacement().campfire = input.getBooleanOr("campfire", false);
    }

    default void setBackpackPlacement(BackpackPlacement backpackData) {
        getBackpackPlacement().sleepingBag = backpackData.sleepingBag;
        getBackpackPlacement().campfire = backpackData.campfire;
        if (getBlockEntity() instanceof BackpackBlockEntity backpackBE)
            backpackBE.markDirtyClient();
    }
}
