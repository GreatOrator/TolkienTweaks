package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.util.block.BackpackSettings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record BackpackSettingsUpdateManager(
        boolean sleepingBag,
        boolean campfire,
        boolean upgrade
) implements CustomPacketPayload {
    public static final Type<BackpackSettingsUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "backpack_setting_packet"));

    @Override
    public Type<BackpackSettingsUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, BackpackSettingsUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, BackpackSettingsUpdateManager::sleepingBag,
                    ByteBufCodecs.BOOL, BackpackSettingsUpdateManager::campfire,
                    ByteBufCodecs.BOOL, BackpackSettingsUpdateManager::upgrade,
                    BackpackSettingsUpdateManager::new);

    public static void handle(final BackpackSettingsUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof BackpackBlockContainer backpackContainer && backpackContainer.tileEntity instanceof BackpackBlockEntity backpackBE) {
                backpackBE.setBackpackSettings(new BackpackSettings(payload.sleepingBag(), payload.campfire(), payload.upgrade()));
            }
        });
    }
}
