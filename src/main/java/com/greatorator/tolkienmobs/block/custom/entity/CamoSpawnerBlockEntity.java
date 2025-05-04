package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.TolkienBaseSpawner;
import com.greatorator.tolkienmobs.containers.CamoSpawnerContainer;
import com.greatorator.tolkienmobs.handler.interfaces.block.SpawnerSettingsBlockEntity;
import com.greatorator.tolkienmobs.handler.interfaces.TolkienSpawner;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.util.block.SpawnerDelays;
import com.greatorator.tolkienmobs.util.block.SpawnerRanges;
import com.greatorator.tolkienmobs.util.block.SpawnerSettings;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CamoSpawnerBlockEntity extends BlockEntity implements MenuProvider, TolkienSpawner, SpawnerSettingsBlockEntity {
    public SpawnerSettings spawnerSettings = new SpawnerSettings(true, true, true);
    public SpawnerDelays spawnerDelays = new SpawnerDelays(200, 800, 20);
    public SpawnerRanges spawnerRanges = new SpawnerRanges(4, 4, 4, 6, 16);
    public List<CompoundTag> entityTags = new ArrayList<>(6);
    private final TolkienBaseSpawner spawnerBase = new TolkienBaseSpawner(this) {
        @Override
        public void broadcastEvent(Level level, BlockPos blockPos, int p_155769_) {
            level.blockEvent(blockPos, TolkienBlocks.CAMO_SPAWNER_BLOCK.get(), p_155769_, 0);
        }

        @Override
        public void setNextSpawnData(@Nullable Level level, BlockPos blockPos, SpawnData spawnData) {
            super.setNextSpawnData(level, blockPos, spawnData);
            if (level != null) {
                BlockState blockstate = level.getBlockState(blockPos);
                level.sendBlockUpdated(blockPos, blockstate, blockstate, 4);
            }

        }

        @Override
        public Either<BlockEntity, Entity> getOwner() {
            return Either.left(CamoSpawnerBlockEntity.this);
        }
    };

    public CamoSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(TolkienBlocks.CAMO_SPAWNER_BLOCK_ENTITY.get(), pos, blockState);
    }

    public void onRightClick(ItemStack stack, BlockState state, BlockPos pos, Player player, InteractionHand hand) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        TolkienSpawner spawner = (TolkienSpawner) blockEntity;
        CompoundTag entityTag = new CompoundTag();
        assert spawner != null;

        if (stack.getItem() instanceof SpawnEggItem spawnEgg) {
            EntityType<?> entityType = spawnEgg.getType(stack);
            spawner.setEntityId(entityType, level.getRandom());
            level.sendBlockUpdated(pos, state, state, 3);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            stack.shrink(1);
            entityTag.putString("entityID", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
            Optional<EntityType<?>> optional = EntityType.byString(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());

            if(!this.entityTags.contains(entityTag) && this.entityTags.size() <= 6) {
                TolkienMobsMain.LOGGER.warn(String.valueOf(this.entityTags.size()));
                this.entityTags.add(entityTag);
                this.markDirtyClient();
                this.setChanged();
                player.sendSystemMessage(Component.translatable("tolkienmobs.msg.added.entities").append(" " + optional.get().getDescription().getString()).withStyle(ChatFormatting.DARK_GREEN));
            } else {
                this.entityTags.removeFirst();
                this.entityTags.add(entityTag);
                this.markDirtyClient();
                this.setChanged();
                player.sendSystemMessage(Component.translatable("tolkienmobs.msg.nomore.entities").withStyle(ChatFormatting.DARK_RED));
            }
        }

        if (player.isCreative() && stack.isEmpty() && Screen.hasControlDown()) {
            this.entityTags.clear();
            this.markDirtyClient();
            this.setChanged();
        if (level.isClientSide) {
                player.sendSystemMessage(Component.translatable("tolkienmobs.msg.cleared.entities").withStyle(ChatFormatting.DARK_RED));
            }
        }

    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.tolkienmobs.block_camo_spawner");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CamoSpawnerContainer(pContainerId, pPlayerInventory, this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.remove("SpawnPotentials");
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.spawnerBase.load(this.level, this.worldPosition, tag);
        loadSpawnerSettings(tag);
        ListTag list = tag.getList("entity_list", 10);
        list.forEach(inbt -> entityTags.add((CompoundTag) inbt));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.spawnerBase.save(tag);
        saveSpawnerSettings(tag);
        ListTag list = new ListTag();
        list.addAll(entityTags);
        tag.put("entity_list", list);

        super.saveAdditional(tag, registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries) {
        super.onDataPacket(net, pkt, pRegistries);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CamoSpawnerBlockEntity blockEntity) {
        blockEntity.spawnerBase.clientTick(level, pos);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CamoSpawnerBlockEntity blockEntity) {
        blockEntity.spawnerBase.serverTick((ServerLevel) level, pos);
    }

    @Override
    public void serverTick(ServerLevel level, BlockPos pos) {
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        return this.spawnerBase.onEventTriggered(this.level, id) ? true : super.triggerEvent(id, type);
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Override
    public void setEntityId(EntityType<?> type, RandomSource random) {
        this.spawnerBase.setEntityId(type, this.level, random, this.worldPosition);
        this.setChanged();
    }

    public TolkienBaseSpawner getSpawnerBase() {
        return this.spawnerBase;
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    public void markDirtyClient() {
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 3);
        }
    }

    /**
     * Block Entity Stuff
     */
    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    @Override
    public SpawnerSettings getSpawnerSettings() {
        return spawnerSettings;
    }

    @Override
    public SpawnerDelays getSpawnerDelays() {
        return spawnerDelays;
    }

    @Override
    public SpawnerRanges getSpawnerRanges() {
        return spawnerRanges;
    }
}