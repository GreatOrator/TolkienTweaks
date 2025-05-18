package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.handler.data.MCDataInput;
import com.greatorator.tolkienmobs.handler.data.MCDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface IManagedData {

    String getName();

    int getIndex();

    void toBytes(MCDataOutput output);

    void fromBytes(MCDataInput input);

    void toNBT(HolderLookup.Provider provider, CompoundTag compound);

    void fromNBT(HolderLookup.Provider provider, CompoundTag compound);

    void markDirty();

    boolean isDirty(boolean reset);

    void init(IDataManager dataManager, int index);

    IDataManager getDataManager();

    DataFlags flags();

    void validate();
}