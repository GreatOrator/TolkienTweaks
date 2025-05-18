package com.greatorator.tolkienmobs.network.internal;

import com.greatorator.tolkienmobs.network.config.ConfigSyncManager;
import com.greatorator.tolkienmobs.network.packet.ICustomPacketHandler;
import com.greatorator.tolkienmobs.network.packet.PacketCustom;
import net.minecraft.client.Minecraft;

import static com.greatorator.tolkienmobs.network.PacketHandler.C_CONFIG_SYNC;

public class ClientConfigurationPacketHandler implements ICustomPacketHandler.IClientConfigurationPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc) {
        switch (packet.getType()) {
            case C_CONFIG_SYNC -> ConfigSyncManager.readSyncPacket(packet);
        }
    }
}
