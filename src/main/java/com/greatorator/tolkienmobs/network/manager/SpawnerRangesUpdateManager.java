package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoSpawnerContainer;
import com.greatorator.tolkienmobs.util.block.SpawnerRanges;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record SpawnerRangesUpdateManager(
        int activationRange,
        int spawnRange,
        int spawnCount,
        int maxCluster,
        int clusterRange
) implements CustomPacketPayload {
    public static final Type<SpawnerRangesUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "spawner_range_packet"));

    @Override
    public Type<SpawnerRangesUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SpawnerRangesUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, SpawnerRangesUpdateManager::activationRange,
                    ByteBufCodecs.INT, SpawnerRangesUpdateManager::spawnRange,
                    ByteBufCodecs.INT, SpawnerRangesUpdateManager::spawnCount,
                    ByteBufCodecs.INT, SpawnerRangesUpdateManager::maxCluster,
                    ByteBufCodecs.INT, SpawnerRangesUpdateManager::clusterRange,
                    SpawnerRangesUpdateManager::new);

    public static void handle(final SpawnerRangesUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof CamoSpawnerContainer spawnerContainer && spawnerContainer.tileEntity instanceof CamoSpawnerBlockEntity spawnerBE) {
                spawnerBE.setSpawnerRanges(new SpawnerRanges(payload.activationRange(), payload.spawnRange(), payload.spawnCount(), payload.maxCluster(), payload.clusterRange()));
            }
        });
    }
}
