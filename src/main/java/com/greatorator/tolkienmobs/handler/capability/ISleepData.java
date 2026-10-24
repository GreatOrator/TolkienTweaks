package com.greatorator.tolkienmobs.handler.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public interface ISleepData {

  Identifier ID =
      Identifier.fromNamespaceAndPath(MODID, "sleep_data");
  String WAKE_TAG = "wakeTime";
  String TIRED_TAG = "tiredTime";
  String SLEEP_TAG = "sleepTime";

  long getSleepTime();

  void setSleepTime(long time);

  long getWakeTime();

  void setWakeTime(long wakeTime);

  long getTiredTime();

  void setTiredTime(long tiredTime);

  BlockPos getAutoSleepPos();

  void setAutoSleepPos(BlockPos pos);

  void copyFrom(ISleepData other);

  CompoundTag write();

  void read(CompoundTag tag);
}
