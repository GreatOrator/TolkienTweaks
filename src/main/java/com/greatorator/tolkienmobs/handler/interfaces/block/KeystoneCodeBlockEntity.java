package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.util.block.KeyStoneCode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface KeystoneCodeBlockEntity {
    KeyStoneCode getKeyStoneData();
    BlockEntity getBlockEntity();

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
}
