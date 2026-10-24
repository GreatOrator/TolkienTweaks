package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.util.block.KeyStoneCode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface KeystoneCodeBlockEntity {
    KeyStoneCode getKeyStoneData();
    BlockEntity getBlockEntity();

    default void saveKeyStoneData(ValueOutput output) {
        output.putString("keyCode", getKeyStoneData().keyCode);
    }

    default void loadKeyStoneData(ValueInput input) {
        getKeyStoneData().keyCode = input.getStringOr("keyCode", "");
    }

    default void setKeyStoneData(KeyStoneCode keyStoneCode) {
        getKeyStoneData().keyCode = keyStoneCode.keyCode;
        if (getBlockEntity() instanceof CamoKeyStoneBlockEntity keyStoneBE)
            keyStoneBE.markDirtyClient();
    }
}
