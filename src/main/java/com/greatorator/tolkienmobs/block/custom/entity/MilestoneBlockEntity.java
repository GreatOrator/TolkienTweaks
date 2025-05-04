package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.MilestoneBlock;
import com.greatorator.tolkienmobs.containers.MilestoneContainer;
import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.greatorator.tolkienmobs.handler.interfaces.block.MilestoneSettingsBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.util.block.MilestoneSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MilestoneBlockEntity extends BlockEntity implements MenuProvider, MilestoneSettingsBlockEntity {
    public MilestoneSettings milestoneSettings = new MilestoneSettings(500, 1, "Enter Name", UUID.randomUUID().toString());
    public int travelDistance = 500;
    public int dimensionCost = 1;
    private BlockPos pos;

    public MilestoneBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TolkienBlocks.MILESTONE_BLOCK_ENTITY.get(), pPos, pBlockState);
        this.pos = pPos;
    }

    public void onRightClick(BlockState blockState, BlockPos blockPos, Player player) {
        TolkienMobsMain.LOGGER.warn(String.valueOf(pos));
        if (blockState.getBlock() == TolkienBlocks.MILESTONE_BLOCK.get() && !blockState.getValue(MilestoneBlock.LIT) && !player.isCreative()) {
            getUUID();
            MilestoneHandler.addPlayerToMilestone(this, player);
            markDirtyClient();
            level.setBlock(worldPosition, blockState.setValue(MilestoneBlock.LIT, true), 0);
        }
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

    public MilestoneSettings getMilestoneSettings() {
        return milestoneSettings;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    //This is a terrible hack, but it's the simplest way to set the block to active for specific players
    @OnlyIn(Dist.CLIENT)
    public void updateClientState() {
        if (MilestoneHandler.isKnownByClient(getUUID(), Minecraft.getInstance().player.getUUID())) {
            BlockState state = level.getBlockState(worldPosition);
            if (state.getBlock() == TolkienBlocks.MILESTONE_BLOCK.get() && !state.getValue(MilestoneBlock.LIT)) {
                level.setBlock(worldPosition, state.setValue(MilestoneBlock.LIT, true), 0);
            }
        }
    }

    public ItemStack getItemTravelCost(MilestoneHandler.MilestoneData dest) {
        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(TolkienMobsConfig.PAYMENT_ITEM.get())).getDefaultInstance().getItem());
        double dist = Math.sqrt(dest.getPos().distSqr(getBlockPos()));

        if (!dest.getWorldKey().registryKey().equals(level.dimension().registryKey())) {
            stack.setCount(dest.getDimensionCost());
        }else {
            stack.setCount(Math.max(1, (int) (dist / dest.getDistanceCost())));
        }
        return stack;
    }

    public int getExperienceTravelCost(MilestoneHandler.MilestoneData dest) {
        double dist = Math.sqrt(dest.getPos().distSqr(getBlockPos()));
        int levels;

        if (!dest.getWorldKey().registryKey().equals(level.dimension().registryKey())) {
            levels = dest.getDimensionCost();
        } else {
            levels = (Math.max(1, (int) (dist / dest.getDistanceCost())));
        }
        return levels;
    }

    public UUID getUUID() {
        if (getMilestoneSettings().uuid.isEmpty()) {
            getMilestoneSettings().uuid = UUID.randomUUID().toString();
        }
        return UUID.fromString(getMilestoneSettings().uuid);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.tolkienmobs.milestone_block");
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

//    public void receivePacketFromClient(MCDataInput input, ServerPlayer client, int id) {
//        super.receivePacketFromClient(input, client, id);
//
//        if (id <= 2 && !client.isCreative()) return;
//
//        switch (id) {
//            case 0:
//                ItemStack stack = client.getItemInHand(InteractionHand.MAIN_HAND).copy();
//                stack.setCount(1);
//                client.sendMessage(new TranslatableComponent("tolkienmobs.msg.payment").append(stack.getItem().getName(stack)), Util.NIL_UUID);
//                paymentItem.set(stack);
//                MilestoneHandler.updateMilestone(this);
//                break;
//            case 1:
//                distanceCost.set(input.readVarInt());
//                MilestoneHandler.updateMilestone(this);
//                break;
//            case 2:
//                dimensionCost.set(input.readVarInt());
//                MilestoneHandler.updateMilestone(this);
//                break;
//            case 3:
//                MilestoneHandler.MilestoneData data = MilestoneHandler.getMilestoneData(level, input.readUUID());
//                if (data == null) {
//                    client.sendMessage(new TranslatableComponent("tolkienmobs.msg.destination"), Util.NIL_UUID);
//                    break;
//                }
//
//                ItemStack cost = getTravelCost(data);
//                if (!cost.isEmpty()) {
//                    int found = 0;
//                    for (ItemStack item : client.getInventory().items) {
//                        if (item.sameItem(cost)) found += item.getCount();
//                    }
//                    if (found < cost.getCount()) {
//                        client.sendMessage(new TranslatableComponent("tolkienmobs.msg.payment.insufficient"), Util.NIL_UUID);
//                        break;
//                    }
//                    int needed = cost.getCount();
//                    for (ItemStack item : client.getInventory().items) {
//                        if (item.sameItem(cost)) {
//                            int count = item.getCount();
//                            item.shrink(Math.min(count, needed));
//                            needed -= count;
//                            if (needed <= 0) break;
//                        }
//                    }
//                }
//
//                BCoreNetwork.sendSound(client.level, client.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.3F, 0.5F, false);
//                TeleportUtils.teleportEntity(client, data.getWorldKey(), Vector3.fromBlockPosCenter(data.getPos()).add(1, 1, 0));
//                BCoreNetwork.sendSound(client.level, client.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.3F, 0.5F, false);
//
//                break;
//        }
//    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new MilestoneContainer(pContainerId, pPlayerInventory, this);
    }

    public int getMilestoneDistance() {
        return travelDistance;
    }

    public void setMilestoneDistance(int newDistance) {
        this.travelDistance = newDistance;
        markDirtyClient();
    }

    public int getMilestoneDimension() {
        return dimensionCost;
    }

    public void setMilestoneDimension(int newDimension) {
        this.dimensionCost = newDimension;
        markDirtyClient();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag nbt = this.saveWithoutMetadata(pRegistries);
        nbt.putString("milestoneTitle", getMilestoneSettings().name);
        nbt.putString("milestoneUUID", getMilestoneSettings().uuid);
        nbt.putInt("milestoneCost", getMilestoneDistance());
        nbt.putInt("milestoneDimension", getMilestoneDimension());
        saveAdditional(nbt, pRegistries);
        return nbt;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("milestoneCost", getMilestoneDistance());
        tag.putInt("milestoneDimension", getMilestoneDimension());
        tag.putString("milestoneTitle", getMilestoneSettings().name);
        tag.putString("milestoneUUID", getMilestoneSettings().uuid);
        tag.putLong("milestonePos", this.pos.asLong());
        this.saveMilestoneSettings(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        setMilestoneDistance(tag.getInt("milestoneCost"));
        setMilestoneDimension(tag.getInt("milestoneDimension"));
        this.pos = BlockPos.of(tag.getLong("milestonePos"));
        this.loadMilestoneSettings(tag);
        super.loadAdditional(tag, provider);
    }

    /** Unused */
    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, MilestoneBlockEntity milestoneBlockEntity) {
    }

    public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, MilestoneBlockEntity milestoneBlockEntity) {
    }
}
