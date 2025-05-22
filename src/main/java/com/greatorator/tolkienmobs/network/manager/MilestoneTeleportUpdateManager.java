package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.BackpackBlock;
import com.greatorator.tolkienmobs.block.custom.SleepingBagBlock;
import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.containers.MilestoneContainer;
import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.greatorator.tolkienmobs.handler.data.MCDataInput;
import com.greatorator.tolkienmobs.handler.vec.Vector3;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.network.PacketHandler;
import com.greatorator.tolkienmobs.util.block.TeleportUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
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

            if (container instanceof MilestoneContainer milestoneContainer && milestoneContainer.tileEntity instanceof MilestoneBlockEntity milestoneBE) {
                TolkienMobsMain.LOGGER.warn("Milestone Teleporting: {}", payload.teleport());
                if (payload.teleport()) {
                    milestoneBE.receivePacketFromClient((MCDataInput) context, (ServerPlayer) sender, 3);
                }
            }
        });
    }
}
