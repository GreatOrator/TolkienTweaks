package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.network.packet.ICustomPacketHandler;
import com.greatorator.tolkienmobs.network.packet.PacketCustom;
import net.minecraft.server.level.ServerPlayer;

public class ServerPacketHandler implements ICustomPacketHandler.IServerPacketHandler{
    @Override
    public void handlePacket(PacketCustom packet, ServerPlayer sender) {

    }
}
