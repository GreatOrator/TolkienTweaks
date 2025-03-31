package com.greatorator.tolkienmobs.handler.interfaces;

import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.RedstoneControlData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static com.greatorator.tolkienmobs.block.custom.ChameleonBlock.ACTIVE;

public interface RedstoneControlledBlockEntity {
    RedstoneControlData getRedstoneControlData();
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
}
