package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.util.block.MilestoneSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface MilestoneSettingsBlockEntity {
    MilestoneSettings getMilestoneSettings();
    BlockEntity getBlockEntity();

    default void saveMilestoneSettings(ValueOutput output) {
        output.putInt("milestoneCost", getMilestoneSettings().distance);
        output.putInt("milestoneDimension", getMilestoneSettings().dimension);
        output.putString("milestoneTitle", getMilestoneSettings().name);
        output.putString("milestoneUUID", getMilestoneSettings().uuid);
    }

    default void loadMilestoneSettings(ValueInput input) {
        getMilestoneSettings().distance = input.getIntOr("milestoneCost", 0);
        getMilestoneSettings().dimension = input.getIntOr("milestoneDimension", 0);
        getMilestoneSettings().name = input.getStringOr("milestoneTitle", "");
        getMilestoneSettings().uuid = input.getStringOr("milestoneUUID", "");
    }

    default void setMilestoneSettings(MilestoneSettings milestoneData) {
        getMilestoneSettings().distance = milestoneData.distance;
        getMilestoneSettings().dimension = milestoneData.dimension;
        getMilestoneSettings().name = milestoneData.name;
        getMilestoneSettings().uuid = milestoneData.uuid;
        if (getBlockEntity() instanceof MilestoneBlockEntity milestoneBE)
            milestoneBE.markDirtyClient();
    }
}
