package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.greatorator.tolkienmobs.network.internal.ClientConfigurationPacketHandler;
import com.greatorator.tolkienmobs.network.manager.*;
import com.greatorator.tolkienmobs.network.packet.PacketCustom;
import com.greatorator.tolkienmobs.network.packet.PacketCustomChannel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.NetworkChannel;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public final class PacketHandler {
    public static final ResourceLocation NET_CHANNEL = ResourceLocation.fromNamespaceAndPath(MODID, "network");
    public static final int C_SEND_MILESTONES = 1;
    public static final int L_CONFIG_SYNC = 1;
    public static final PacketCustomChannel CHANNEL = new PacketCustomChannel(NET_CHANNEL)
            .optional()
            .versioned("tolkienmobs")
            .clientConfiguration(() -> ClientConfigurationPacketHandler::new)
            .client(() -> ClientPacketHandler::new)
            .server(() -> ServerPacketHandler::new);

    private PacketHandler() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
            var registrar = event.registrar(MODID).versioned("1");
            registerClientToServer(registrar);
        });
        CHANNEL.init(modEventBus);
    }

    private static void registerClientToServer(PayloadRegistrar registrar) {
        registrar.playToServer(KeyCodeUpdateManager.TYPE,
                KeyCodeUpdateManager.STREAM_CODEC, KeyCodeUpdateManager::handle);
        registrar.playToServer(LockedChestUpdateManager.TYPE,
                LockedChestUpdateManager.STREAM_CODEC, LockedChestUpdateManager::handle);
        registrar.playToServer(LockedTreasureChestUpdateManager.TYPE,
                LockedTreasureChestUpdateManager.STREAM_CODEC, LockedTreasureChestUpdateManager::handle);
        registrar.playToServer(LockedDoubleChestUpdateManager.TYPE,
                LockedDoubleChestUpdateManager.STREAM_CODEC, LockedDoubleChestUpdateManager::handle);
        registrar.playToServer(LockedDoubleTreasureChestUpdateManager.TYPE,
                LockedDoubleTreasureChestUpdateManager.STREAM_CODEC, LockedDoubleTreasureChestUpdateManager::handle);
        registrar.playToServer(KeyStoneDataUpdateManager.TYPE,
                KeyStoneDataUpdateManager.STREAM_CODEC, KeyStoneDataUpdateManager::handle);
        registrar.playToServer(KeyStoneSettingsUpdateManager.TYPE,
                KeyStoneSettingsUpdateManager.STREAM_CODEC, KeyStoneSettingsUpdateManager::handle);
        registrar.playToServer(KeyStoneRedstoneUpdateManager.TYPE,
                KeyStoneRedstoneUpdateManager.STREAM_CODEC, KeyStoneRedstoneUpdateManager::handle);
        registrar.playToServer(SpawnerSettingsUpdateManager.TYPE,
                SpawnerSettingsUpdateManager.STREAM_CODEC, SpawnerSettingsUpdateManager::handle);
        registrar.playToServer(SpawnerDelaysUpdateManager.TYPE,
                SpawnerDelaysUpdateManager.STREAM_CODEC, SpawnerDelaysUpdateManager::handle);
        registrar.playToServer(SpawnerRangesUpdateManager.TYPE,
                SpawnerRangesUpdateManager.STREAM_CODEC, SpawnerRangesUpdateManager::handle);

        registrar.playToServer(KeyStoneDelayUpdateManager.TYPE,
                KeyStoneDelayUpdateManager.STREAM_CODEC, KeyStoneDelayUpdateManager::handle);
        registrar.playToServer(BackpackSettingsUpdateManager.TYPE,
                BackpackSettingsUpdateManager.STREAM_CODEC, BackpackSettingsUpdateManager::handle);
        registrar.playToServer(BackpackPlacementUpdateManager.TYPE,
                BackpackPlacementUpdateManager.STREAM_CODEC, BackpackPlacementUpdateManager::handle);
        registrar.playToServer(BackpackUpgradesUpdateManager.TYPE,
                BackpackUpgradesUpdateManager.STREAM_CODEC, BackpackUpgradesUpdateManager::handle);
        registrar.playToServer(BackpackFluidUpgradesUpdateManager.TYPE,
                BackpackFluidUpgradesUpdateManager.STREAM_CODEC, BackpackFluidUpgradesUpdateManager::handle);
        registrar.playToServer(MilestoneSettingsUpdateManager.TYPE,
                MilestoneSettingsUpdateManager.STREAM_CODEC, MilestoneSettingsUpdateManager::handle);
    }

    public static void sendMilestonesToClients(MilestoneHandler saveData, ServerPlayer player) {
        PacketCustom packet = new PacketCustom(NET_CHANNEL, C_SEND_MILESTONES, player.level().registryAccess());
        saveData.serialize(packet);
        if (player != null) {
            packet.sendToPlayer(player);
        } else {
            packet.toClientPacket();
        }
    }

    private static void registerServerToClient(IEventBus modBus) {
    }
}