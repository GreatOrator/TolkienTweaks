package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.containers.MilestoneContainer;
import com.greatorator.tolkienmobs.util.block.MilestoneSettings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record MilestoneSettingsUpdateManager(
        int distance,
        int dimension,
        String name,
        String UUID

) implements CustomPacketPayload {
    public static final Type<MilestoneSettingsUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "milestone_setting_packet"));

    @Override
    public Type<MilestoneSettingsUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, MilestoneSettingsUpdateManager> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MilestoneSettingsUpdateManager::distance,
            ByteBufCodecs.INT, MilestoneSettingsUpdateManager::dimension,
            ByteBufCodecs.STRING_UTF8, MilestoneSettingsUpdateManager::name,
            ByteBufCodecs.STRING_UTF8, MilestoneSettingsUpdateManager::UUID,
            MilestoneSettingsUpdateManager::new
    );

    public static void handle(final MilestoneSettingsUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof MilestoneContainer milestoneContainer && milestoneContainer.tileEntity instanceof MilestoneBlockEntity milestoneBE) {
                milestoneBE.setMilestoneSettings(new MilestoneSettings(payload.distance(), payload.dimension(),payload.name(), payload.UUID()));
            }
        });
    }
}
