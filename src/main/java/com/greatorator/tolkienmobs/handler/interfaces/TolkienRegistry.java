package com.greatorator.tolkienmobs.handler.interfaces;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienWoodTypes;
import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import com.greatorator.tolkienmobs.util.*;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.block.custom.CamoKeyStoneBlock.ACTIVE;

public interface TolkienRegistry {
    ResourceKey<Registry<TolkienWoodTypes>> WOOD_TYPE_REGISTRY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "b_wood_type"));
    ResourceKey<Registry<TrinketRecipe>> TRINKET_RECIPES = ResourceKey.createRegistryKey(TolkienMobsMain.createResourceLocation("recipes/trinket_table"));

    RedstoneControlData getRedstoneControlData();
    KeyStoneSettings getKeyStoneSettings();
    BackpackSettings getBackpackSettings();
    BackpackUpgrades getBackpackUpgrades();
    BackpackFluidUpgrades getBackpackFluidUpgrades();
    KeyStoneCode getKeyStoneData();
    BlockEntity getBlockEntity();

    default void setRedstoneSettings(int redstoneMode) {
        getRedstoneControlData().redstoneMode = GeneralUtility.RedstoneMode.values()[redstoneMode];
        if (getBlockEntity() instanceof CamoKeyStoneBlockEntity keyStoneBE)
            keyStoneBE.markDirtyClient();
        BlockState blockState = getBlockEntity().getBlockState();
        if (blockState.hasProperty(ACTIVE)) {
            getBlockEntity().getLevel().setBlockAndUpdate(getBlockEntity().getBlockPos(), blockState.setValue(ACTIVE, isActiveRedstoneTestOnly()));
        }
    }

    default boolean isActiveRedstoneTestOnly() {
        if (getRedstoneControlData().redstoneMode.equals(GeneralUtility.RedstoneMode.TOGGLE))
            return true;
        if (getRedstoneControlData().redstoneMode.equals(GeneralUtility.RedstoneMode.DELAY))
            return getRedstoneControlData().receivingRedstone;
        if (getRedstoneControlData().redstoneMode.equals(GeneralUtility.RedstoneMode.PULSE) && getRedstoneControlData().pulsed) {
            return true;
        }
        return false;
    }

    default void saveKeyStoneData(CompoundTag tag) {
        tag.putString("keyCode", getKeyStoneData().keyCode);
    }

    default void loadKeyStoneData(CompoundTag tag) {
        getKeyStoneData().keyCode = tag.getString("keyCode");
    }

    default void setKeyStoneData(KeyStoneCode keyStoneCode) {
        getKeyStoneData().keyCode = keyStoneCode.keyCode;
        if (getBlockEntity() instanceof CamoKeyStoneBlockEntity keyStoneBE)
            keyStoneBE.markDirtyClient();
    }

    default void saveKeyStoneSettings(CompoundTag tag) {
        tag.putBoolean("keepKey", getKeyStoneSettings().keepKey);
        tag.putInt("redstoneMode", getRedstoneControlData().redstoneMode.ordinal());
        tag.putBoolean("pulsed", getRedstoneControlData().pulsed);
        tag.putBoolean("receivingRedstone", getRedstoneControlData().receivingRedstone);
    }

    default void loadKeyStoneSettings(CompoundTag tag) {
        getKeyStoneSettings().keepKey = tag.getBoolean("keepKey");
        if (tag.contains("redstoneMode")) { //Assume all the others are there too...
            getRedstoneControlData().redstoneMode = GeneralUtility.RedstoneMode.values()[tag.getInt("redstoneMode")];
            getRedstoneControlData().pulsed = tag.getBoolean("pulsed");
            getRedstoneControlData().receivingRedstone = tag.getBoolean("receivingRedstone");
        }
    }

    default void setKeyStoneSettings(KeyStoneSettings keyStoneData) {
        getKeyStoneSettings().keepKey = keyStoneData.keepKey;
        if (getBlockEntity() instanceof CamoKeyStoneBlockEntity keyStoneBE)
            keyStoneBE.markDirtyClient();
    }

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