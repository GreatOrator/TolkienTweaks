package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.handler.data.MCDataInput;
import com.greatorator.tolkienmobs.network.packet.PacketCustom;
import net.minecraft.nbt.CompoundTag;

public interface IDataManager {
    void detectAndSendChanges();

    PacketCustom createSyncPacket();

    void receiveSyncData(MCDataInput input);

    IManagedData getDataByName(String name);

    IManagedData getDataByIndex(int index);

    void writeToNBT(CompoundTag compound);

    void readFromNBT(CompoundTag compound);

    void markDirty();

    boolean isClientSide();

    void sendToServer(IManagedData data);
}