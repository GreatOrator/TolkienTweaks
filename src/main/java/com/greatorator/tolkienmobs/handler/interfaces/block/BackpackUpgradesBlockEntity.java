package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.util.block.BackpackUpgrades;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface BackpackUpgradesBlockEntity {
    BackpackUpgrades getBackpackUpgrades();
    BlockEntity getBlockEntity();

    default void saveBackpackUpgrades(CompoundTag tag) {
        tag.putBoolean("crafting", getBackpackUpgrades().crafting);
        tag.putBoolean("sleepingBagUpgrade", getBackpackUpgrades().sleepingBag);
        tag.putBoolean("campfireUpgrade", getBackpackUpgrades().campfire);
        tag.putBoolean("size_upgrade", getBackpackUpgrades().size_upgrade);
        tag.putBoolean("size_upgrade_2", getBackpackUpgrades().size_upgrade_2);
    }

    default void loadBackpackUpgrades(CompoundTag tag) {
        getBackpackUpgrades().crafting = tag.getBoolean("crafting");
        getBackpackUpgrades().sleepingBag = tag.getBoolean("sleepingBagUpgrade");
        getBackpackUpgrades().campfire = tag.getBoolean("campfireUpgrade");
        getBackpackUpgrades().size_upgrade = tag.getBoolean("size_upgrade");
        getBackpackUpgrades().size_upgrade_2 = tag.getBoolean("size_upgrade_2");
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
