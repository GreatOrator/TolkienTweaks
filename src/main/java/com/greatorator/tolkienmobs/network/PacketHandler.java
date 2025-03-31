package com.greatorator.tolkienmobs.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public final class PacketHandler {
    private PacketHandler() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
            var registrar = event.registrar(MODID).versioned("1");
            registerClientToServer(registrar);
            registerServerToClient(registrar);
        });
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
        registrar.playToServer(BackpackUpgradesUpdateManager.TYPE,
                BackpackUpgradesUpdateManager.STREAM_CODEC, BackpackUpgradesUpdateManager::handle);
        registrar.playToServer(BackpackFluidUpgradesUpdateManager.TYPE,
                BackpackFluidUpgradesUpdateManager.STREAM_CODEC, BackpackFluidUpgradesUpdateManager::handle);
    }

    private static void registerServerToClient(PayloadRegistrar registrar) {

    }
}