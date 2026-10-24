package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.util.block.BackpackUpgrades;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface BackpackUpgradesBlockEntity {
    BackpackUpgrades getBackpackUpgrades();
    BlockEntity getBlockEntity();

    default void saveBackpackUpgrades(ValueOutput output) {
        output.putBoolean("crafting", getBackpackUpgrades().crafting);
        output.putBoolean("sleepingBagUpgrade", getBackpackUpgrades().sleepingBag);
        output.putBoolean("campfireUpgrade", getBackpackUpgrades().campfire);
        output.putBoolean("size_upgrade", getBackpackUpgrades().size_upgrade);
        output.putBoolean("size_upgrade_2", getBackpackUpgrades().size_upgrade_2);
    }

    default void loadBackpackUpgrades(ValueInput input) {
        getBackpackUpgrades().crafting = input.getBooleanOr("crafting", false);
        getBackpackUpgrades().sleepingBag = input.getBooleanOr("sleepingBagUpgrade", false);
        getBackpackUpgrades().campfire = input.getBooleanOr("campfireUpgrade", false);
        getBackpackUpgrades().size_upgrade = input.getBooleanOr("size_upgrade", false);
        getBackpackUpgrades().size_upgrade_2 = input.getBooleanOr("size_upgrade_2", false);
    }

    default void setBackpackUpgrades(BackpackUpgrades backpackData) {
        getBackpackUpgrades().crafting = backpackData.crafting;
        getBackpackUpgrades().sleepingBag = backpackData.sleepingBag;
        getBackpackUpgrades().campfire = backpackData.campfire;
        getBackpackUpgrades().size_upgrade = backpackData.size_upgrade;
        getBackpackUpgrades().size_upgrade_2 = backpackData.size_upgrade_2;
        if (getBlockEntity() instanceof BackpackBlockEntity backpackBE)
            backpackBE.markDirtyClient();
    }
}
