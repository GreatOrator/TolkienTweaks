package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.faction.FactionStandings;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Carries a player's three standings from the server to that player alone. Sent when they log in and
 * after every change, so the ledger is always drawing the numbers the server holds.
 */
public record FactionStandingsSyncManager(int west, int east, int harad) implements CustomPacketPayload {
    public static final Type<FactionStandingsSyncManager> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", "faction_standings_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FactionStandingsSyncManager> STREAM_CODEC =
            CustomPacketPayload.codec(FactionStandingsSyncManager::write, FactionStandingsSyncManager::new);

    public FactionStandingsSyncManager(RegistryFriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(west);
        buf.writeVarInt(east);
        buf.writeVarInt(harad);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FactionStandingsSyncManager payload, IPayloadContext context) {
        // The ledger is drawn from this cache, so the client only ever stores its own three totals.
        FactionStandings.acceptClient(new int[]{payload.west(), payload.east(), payload.harad()});
    }
}
