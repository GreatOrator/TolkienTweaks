package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.containers.TolkienContainer;
import com.greatorator.tolkienmobs.network.packet.ICustomPacketHandler;
import com.greatorator.tolkienmobs.network.packet.PacketCustom;
import net.minecraft.server.level.ServerPlayer;

public class ServerPacketHandler implements ICustomPacketHandler.IServerPacketHandler{
    @Override
    public void handlePacket(PacketCustom packet, ServerPlayer sender) {
        switch (packet.getType()) {
            case PacketHandler.S_CONTAINER_MESSAGE -> handleContainerMessage(packet, sender);
        }
    }

    private void handleContainerMessage(PacketCustom packet, ServerPlayer sender) {
        try {
            if (sender.containerMenu instanceof TolkienContainer) {
                ((TolkienContainer<?>) sender.containerMenu).handleContainerMessage(packet, sender);
            }
        } catch (Throwable e) {
            TolkienMobsMain.LOGGER.error("Something went wrong while attempting to read a packet sent from this client: {}", sender);
            e.printStackTrace();
        }
    }
}
