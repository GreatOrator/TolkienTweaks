package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.containers.PiggyBankContainer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.greatorator.tolkienmobs.handler.capability.TolkienItemStackHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jetbrains.annotations.Nullable;

public class PiggyBankBlockEntity extends TolkienBlockEntity implements MenuProvider {
    public final TolkienItemStackHandler itemHandler = new TolkienItemStackHandler(54) {
        @Override
        protected int getCapacity(int slot, ItemResource resource) {
            return 54;
        }

        @Override
        protected void onContentsChanged(int slot, ItemStack stack) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public PiggyBankBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TolkienBlocks.PIGGY_BANK_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.tolkienmobs.block.tolkienmobs.block_piggybank");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new PiggyBankContainer(pContainerId, pPlayerInventory, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        itemHandler.serialize(output.child("inventory"));

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        itemHandler.deserialize(input.childOrEmpty("inventory"));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    /**
     * 26.1 calls this instead of the block's removed {@code onRemove}, which is where the bank's
     * fifty-four slots are dropped.
     */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        this.drops();
        super.preRemoveSideEffects(pos, state);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.size());
        for(int i = 0; i < itemHandler.size(); i++) {
            inv.setItem(i, ItemUtil.getStack(itemHandler, i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

//    private boolean isFull() {
//        boolean flag = false;
//
//        return true;
//    }
//
//    public void tick(Level level, BlockPos pPos, BlockState pState) {
//        boolean flag = this.isFull();
//        boolean flag1 = false;
//
//        if (flag != this.isFull()) {
//            flag1 = true;
//            pState = pState.setValue(PiggyBankBlock.FULL, Boolean.valueOf(this.isFull()));
//            level.setBlock(pPos, pState, 3);
//        }
//    }
}