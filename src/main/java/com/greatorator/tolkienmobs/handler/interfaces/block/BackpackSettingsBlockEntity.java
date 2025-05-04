package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.util.block.BackpackSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface BackpackSettingsBlockEntity {
    BackpackSettings getBackpackSettings();
    BlockEntity getBlockEntity();

    default void saveBackpackSettings(CompoundTag tag) {
        tag.putBoolean("sleepingBag", getBackpackSettings().sleepingBag);
        tag.putBoolean("campfire", getBackpackSettings().campfire);
        tag.putBoolean("upgrade", getBackpackSettings().upgrade);
    }

    default void loadBackpackSettings(CompoundTag tag) {
        getBackpackSettings().sleepingBag = tag.getBoolean("sleepingBag");
        getBackpackSettings().campfire = tag.getBoolean("campfire");
        getBackpackSettings().upgrade = tag.getBoolean("upgrade");
    }

    default void setBackpackSettings(BackpackSettings backpackData) {
        getBackpackSettings().sleepingBag = backpackData.sleepingBag;
        getBackpackSettings().campfire = backpackData.campfire;
        getBackpackSettings().upgrade = backpackData.upgrade;
        if (getBlockEntity() instanceof BackpackBlockEntity backpackBE)
            backpackBE.markDirtyClient();
    }
}
