package com.greatorator.tolkienmobs.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

public interface ICustomPacketHandler {

    interface IClientPacketHandler extends ICustomPacketHandler {

        void handlePacket(PacketCustom packet, Minecraft mc);
    }

    interface IServerPacketHandler extends ICustomPacketHandler {

        void handlePacket(PacketCustom packet, ServerPlayer sender);
    }

    interface IClientConfigurationPacketHandler extends ICustomPacketHandler {

        void handlePacket(PacketCustom packet, Minecraft mc);
    }

    @ApiStatus.Experimental
    interface IServerConfigurationPacketHandler extends ICustomPacketHandler {

        void handlePacket(PacketCustom packet, ServerPlayer sender, Consumer<ConfigurationTask.Type> onTaskCompleted);
    }
}
