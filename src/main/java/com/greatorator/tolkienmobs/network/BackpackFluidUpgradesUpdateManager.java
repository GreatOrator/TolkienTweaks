package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.util.BackpackFluidUpgrades;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record BackpackFluidUpgradesUpdateManager(
        boolean fluid_tank,
        boolean fluid_tank_2,
        boolean fluid_tank_3,
        boolean fluid_tank_4,
        boolean fluid_tank_5
) implements CustomPacketPayload {
    public static final Type<BackpackFluidUpgradesUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "backpack_upgrade_2_packet"));

    @Override
    public Type<BackpackFluidUpgradesUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, BackpackFluidUpgradesUpdateManager> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, BackpackFluidUpgradesUpdateManager::fluid_tank,
                    ByteBufCodecs.BOOL, BackpackFluidUpgradesUpdateManager::fluid_tank_2,
                    ByteBufCodecs.BOOL, BackpackFluidUpgradesUpdateManager::fluid_tank_3,
                    ByteBufCodecs.BOOL, BackpackFluidUpgradesUpdateManager::fluid_tank_4,
                    ByteBufCodecs.BOOL, BackpackFluidUpgradesUpdateManager::fluid_tank_5,
                    BackpackFluidUpgradesUpdateManager::new);

    public static void handle(final BackpackFluidUpgradesUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof BackpackBlockContainer backpackContainer && backpackContainer.tileEntity instanceof BackpackBlockEntity backpackBE) {
                backpackBE.setBackpackFluidUpgrades(new BackpackFluidUpgrades(payload.fluid_tank(), payload.fluid_tank_2(), payload.fluid_tank_3(), payload.fluid_tank_4(), payload.fluid_tank_5()));
            }
        });
    }
}
