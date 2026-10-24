package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.containers.CamoChestContainer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * The camouflage chest's storage. Everything visible about the block comes from {@code ChameleonBlock}'s
 * camo model, so the block entity only has to hold the twenty-seven slots and keep the chest's own
 * behaviour - opening and closing sounds, the comparator reading and the openers count - from vanilla.
 */
public class CamoChestBlockEntity extends RandomizableContainerBlockEntity {
    public static final int ROWS = 3;
    public static final int SLOTS = ROWS * 9;

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);

    private final ContainerOpenersCounter openers = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            CamoChestBlockEntity.this.playSound(state, SoundEvents.CHEST_OPEN);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            CamoChestBlockEntity.this.playSound(state, SoundEvents.CHEST_CLOSE);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int previousCount) {
            if (!level.isClientSide()) {
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof CamoChestContainer menu && menu.isFor(CamoChestBlockEntity.this);
        }
    };

    public CamoChestBlockEntity(BlockPos pos, BlockState state) {
        super(TolkienBlocks.CAMO_CHEST_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public int getContainerSize() {
        return SLOTS;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return new CamoChestContainer(containerId, playerInventory, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator() && this.level != null && !this.level.isClientSide()) {
            this.openers.incrementOpeners(player, this.level, this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator() && this.level != null && !this.level.isClientSide()) {
            this.openers.decrementOpeners(player, this.level, this.getBlockPos(), this.getBlockState());
        }
    }

    private void playSound(BlockState state, SoundEvent sound) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        this.level.playSound(null, this.getBlockPos(), sound, SoundSource.BLOCKS, 0.5F, this.level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.trySaveLootTable(output)) {
            ContainerHelper.saveAllItems(output, this.items, registries);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(input)) {
            ContainerHelper.loadAllItems(input, this.items, registries);
        }
    }
}
