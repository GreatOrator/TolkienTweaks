package com.greatorator.tolkienmobs.handler.interfaces;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienWoodTypes;
import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.KeyStoneData;
import com.greatorator.tolkienmobs.util.KeyStoneSettings;
import com.greatorator.tolkienmobs.util.RedstoneControlData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.block.custom.CamoKeyStoneBlock.*;
import static com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity.tickDelay;

public interface TolkienRegistry {
    ResourceKey<Registry<TolkienWoodTypes>> WOOD_TYPE_REGISTRY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "b_wood_type"));
    ResourceKey<Registry<TrinketRecipe>> TRINKET_RECIPES = ResourceKey.createRegistryKey(TolkienMobsMain.createResourceLocation("recipes/trinket_table"));

    RedstoneControlData getRedstoneControlData();
    KeyStoneSettings getKeyStoneSettings();
    KeyStoneData getKeyStoneData();
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

    default void redstoneMode(Level world, BlockPos blockPos) {
        if (getRedstoneControlData().redstoneMode.equals(GeneralUtility.RedstoneMode.TOGGLE)) {
            world.setBlockAndUpdate(blockPos, world.getBlockState(blockPos).cycle(ACTIVE));
            world.playSound((Player)null, blockPos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, 0.6F);
            world.setBlock(blockPos, world.getBlockState(blockPos).cycle(POWERED), 3);
        } else if (getRedstoneControlData().redstoneMode.equals(GeneralUtility.RedstoneMode.PULSE)) {
            world.setBlockAndUpdate(blockPos, world.getBlockState(blockPos).setValue(ACTIVE, true));
            world.setBlock(blockPos, world.getBlockState(blockPos).setValue(POWERED, true), 3);
            world.playSound((Player)null, blockPos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
        } else if (getRedstoneControlData().redstoneMode.equals(GeneralUtility.RedstoneMode.DELAY) && tickDelay > 0) {
            world.setBlockAndUpdate(blockPos, world.getBlockState(blockPos).setValue(ACTIVE, true));
            world.setBlock(blockPos, world.getBlockState(blockPos).setValue(POWERED, true), 3);
            world.playSound((Player)null, blockPos, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
        }
    }

    default void saveKeyStoneData(CompoundTag tag) {
        tag.putString("keyCode", getKeyStoneData().keyCode);
//        tag.putBoolean("rsToggle", getKeyStoneSettings().rsToggle);
//        tag.putBoolean("rsPulse", getKeyStoneSettings().rsPulse);
//        tag.putBoolean("rsDelay", getKeyStoneSettings().rsDelay);
    }

    default void loadKeyStoneData(CompoundTag tag) {
        getKeyStoneData().keyCode = tag.getString("keyCode");
//        getKeyStoneSettings().rsToggle = tag.getBoolean("rsToggle");
//        getKeyStoneSettings().rsPulse = tag.getBoolean("rsPulse");
//        getKeyStoneSettings().rsDelay = tag.getBoolean("rsDelay");
    }

    default void setKeyStoneData(KeyStoneData keyStoneData) {
        getKeyStoneData().keyCode = keyStoneData.keyCode;
//        getKeyStoneSettings().rsToggle = keyStoneSettings.rsToggle;
//        getKeyStoneSettings().rsPulse = keyStoneSettings.rsPulse;
//        getKeyStoneSettings().rsDelay = keyStoneSettings.rsDelay;
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
}