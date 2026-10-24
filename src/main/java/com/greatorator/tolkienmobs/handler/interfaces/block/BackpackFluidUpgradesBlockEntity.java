package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.util.block.BackpackFluidUpgrades;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface BackpackFluidUpgradesBlockEntity {
    BackpackFluidUpgrades getBackpackFluidUpgrades();
    BlockEntity getBlockEntity();

    default void saveBackpackFluidUpgrades(ValueOutput output) {
        output.putBoolean("fluid_tank", getBackpackFluidUpgrades().fluid_tank);
        output.putBoolean("fluid_tank_2", getBackpackFluidUpgrades().fluid_tank_2);
        output.putBoolean("fluid_tank_3", getBackpackFluidUpgrades().fluid_tank_3);
        output.putBoolean("fluid_tank_4", getBackpackFluidUpgrades().fluid_tank_4);
        output.putBoolean("fluid_tank_5", getBackpackFluidUpgrades().fluid_tank_5);
    }

    default void loadBackpackFluidUpgrades(ValueInput input) {
        getBackpackFluidUpgrades().fluid_tank = input.getBooleanOr("fluid_tank", false);
        getBackpackFluidUpgrades().fluid_tank_2 = input.getBooleanOr("fluid_tank_2", false);
        getBackpackFluidUpgrades().fluid_tank_3 = input.getBooleanOr("fluid_tank_3", false);
        getBackpackFluidUpgrades().fluid_tank_4 = input.getBooleanOr("fluid_tank_4", false);
        getBackpackFluidUpgrades().fluid_tank_5 = input.getBooleanOr("fluid_tank_5", false);
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
