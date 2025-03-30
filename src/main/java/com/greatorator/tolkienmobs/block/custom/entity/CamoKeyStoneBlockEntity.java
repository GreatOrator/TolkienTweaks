package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.containers.CamoKeyStoneContainer;
import com.greatorator.tolkienmobs.handler.interfaces.TolkienRegistry;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.item.custom.KeyItem;
import com.greatorator.tolkienmobs.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static com.greatorator.tolkienmobs.block.custom.ChameleonBlock.ACTIVE;
import static com.greatorator.tolkienmobs.block.custom.ChameleonBlock.POWERED;

public class CamoKeyStoneBlockEntity extends BlockEntity implements MenuProvider, TolkienRegistry {
    public RedstoneControlData redstoneControlData = new RedstoneControlData();
    public KeyStoneSettings keyStoneSettings = new KeyStoneSettings(true);
    public KeyStoneCode keyStoneCode = new KeyStoneCode("Enter Code");
    public int tickDelay = 20;
    private int activeTime = 0;

    public CamoKeyStoneBlockEntity(BlockPos pos, BlockState blockState) {
        super(TolkienBlocks.KEY_STONE_BLOCK_ENTITY.get(), pos, blockState);
    }

    public void onRightClick(BlockState state, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof KeyItem && (KeyItem.getKeyCode(stack).equals(getKeyCode()))) {
            getRedstoneMode(this.level, this.worldPosition);
            int uses = KeyItem.getUses(stack);
            boolean mode = KeyItem.getMode(stack);

            if (mode) {
                if (KeyItem.getUses(stack) >= 0) {
                    this.level.sendBlockUpdated(worldPosition, state, state, 3);

                    if (uses == 0) {
                        stack.shrink(1);
                        this.level.playSound((Player) null, worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.3F, 0.6F);
                        player.sendSystemMessage(Component.translatable("tolkienmobs.msg.key_used").withStyle(ChatFormatting.RED));
                    }
                    KeyItem.setKeyData(stack, KeyItem.getKeyCode(stack), uses - 1, KeyItem.getMode(stack));
                }
            }
        } else {
            player.sendSystemMessage(Component.translatable("tolkienmobs.msg.wrong_key").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    public int getTickDelay() {
        return tickDelay;
    }

    public void setTickDelay(int newTickDelay) {
        this.tickDelay = newTickDelay;
        markDirtyClient();
    }

    @Override
    public RedstoneControlData getRedstoneControlData() {
        return redstoneControlData;
    }

    public KeyStoneSettings getKeyStoneSettings() {
        return keyStoneSettings;
    }

    public KeyStoneCode getKeyStoneData() {
        return keyStoneCode;
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
        return new CamoKeyStoneContainer(pContainerId, pPlayerInventory, this);
    }

    public String getKeyCode() {
        return getKeyStoneData().keyCode;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void getRedstoneMode(Level world, BlockPos blockPos) {
        if (getRedstoneControlData().redstoneMode.equals(GeneralUtility.RedstoneMode.TOGGLE)) {
            TolkienMobsMain.LOGGER.warn("Toggled");
            world.setBlockAndUpdate(blockPos, world.getBlockState(blockPos).cycle(ACTIVE));
            world.playSound((Player)null, blockPos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, 0.6F);
            world.setBlock(blockPos, world.getBlockState(blockPos).cycle(POWERED), 3);
        } else if (getRedstoneControlData().redstoneMode.equals(GeneralUtility.RedstoneMode.PULSE)) {
            TolkienMobsMain.LOGGER.warn("Pulse");
            world.setBlockAndUpdate(blockPos, world.getBlockState(blockPos).setValue(ACTIVE, true));
            world.setBlock(blockPos, world.getBlockState(blockPos).setValue(POWERED, true), 3);
            world.playSound((Player)null, blockPos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
        } else if (getRedstoneControlData().redstoneMode.equals(GeneralUtility.RedstoneMode.DELAY) && tickDelay >= 0) {
            TolkienMobsMain.LOGGER.warn("Delay");
            world.setBlockAndUpdate(blockPos, world.getBlockState(blockPos).setValue(ACTIVE, true));
            world.setBlock(blockPos, world.getBlockState(blockPos).setValue(POWERED, true), 3);
              world.playSound((Player)null, blockPos, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
        }
    }

    public void tickServer() {
        if (getRedstoneControlData().redstoneMode.equals(GeneralUtility.RedstoneMode.PULSE) && this.getBlockState().getValue(ACTIVE)) {
            if (activeTime == 30) {
                this.level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(ACTIVE, false).setValue(POWERED, false));
                this.getBlockState().setValue(ACTIVE, false);
                this.getBlockState().setValue(POWERED, false);
                this.level.playSound((Player)null, worldPosition, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.4F);

                activeTime = 0;
            }
            activeTime++;
        } else if (getRedstoneControlData().redstoneMode.equals(GeneralUtility.RedstoneMode.DELAY) && tickDelay >= 0 && this.getBlockState().getValue(ACTIVE)) {
            if (activeTime == getTickDelay()) {
                this.level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(ACTIVE, false).setValue(POWERED, false));
                this.getBlockState().setValue(ACTIVE, false);
                this.getBlockState().setValue(POWERED, false);
                this.level.playSound((Player)null, worldPosition, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.4F);

                activeTime = 0;
            }
            activeTime++;
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag nbt = this.saveWithoutMetadata(pRegistries);
        nbt.putString("keyCode", getKeyStoneData().keyCode);
        nbt.putInt("tickDelay", getTickDelay());
        saveAdditional(nbt, pRegistries);
        return nbt;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("tickDelay", getTickDelay());
        tag.putInt("activeTime", this.activeTime);
        this.saveKeyStoneSettings(tag);
        this.saveKeyStoneData(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        setTickDelay(tag.getInt("tickDelay"));
        this.activeTime = tag.getInt("activeTime");
        this.loadKeyStoneSettings(tag);
        this.loadKeyStoneData(tag);
        super.loadAdditional(tag, provider);
    }

    public void tickClient() {
    }

    @Override
    public BackpackSettings getBackpackSettings() {
        return null;
    }

    @Override
    public BackpackUpgrades getBackpackUpgrades() {
        return null;
    }

    @Override
    public BackpackFluidUpgrades getBackpackFluidUpgrades() {
        return null;
    }
}
