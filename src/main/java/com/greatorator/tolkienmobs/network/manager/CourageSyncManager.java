package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.faction.CourageLedger;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Carries a player's courage from the server to that player alone. Sent when they log in and after
 * every deed, so the ledger's row and the HUD both draw the number the server holds.
 */
public record CourageSyncManager(int deeds) implements CustomPacketPayload {
    public static final Type<CourageSyncManager> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", "courage_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CourageSyncManager> STREAM_CODEC =
            CustomPacketPayload.codec(CourageSyncManager::write, CourageSyncManager::new);

    public CourageSyncManager(RegistryFriendlyByteBuf buf) {
        this(buf.readVarInt());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(deeds);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CourageSyncManager payload, IPayloadContext context) {
        // The row and the HUD are drawn from this cache, so the client stores only its own total.
        CourageLedger.acceptClient(payload.deeds());
    }
}
