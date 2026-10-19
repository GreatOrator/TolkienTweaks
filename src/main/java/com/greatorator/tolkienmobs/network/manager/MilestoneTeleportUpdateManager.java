package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.containers.MilestoneContainer;
import com.greatorator.tolkienmobs.handler.data.MCDataInput;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record MilestoneTeleportUpdateManager(
        boolean teleport
) implements CustomPacketPayload {
    public static final Type<MilestoneTeleportUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "milestone_teleport_packet"));

    @Override
    public Type<MilestoneTeleportUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, MilestoneTeleportUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, MilestoneTeleportUpdateManager::teleport,
                    MilestoneTeleportUpdateManager::new);

    public static void handle(final MilestoneTeleportUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            Level level = sender.level();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof MilestoneContainer milestoneContainer && milestoneContainer.tile instanceof MilestoneBlockEntity milestoneBE) {
                if (payload.teleport()) {
                    milestoneBE.receivePacketFromClient((MCDataInput) context, (ServerPlayer) sender, 3);
                }
            }
        });
    }
}
