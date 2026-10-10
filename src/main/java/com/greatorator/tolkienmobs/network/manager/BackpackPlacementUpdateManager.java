package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record BackpackPlacementUpdateManager(
        boolean sleepingBag,
        boolean campfire
) implements CustomPacketPayload {
    public static final Type<BackpackPlacementUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "backpack_placement_packet"));

    @Override
    public Type<BackpackPlacementUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, BackpackPlacementUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, BackpackPlacementUpdateManager::sleepingBag,
                    ByteBufCodecs.BOOL, BackpackPlacementUpdateManager::campfire,
                    BackpackPlacementUpdateManager::new);

    /** Sent when the sleeping bag ({@code sleepingBag = true}) or campfire ({@code campfire = true}) button is pressed. */
    public static void handle(final BackpackPlacementUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof BackpackBlockContainer backpackContainer && backpackContainer.tileEntity instanceof BackpackBlockEntity backpackBE) {
                if (payload.sleepingBag() != payload.campfire()) {
                    backpackBE.toggleDeployable(payload.sleepingBag(), sender);
                }
            }
        });
    }
}
