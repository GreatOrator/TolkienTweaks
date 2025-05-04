package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoSpawnerContainer;
import com.greatorator.tolkienmobs.util.block.SpawnerDelays;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record SpawnerDelaysUpdateManager(
        int minSpawnDelay,
        int maxSpawnDelay,
        int spawnDelay
) implements CustomPacketPayload {
    public static final Type<SpawnerDelaysUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "spawner_delay_packet"));

    @Override
    public Type<SpawnerDelaysUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SpawnerDelaysUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, SpawnerDelaysUpdateManager::minSpawnDelay,
                    ByteBufCodecs.INT, SpawnerDelaysUpdateManager::maxSpawnDelay,
                    ByteBufCodecs.INT, SpawnerDelaysUpdateManager::spawnDelay,
                    SpawnerDelaysUpdateManager::new);

    public static void handle(final SpawnerDelaysUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof CamoSpawnerContainer spawnerContainer && spawnerContainer.tileEntity instanceof CamoSpawnerBlockEntity spawnerBE) {
                spawnerBE.setSpawnerDelays(new SpawnerDelays(payload.minSpawnDelay(), payload.maxSpawnDelay(), payload.spawnDelay()));
            }
        });
    }
}
