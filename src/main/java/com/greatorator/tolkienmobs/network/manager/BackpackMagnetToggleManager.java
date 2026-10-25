package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.util.block.BackpackMagnet;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * Flips the magnet of the player's backpack: from the toggle in the backpack's own screen, or from the
 * magnet key anywhere else. The server owns the setting and sends the new one back, so a screen that is
 * open follows whether the key or the button was used.
 */
public record BackpackMagnetToggleManager() implements CustomPacketPayload {
    public static final Type<BackpackMagnetToggleManager> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MODID, "backpack_magnet_toggle_packet"));
    public static final StreamCodec<ByteBuf, BackpackMagnetToggleManager> STREAM_CODEC = StreamCodec.unit(new BackpackMagnetToggleManager());

    @Override
    public Type<BackpackMagnetToggleManager> type() {
        return TYPE;
    }

    public static void handle(final BackpackMagnetToggleManager payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            // A backpack whose screen is open is the one being looked at, so that is the one to flip.
            AbstractContainerMenu menu = player.containerMenu;
            if (menu instanceof BackpackBlockContainer backpackContainer) {
                BackpackBlockEntity open = backpackContainer.tileEntity;
                open.getBackpackSettings().magnet = !open.getBackpackSettings().magnet;
                open.markDirtyClient();
                return;
            }

            // Otherwise the worn or carried backpack that has a magnet fitted - the same one the scan
            // fills, so the key and the button always flip the bag that is actually pulling.
            BackpackBlockEntity backpack = BackpackMagnet.findMagnetBackpack(player);
            if (backpack == null) {
                return;
            }
            backpack.getBackpackSettings().magnet = !backpack.getBackpackSettings().magnet;
            backpack.saveToStack();
        });
    }
}
