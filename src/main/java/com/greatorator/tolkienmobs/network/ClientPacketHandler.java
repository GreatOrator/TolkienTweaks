package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.greatorator.tolkienmobs.network.packet.ICustomPacketHandler;
import com.greatorator.tolkienmobs.network.packet.PacketCustom;
import net.minecraft.client.Minecraft;

public class ClientPacketHandler implements ICustomPacketHandler.IClientPacketHandler{
    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc) {
        switch (packet.getType()) {
            case PacketHandler.C_SEND_MILESTONES: {
                handleMilestoneSync(packet);
                break;
            }
        }
    }

    private static void handleMilestoneSync(PacketCustom packet) {
        MilestoneHandler.deserialize(packet);
    }
}
