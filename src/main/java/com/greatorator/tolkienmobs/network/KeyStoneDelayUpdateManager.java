package com.greatorator.tolkienmobs.network;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.block.custom.entity.CamoKeyStoneBlockEntity;
import com.greatorator.tolkienmobs.containers.CamoKeyStoneContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public record KeyStoneDelayUpdateManager(
        int tickDelay
) implements CustomPacketPayload {
    public static final Type<KeyStoneDelayUpdateManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "key_stone_delay_packet"));

    @Override
    public Type<KeyStoneDelayUpdateManager> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, KeyStoneDelayUpdateManager> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, KeyStoneDelayUpdateManager::tickDelay,
            KeyStoneDelayUpdateManager::new
    );

    public static void handle(final KeyStoneDelayUpdateManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player sender = context.player();
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof CamoKeyStoneContainer keyStoneContainer && keyStoneContainer.tileEntity instanceof CamoKeyStoneBlockEntity keyStoneBE) {
                keyStoneBE.setTickDelay(Math.min(Math.max(payload.tickDelay(), TolkienMobsConfig.MINIMUM_TICK_SPEED.get()), 1200));
            }
        });
    }
}
