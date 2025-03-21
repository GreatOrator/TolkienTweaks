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
    }

    private static void registerServerToClient(PayloadRegistrar registrar) {

    }
}