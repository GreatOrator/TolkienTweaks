package com.greatorator.tolkienmobs.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class PacketHandler {
    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID);

        registrar.playToServer(KeyCodePayload.TYPE, KeyCodePayload.STREAM_CODEC, PacketUpdateManager.get()::handle);
    }
}