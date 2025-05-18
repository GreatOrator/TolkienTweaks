package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.containers.TolkienContainer;
import com.greatorator.tolkienmobs.handler.data.MCDataInput;
import com.greatorator.tolkienmobs.handler.data.MCDataOutput;
import com.greatorator.tolkienmobs.network.PacketHandler;
import com.greatorator.tolkienmobs.network.packet.PacketCustom;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TolkienBlockEntity extends BlockEntity {
    private Map<Integer, BiConsumer<MCDataInput, ServerPlayer>> serverPacketHandlers = new HashMap<>();
    private Map<Integer, Consumer<MCDataInput>> clientPacketHandlers = new HashMap<>();

    public TolkienBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void receivePacketFromClient(MCDataInput data, ServerPlayer client, int id) {
        if (serverPacketHandlers.containsKey(id)) {
            serverPacketHandlers.get(id).accept(data, client);
        }
    }

    public void receivePacketFromServer(MCDataInput data, int id) {
        if (clientPacketHandlers.containsKey(id)) {
            clientPacketHandlers.get(id).accept(data);
        }
    }

    public PacketCustom createClientBoundPacket(int id) {
        PacketCustom packet = new PacketCustom(PacketHandler.NET_CHANNEL, PacketHandler.C_TILE_MESSAGE, getLevel().registryAccess());
        packet.writePos(this.worldPosition);
        packet.writeByte((byte)id);
        return packet;
    }

    public PacketCustom createServerBoundPacket(int id) {
        Player player = TolkienMobsMain.proxy.getClientPlayer();
        if (player != null) {
            AbstractContainerMenu container = player.containerMenu;
            if (container instanceof TolkienContainer && ((TolkienContainer)container).tile == this) {
                PacketCustom packet = ((TolkienContainer)container).createServerBoundPacket(1);
                packet.writeByte((byte)id);
                return packet;
            }
        }

        return new PacketCustom(PacketHandler.NET_CHANNEL, PacketHandler.S_DUMMY_PACKET, getLevel().registryAccess());
    }

    public PacketCustom sendPacketToClient(Consumer<MCDataOutput> writer, int id) {
        PacketCustom packet = this.createClientBoundPacket(id);
        writer.accept(packet);
        return packet;
    }

    public void sendPacketToClient(ServerPlayer player, Consumer<MCDataOutput> writer, int id) {
        this.sendPacketToClient(writer, id).sendToPlayer(player);
    }

    public void sendPacketToServer(Consumer<MCDataOutput> writer, int id) {
        PacketCustom packet = this.createServerBoundPacket(id);
        writer.accept(packet);
        packet.sendToServer();
    }

    public void markDirtyClient() {
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 3);
        }
    }
}