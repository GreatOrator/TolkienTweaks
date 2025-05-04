package com.greatorator.tolkienmobs.handler.interfaces.block;

import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.util.block.MilestoneSettings;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface MilestoneSettingsBlockEntity {
    MilestoneSettings getMilestoneSettings();
    BlockEntity getBlockEntity();

    default void saveMilestoneSettings(CompoundTag tag) {
        tag.putInt("milestoneCost", getMilestoneSettings().distance);
        tag.putInt("milestoneDimension", getMilestoneSettings().dimension);
        tag.putString("milestoneTitle", getMilestoneSettings().name);
        tag.putString("milestoneUUID", getMilestoneSettings().uuid);
    }

    default void loadMilestoneSettings(CompoundTag tag) {
        getMilestoneSettings().distance = tag.getInt("milestoneCost");
        getMilestoneSettings().dimension = tag.getInt("milestoneDimension");
        getMilestoneSettings().name = tag.getString("milestoneTitle");
        getMilestoneSettings().uuid = tag.getString("milestoneUUID");
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
