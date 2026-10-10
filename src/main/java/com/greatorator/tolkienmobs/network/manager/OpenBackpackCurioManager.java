package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.integration.CuriosBackpackHelper;
import com.greatorator.tolkienmobs.item.custom.BackpackItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Sent by the open-backpack key: opens the backpack worn in the player's Curios back slot. */
public record OpenBackpackCurioManager() implements CustomPacketPayload {
    public static final Type<OpenBackpackCurioManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "open_backpack_curio_packet"));
    public static final StreamCodec<ByteBuf, OpenBackpackCurioManager> STREAM_CODEC = StreamCodec.unit(new OpenBackpackCurioManager());

    @Override
    public Type<OpenBackpackCurioManager> type() {
        return TYPE;
    }

    public static void handle(final OpenBackpackCurioManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && ModList.get().isLoaded("curios")) {
                CuriosBackpackHelper.findBackpack(player).ifPresent(stack -> BackpackItem.open(player, stack, null));
            }
        });
    }
}
