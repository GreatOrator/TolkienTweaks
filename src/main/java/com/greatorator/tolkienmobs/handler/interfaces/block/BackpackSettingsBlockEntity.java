package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.util.block.BackpackSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface BackpackSettingsBlockEntity {
    BackpackSettings getBackpackSettings();
    BlockEntity getBlockEntity();

    default void saveBackpackSettings(ValueOutput output) {
        output.putBoolean("sleepingBag", getBackpackSettings().sleepingBag);
        output.putBoolean("campfire", getBackpackSettings().campfire);
        output.putBoolean("upgrade", getBackpackSettings().upgrade);
        output.putBoolean("magnet", getBackpackSettings().magnet);
    }

    default void loadBackpackSettings(ValueInput input) {
        getBackpackSettings().sleepingBag = input.getBooleanOr("sleepingBag", false);
        getBackpackSettings().campfire = input.getBooleanOr("campfire", false);
        getBackpackSettings().upgrade = input.getBooleanOr("upgrade", false);
        // A backpack saved before the magnet existed loads with the magnet on, the same order a
        // newly fitted upgrade arrives in.
        getBackpackSettings().magnet = input.getBooleanOr("magnet", true);
    }

    default void setBackpackSettings(BackpackSettings backpackData) {
        getBackpackSettings().sleepingBag = backpackData.sleepingBag;
        getBackpackSettings().campfire = backpackData.campfire;
        getBackpackSettings().upgrade = backpackData.upgrade;
        getBackpackSettings().magnet = backpackData.magnet;
        if (getBlockEntity() instanceof BackpackBlockEntity backpackBE)
            backpackBE.markDirtyClient();
    }
}
