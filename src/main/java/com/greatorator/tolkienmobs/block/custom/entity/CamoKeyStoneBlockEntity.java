package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.containers.CamoKeyStoneContainer;
import com.greatorator.tolkienmobs.handler.interfaces.TolkienRegistry;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.KeyStoneData;
import com.greatorator.tolkienmobs.util.KeyStoneSettings;
import com.greatorator.tolkienmobs.util.RedstoneControlData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class CamoKeyStoneBlockEntity extends BlockEntity implements MenuProvider, TolkienRegistry {
    public RedstoneControlData redstoneControlData = new RedstoneControlData();
    public KeyStoneSettings keyStoneSettings = new KeyStoneSettings(true);
    public KeyStoneData keyStoneData = new KeyStoneData("Enter Code");
    public static int tickDelay = 20;

    public CamoKeyStoneBlockEntity(BlockPos pos, BlockState blockState) {
        super(TolkienBlocks.KEY_STONE_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    @Override
    public RedstoneControlData getRedstoneControlData() {
        return redstoneControlData;
    }

    public KeyStoneSettings getKeyStoneSettings() {
        return keyStoneSettings;
    }

    public KeyStoneData getKeyStoneData() {
        return keyStoneData;
    }

    public void markDirtyClient() {
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 3);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.tolkienmobs.block.tolkienmobs.block_key_stone");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        TolkienMobsMain.LOGGER.warn(getKeyCode());
        return new CamoKeyStoneContainer(pContainerId, pPlayerInventory, this);
    }

    public String getKeyCode() {
        return getKeyStoneData().keyCode;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag nbt = this.saveWithoutMetadata(pRegistries);
        nbt.putString("keyCode", getKeyStoneData().keyCode);
        saveAdditional(nbt, pRegistries);
        return nbt;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        this.saveKeyStoneSettings(tag);
        this.saveKeyStoneData(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        this.loadKeyStoneSettings(tag);
        this.loadKeyStoneData(tag);
        super.loadAdditional(tag, provider);
    }
}
