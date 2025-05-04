package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.util.block.BackpackFluidUpgrades;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface BackpackFluidUpgradesBlockEntity {
    BackpackFluidUpgrades getBackpackFluidUpgrades();
    BlockEntity getBlockEntity();

    default void saveBackpackFluidUpgrades(CompoundTag tag) {
        tag.putBoolean("fluid_tank", getBackpackFluidUpgrades().fluid_tank);
        tag.putBoolean("fluid_tank_2", getBackpackFluidUpgrades().fluid_tank_2);
        tag.putBoolean("fluid_tank_3", getBackpackFluidUpgrades().fluid_tank_3);
        tag.putBoolean("fluid_tank_4", getBackpackFluidUpgrades().fluid_tank_4);
        tag.putBoolean("fluid_tank_5", getBackpackFluidUpgrades().fluid_tank_5);
    }

    default void loadBackpackFluidUpgrades(CompoundTag tag) {
        getBackpackFluidUpgrades().fluid_tank = tag.getBoolean("fluid_tank");
        getBackpackFluidUpgrades().fluid_tank_2 = tag.getBoolean("fluid_tank_2");
        getBackpackFluidUpgrades().fluid_tank_3 = tag.getBoolean("fluid_tank_3");
        getBackpackFluidUpgrades().fluid_tank_4 = tag.getBoolean("fluid_tank_4");
        getBackpackFluidUpgrades().fluid_tank_5 = tag.getBoolean("fluid_tank_5");
    }

    default void setBackpackFluidUpgrades(BackpackFluidUpgrades backpackData) {
        getBackpackFluidUpgrades().fluid_tank = backpackData.fluid_tank;
        getBackpackFluidUpgrades().fluid_tank_2 = backpackData.fluid_tank_2;
        getBackpackFluidUpgrades().fluid_tank_3 = backpackData.fluid_tank_3;
        getBackpackFluidUpgrades().fluid_tank_4 = backpackData.fluid_tank_4;
        getBackpackFluidUpgrades().fluid_tank_5 = backpackData.fluid_tank_5;
        if (getBlockEntity() instanceof BackpackBlockEntity backpackBE)
            backpackBE.markDirtyClient();
    }
}
