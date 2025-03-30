package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.util.BackpackUpgrades;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record BackpackUpgradesUpdateManager(
        boolean crafting,
        boolean sleepingBag,
        boolean campfire,
        boolean size_upgrade,
        boolean size_upgrade_2
) implements CustomPacketPayload {
    public static final Type<BackpackUpgradesUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "backpack_upgrade_packet"));

    @Override
    public Type<BackpackUpgradesUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, BackpackUpgradesUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, BackpackUpgradesUpdateManager::crafting,
                    ByteBufCodecs.BOOL, BackpackUpgradesUpdateManager::sleepingBag,
                    ByteBufCodecs.BOOL, BackpackUpgradesUpdateManager::campfire,
                    ByteBufCodecs.BOOL, BackpackUpgradesUpdateManager::size_upgrade,
                    ByteBufCodecs.BOOL, BackpackUpgradesUpdateManager::size_upgrade_2,
                    BackpackUpgradesUpdateManager::new);

    public static void handle(final BackpackUpgradesUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof BackpackBlockContainer backpackContainer && backpackContainer.tileEntity instanceof BackpackBlockEntity backpackBE) {
                backpackBE.setBackpackUpgrades(new BackpackUpgrades(payload.crafting(), payload.sleepingBag(), payload.campfire(), payload.size_upgrade(), payload.size_upgrade_2()));
            }
        });
    }
}
