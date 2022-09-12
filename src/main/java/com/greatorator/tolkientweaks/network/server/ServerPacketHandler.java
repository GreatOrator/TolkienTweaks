package com.greatorator.tolkientweaks.network.server;

import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.IServerPlayNetHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPacketHandler implements ICustomPacketHandler.IServerPacketHandler {
    public static final Logger LOGGER = LogManager.getLogger(ServerPacketHandler.class);


    @Override
    public void handlePacket(PacketCustom packet, ServerPlayerEntity sender, IServerPlayNetHandler handler) {
        switch (packet.getType()) {
        }
    }
}