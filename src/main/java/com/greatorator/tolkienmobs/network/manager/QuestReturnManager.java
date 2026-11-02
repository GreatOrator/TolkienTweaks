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
 * A player asking the book to walk them back to the body who gave them a page they have finished.
 * <p>
 * Only the slot in the player's own book crosses the wire, the same as setting a page aside: the server
 * reads that player's work at that position, and answers nothing at all for a slot that holds no story
 * page, a page that is not finished, or a giver the player is already standing beside. A client cannot
 * name somebody else's work or a place of its own choosing, so the only journey this can ask for is back
 * to a page's own origin.
 */
public record QuestReturnManager(int slot) implements CustomPacketPayload {
    public static final Type<QuestReturnManager> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(MODID, "quest_return"));
    public static final StreamCodec<RegistryFriendlyByteBuf, QuestReturnManager> STREAM_CODEC =
            CustomPacketPayload.codec(QuestReturnManager::write, QuestReturnManager::new);

    public QuestReturnManager(RegistryFriendlyByteBuf buf) {
        this(buf.readVarInt());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(slot);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(QuestReturnManager payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                QuestLedger.returnToGiver(player, payload.slot());
            }
        });
    }
}
