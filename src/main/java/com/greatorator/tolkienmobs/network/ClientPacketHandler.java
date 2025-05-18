package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.block.custom.entity.TolkienBlockEntity;
import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.greatorator.tolkienmobs.network.manager.IDataManagerProvider;
import com.greatorator.tolkienmobs.network.packet.ICustomPacketHandler;
import com.greatorator.tolkienmobs.network.packet.PacketCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class ClientPacketHandler implements ICustomPacketHandler.IClientPacketHandler{
    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc) {
        switch (packet.getType()) {
            case PacketHandler.C_SEND_MILESTONES -> {
                handleMilestoneSync(packet);
            }
            case PacketHandler.C_TILE_DATA_MANAGER -> {
                BlockPos pos = packet.readPos();
                if (mc.level.getBlockEntity(pos) instanceof IDataManagerProvider tile) {
                    tile.getDataManager().receiveSyncData(packet);
                }
            }
            case PacketHandler.C_TILE_MESSAGE -> {
                BlockPos pos = packet.readPos();
                if (mc.level.getBlockEntity(pos) instanceof TolkienBlockEntity tile) {
                    int id = packet.readByte() & 0xFF;
                    tile.receivePacketFromServer(packet, id);
                }
            }
            case PacketHandler.C_PLAY_SOUND -> handlePlaySound(packet, mc);
        }
    }

    private static void handleMilestoneSync(PacketCustom packet) {
        MilestoneHandler.deserialize(packet);
    }

    private static void handlePlaySound(PacketCustom packet, Minecraft mc) {
        if (mc.level == null) return;
        BlockPos pos = packet.readPos();
        SoundEvent sound = packet.readRegistryId();
        SoundSource category = SoundSource.values()[packet.readVarInt()];
        float volume = packet.readFloat();
        float pitch = packet.readFloat();
        boolean distanceDelay = packet.readBoolean();
        mc.level.playLocalSound(pos, sound, category, volume, pitch, distanceDelay);
    }
}
