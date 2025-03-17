package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.containers.LockableTreasureChestContainer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
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
import org.jetbrains.annotations.Nullable;

public class LockableTreasureChestBlockEntity extends BlockEntity implements MenuProvider {
    private static String keyCode;


    public LockableTreasureChestBlockEntity(BlockPos pos, BlockState blockState) {
        super(TolkienBlocks.LOCKABLE_TREASURE_CHEST_BLOCK_ENTITY.get(), pos, blockState);
        keyCode = this.getData(TolkienDataComponents.CHEST_CODE);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.tolkienmobs.block.tolkienmobs.lockable_treasure_chest_block");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new LockableTreasureChestContainer(pContainerId, pPlayerInventory, this);
    }

    public static void setKeyCode(LockableTreasureChestBlockEntity entity, String text) {
        keyCode = entity.setData(TolkienDataComponents.CHEST_CODE, text);
    }

    public String getKeyCode() {
        return this.getData(TolkienDataComponents.CHEST_CODE);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag nbt = this.saveWithoutMetadata(pRegistries);
        nbt.putString("chest_code", keyCode);

        return nbt;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.putString("chest_code", keyCode);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        keyCode = String.valueOf(pTag.get("chest_code"));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}