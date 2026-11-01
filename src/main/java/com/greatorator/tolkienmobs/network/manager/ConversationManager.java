package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.client.ConversationScreen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Asks the client to open a page of the campaign and let it speak.
 * <p>
 * The words are not on the wire: they are the page's own talk keys, which the client reads in its own
 * language, so a page that is translated is translated wherever it is read. What travels is the speaker -
 * the name the body in front of the player is actually wearing, so the page cannot contradict its own
 * nameplate - and the key prefix the page's lines hang off.
 * <p>
 * It is sent when a herald hands a page over, and again if the player asks him about one already carried,
 * because a page is worth re-reading and the book only ever says what it wants.
 */
/**
 * How the leaf was opened, which is the difference between a page being handed over and a page
 * being read again.
 */
public record ConversationManager(String speaker, String page, Mode mode) implements CustomPacketPayload {
    /**
     * The three ways a leaf can open. The order is the wire format, and an unknown value is read as
     * {@link #OPEN}, so a client dropped in from a newer build still draws a page rather than a
     * blank one.
     */
    public enum Mode {
        /** A page just handed over: the opening, and the two answers a player can give it. */
        OPEN,
        /** A page already taken and already answered, opened again only to be read. */
        ANSWERED,
        /** The herald will not hand his country's work over, and says so on the same leaf. */
        REFUSED
    }
    public static final Type<ConversationManager> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", "conversation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConversationManager> STREAM_CODEC =
            CustomPacketPayload.codec(ConversationManager::write, ConversationManager::new);

    public ConversationManager(RegistryFriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readUtf(), modeOf(buf.readByte()));
    }

    /** A mode off the wire, clamped: a value this build has no name for is read as a fresh page. */
    private static Mode modeOf(byte ordinal) {
        Mode[] modes = Mode.values();
        return ordinal >= 0 && ordinal < modes.length ? modes[ordinal] : Mode.OPEN;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(speaker);
        buf.writeUtf(page);
        buf.writeByte(mode.ordinal());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ConversationManager payload, IPayloadContext context) {
        // A dedicated server has no client to open anything on, and the screen is a client class: the
        // guard is what keeps a common handler from reaching for one that was never loaded.
        if (!FMLEnvironment.getDist().isClient()) return;
        context.enqueueWork(() -> open(payload.speaker(), payload.page(), payload.mode()));
    }

    /** Kept out of the lambda so the client class is only touched on the client. */
    private static void open(String speaker, String page, Mode mode) {
        net.minecraft.client.Minecraft.getInstance()
                .setScreen(new ConversationScreen(speaker, page, mode));
    }
}
