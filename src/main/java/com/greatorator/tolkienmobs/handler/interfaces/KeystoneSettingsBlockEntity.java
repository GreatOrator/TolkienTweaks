package com.greatorator.tolkienmobs.handler.interfaces;

import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.KeyStoneSettings;
import com.greatorator.tolkienmobs.util.RedstoneControlData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface KeystoneSettingsBlockEntity {
    KeyStoneSettings getKeyStoneSettings();
    BlockEntity getBlockEntity();
    RedstoneControlData getRedstoneControlData();

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
