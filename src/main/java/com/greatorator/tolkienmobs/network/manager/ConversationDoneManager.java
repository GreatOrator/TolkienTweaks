package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.faction.QuestLedger;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Tells the server that a page of talk was read to the end and put away.
 * <p>
 * The words never leave the client - they are the page's own talk keys, read in the reader's language -
 * so the only thing that can be sent is which page it was. That is enough: a dialogue page in the book
 * names the leaf it asks to be read, and the ledger decides what hearing it out is worth.
 * <p>
 * It travels only when a reply was actually given. A leaf shut with Escape before it was answered is a
 * page put down unread, and nothing is finished by that.
 */
public record ConversationDoneManager(String page) implements CustomPacketPayload {
    public static final Type<ConversationDoneManager> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", "conversation_done"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConversationDoneManager> STREAM_CODEC =
            CustomPacketPayload.codec(ConversationDoneManager::write, ConversationDoneManager::new);

    public ConversationDoneManager(RegistryFriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(page);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ConversationDoneManager payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                QuestLedger.finishTalk(player, payload.page());
            }
        });
    }
}
