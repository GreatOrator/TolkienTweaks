package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoKeyStoneContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record KeyStonePulseUpdateManager(
        int pulseLength
) implements CustomPacketPayload {
    public static final Type<KeyStonePulseUpdateManager> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MODID, "key_stone_pulse_packet"));

    @Override
    public Type<KeyStonePulseUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, KeyStonePulseUpdateManager> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, KeyStonePulseUpdateManager::pulseLength,
            KeyStonePulseUpdateManager::new
    );

    public static void handle(final KeyStonePulseUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof CamoKeyStoneContainer keyStoneContainer && keyStoneContainer.tileEntity instanceof CamoKeyStoneBlockEntity keyStoneBE) {
                keyStoneBE.setPulseLength(Math.min(Math.max(payload.pulseLength(), TolkienMobsConfig.MINIMUM_TICK_SPEED.get()), 1200));
            }
        });
    }
}
