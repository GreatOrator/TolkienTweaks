package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.block.KeyStoneSettings;
import com.greatorator.tolkienmobs.util.block.RedstoneControlData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface KeystoneSettingsBlockEntity {
    KeyStoneSettings getKeyStoneSettings();
    BlockEntity getBlockEntity();
    RedstoneControlData getRedstoneControlData();

    default void saveKeyStoneSettings(ValueOutput output) {
        output.putBoolean("requirePlayer", getKeyStoneSettings().keepKey);
        output.putInt("redstoneMode", getRedstoneControlData().redstoneMode.ordinal());
        output.putBoolean("pulsed", getRedstoneControlData().pulsed);
        output.putBoolean("receivingRedstone", getRedstoneControlData().receivingRedstone);
    }

    default void loadKeyStoneSettings(ValueInput input) {
        getKeyStoneSettings().keepKey = input.getBooleanOr("requirePlayer", false);
        if (input.getInt("redstoneMode").isPresent()) { //Assume all the others are there too...
            getRedstoneControlData().redstoneMode = GeneralUtility.RedstoneMode.values()[input.getIntOr("redstoneMode", 0)];
            getRedstoneControlData().pulsed = input.getBooleanOr("pulsed", false);
            getRedstoneControlData().receivingRedstone = input.getBooleanOr("receivingRedstone", false);
        }
    }

    default void setKeyStoneSettings(KeyStoneSettings keyStoneData) {
        getKeyStoneSettings().keepKey = keyStoneData.keepKey;
        if (getBlockEntity() instanceof CamoKeyStoneBlockEntity keyStoneBE)
            keyStoneBE.markDirtyClient();
    }
}
