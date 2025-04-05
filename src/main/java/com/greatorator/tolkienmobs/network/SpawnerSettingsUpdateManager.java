package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoSpawnerContainer;
import com.greatorator.tolkienmobs.util.SpawnerSettings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record SpawnerSettingsUpdateManager(
        boolean requirePlayer,
        boolean ignoreSpawnReq,
        boolean spawnerParticles
) implements CustomPacketPayload {
    public static final Type<SpawnerSettingsUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "spawner_setting_packet"));

    @Override
    public Type<SpawnerSettingsUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SpawnerSettingsUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, SpawnerSettingsUpdateManager::requirePlayer,
                    ByteBufCodecs.BOOL, SpawnerSettingsUpdateManager::ignoreSpawnReq,
                    ByteBufCodecs.BOOL, SpawnerSettingsUpdateManager::spawnerParticles,
                    SpawnerSettingsUpdateManager::new);

    public static void handle(final SpawnerSettingsUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof CamoSpawnerContainer spawnerContainer && spawnerContainer.tileEntity instanceof CamoSpawnerBlockEntity spawnerBE) {
                spawnerBE.setSpawnerSettings(new SpawnerSettings(payload.requirePlayer(), payload.ignoreSpawnReq(), payload.spawnerParticles()));
            }
        });
    }
}
