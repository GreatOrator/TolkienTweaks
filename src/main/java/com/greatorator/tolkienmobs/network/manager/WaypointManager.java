package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.faction.Waypoint;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Carries one direction to one player: the name of the ground their page names, where it is, and which
 * dimension it is in. Sent whenever the book is sent, so the needle changes with the page rather than
 * needing a packet of its own for every way a page can be taken or paid.
 * <p>
 * An empty mark is the ordinary case - a page with no ground, or no page at all - and it clears the HUD
 * rather than leaving the last destination pointing at a place the player is already finished with.
 */
public record WaypointManager(String label, BlockPos pos, String dimension, boolean present)
        implements CustomPacketPayload {
    public static final Type<WaypointManager> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", "waypoint"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WaypointManager> STREAM_CODEC =
            CustomPacketPayload.codec(WaypointManager::write, WaypointManager::new);

    public WaypointManager(RegistryFriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readBlockPos(), buf.readUtf(), buf.readBoolean());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(label);
        buf.writeBlockPos(pos);
        buf.writeUtf(dimension);
        buf.writeBoolean(present);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(WaypointManager payload, IPayloadContext context) {
        Waypoint.acceptClient(payload);
    }
}
