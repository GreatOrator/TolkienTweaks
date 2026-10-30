package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.faction.QuestLedger;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * A player setting a quest aside, sent from the book.
 * <p>
 * Only the slot in the player's own list crosses the wire. The server reads that player's book and
 * throws the quest at that position away, so a client cannot name someone else's work, invent terms to
 * be paid for, or abandon more than it is holding: a slot that no longer holds a quest does nothing.
 */
public record QuestAbandonManager(int slot) implements CustomPacketPayload {
    public static final Type<QuestAbandonManager> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(MODID, "quest_abandon"));
    public static final StreamCodec<RegistryFriendlyByteBuf, QuestAbandonManager> STREAM_CODEC =
            CustomPacketPayload.codec(QuestAbandonManager::write, QuestAbandonManager::new);

    public QuestAbandonManager(RegistryFriendlyByteBuf buf) {
        this(buf.readVarInt());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(slot);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(QuestAbandonManager payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                QuestLedger.abandon(player, payload.slot());
            }
        });
    }
}
