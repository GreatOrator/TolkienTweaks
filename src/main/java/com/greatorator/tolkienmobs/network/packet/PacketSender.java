package com.greatorator.tolkienmobs.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;
import static net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer;

public class PacketSender {

    // region Server -> Client
    public static Packet<?> toClientPacket(CustomPacketPayload payload) {
        return new ClientboundCustomPayloadPacket(payload);
    }

    public static void sendToPlayer(CustomPacketPayload payload, @Nullable ServerPlayer player) {
        sendToPlayer(toClientPacket(payload), player);
    }

    public static void sendToPlayer(Packet<?> packet, @Nullable ServerPlayer player) {
        if (player == null) {
            sendToAllPlayers(packet);
        } else {
            player.connection.send(packet);
        }
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        sendToAllPlayers(toClientPacket(payload));
    }

    public static void sendToAllPlayers(Packet<?> packet) {
        getCurrentServer().getPlayerList().broadcastAll(packet);
    }

    public static void sendToAllAround(CustomPacketPayload payload, BlockPos pos, double range, ResourceKey<Level> dim) {
        sendToAllAround(toClientPacket(payload), pos, range, dim);
    }

    public static void sendToAllAround(Packet<?> packet, BlockPos pos, double range, ResourceKey<Level> dim) {
        sendToAllAround(packet, pos.getX(), pos.getY(), pos.getZ(), range, dim);
    }

    public static void sendToAllAround(CustomPacketPayload payload, double x, double y, double z, double range, ResourceKey<Level> dim) {
        sendToAllAround(toClientPacket(payload), x, y, z, range, dim);
    }

    public static void sendToAllAround(Packet<?> packet, double x, double y, double z, double range, ResourceKey<Level> dim) {
        getCurrentServer().getPlayerList().broadcast(null, x, y, z, range, dim, packet);
    }

    public static void sendToDimension(CustomPacketPayload payload, ResourceKey<Level> dim) {
        sendToDimension(toClientPacket(payload), dim);
    }

    public static void sendToDimension(Packet<?> packet, ResourceKey<Level> dim) {
        getCurrentServer().getPlayerList().broadcastAll(packet, dim);
    }

    public static void sendToChunk(CustomPacketPayload payload, BlockEntity tile) {
        sendToChunk(toClientPacket(payload), tile);
    }

    public static void sendToChunk(Packet<?> packet, BlockEntity tile) {
        sendToChunk(packet, (ServerLevel) requireNonNull(tile.getLevel()), tile.getBlockPos());
    }

    public static void sendToChunk(CustomPacketPayload payload, ServerLevel level, BlockPos pos) {
        sendToChunk(toClientPacket(payload), level, pos);
    }

    public static void sendToChunk(Packet<?> packet, ServerLevel level, BlockPos pos) {
        sendToChunk(packet, level, pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static void sendToChunk(CustomPacketPayload payload, ServerLevel level, int chunkX, int chunkZ) {
        sendToChunk(toClientPacket(payload), level, chunkX, chunkZ);
    }

    public static void sendToChunk(Packet<?> packet, ServerLevel level, int chunkX, int chunkZ) {
        sendToChunk(packet, level, new ChunkPos(chunkX, chunkZ));
    }

    public static void sendToChunk(CustomPacketPayload payload, ServerLevel level, ChunkPos pos) {
        sendToChunk(toClientPacket(payload), level, pos);
    }

    public static void sendToChunk(Packet<?> packet, ServerLevel level, ChunkPos pos) {
        level.getChunkSource().chunkMap.getPlayers(pos, false).forEach(e -> sendToPlayer(packet, e));
    }

    public static void sendToOps(CustomPacketPayload payload) {
        sendToOps(toClientPacket(payload));
    }

    public static void sendToOps(Packet<?> packet) {
        PlayerList playerList = getCurrentServer().getPlayerList();
        ServerOpList opList = playerList.getOps();
        for (ServerPlayer player : playerList.getPlayers()) {
            if (opList.get(player.getGameProfile()) != null) {
                sendToPlayer(packet, player);
            }
        }
    }

    // region Client -> Server
    public static Packet<?> toServerPacket(CustomPacketPayload payload) {
        return new ServerboundCustomPayloadPacket(payload);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        sendToServer(toServerPacket(payload));
    }

    public static void sendToServer(Packet<?> packet) {
        requireNonNull(Minecraft.getInstance().getConnection()).send(packet);
    }
}