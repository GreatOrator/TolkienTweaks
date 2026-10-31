package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.faction.Quest;
import com.greatorator.tolkienmobs.faction.QuestLedger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Carries a player's book to that player alone: the quests they hold, and one mark per giver this
 * player has business with. The marks are on the wire because the client cannot read world data and
 * the mark over a giver's head has to be drawn from something - and because the mark is the player's,
 * not the giver's, so the same NPC is a question mark to one player and a dimmed exclamation to
 * another. A mark also says which line it belongs to, since the ink is what tells a mainline step from
 * an ordinary errand.
 * <p>
 * Written by hand rather than composed, because a quest is a record of ten fields and a table that a
 * composite codec would spell out twice.
 */
public record QuestSyncManager(List<Quest> quests, Map<UUID, QuestLedger.Mark> marks)
        implements CustomPacketPayload {
    public static final Type<QuestSyncManager> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", "quest_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, QuestSyncManager> STREAM_CODEC =
            CustomPacketPayload.codec(QuestSyncManager::write, QuestSyncManager::new);

    public QuestSyncManager(RegistryFriendlyByteBuf buf) {
        this(readQuests(buf), readMarks(buf));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(quests.size());
        for (Quest quest : quests) {
            writeQuest(buf, quest);
        }
        buf.writeVarInt(marks.size());
        for (Map.Entry<UUID, QuestLedger.Mark> mark : marks.entrySet()) {
            buf.writeUUID(mark.getKey());
            buf.writeVarInt(mark.getValue().state().ordinal());
            buf.writeBoolean(mark.getValue().mainline());
        }
    }

    private static void writeQuest(RegistryFriendlyByteBuf buf, Quest quest) {
        buf.writeVarInt(quest.kind().ordinal());
        buf.writeUtf(quest.target());
        buf.writeVarInt(quest.goal());
        buf.writeVarInt(quest.progress());
        buf.writeUtf(quest.employer());
        buf.writeVarInt(quest.standing());
        buf.writeUtf(quest.giver());
        buf.writeBlockPos(quest.origin());
        buf.writeUUID(quest.giverId());
        buf.writeVarInt(quest.rewards().size());
        for (Quest.Reward reward : quest.rewards()) {
            buf.writeUtf(reward.item());
            buf.writeVarInt(reward.count());
        }
    }

    private static List<Quest> readQuests(RegistryFriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<Quest> quests = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Quest.Kind kind = Quest.Kind.values()[Math.min(buf.readVarInt(), Quest.Kind.values().length - 1)];
            String target = buf.readUtf();
            int goal = buf.readVarInt();
            int progress = buf.readVarInt();
            String employer = buf.readUtf();
            int standing = buf.readVarInt();
            String giver = buf.readUtf();
            BlockPos origin = buf.readBlockPos();
            UUID giverId = buf.readUUID();
            int rewards = buf.readVarInt();
            List<Quest.Reward> paid = new ArrayList<>(rewards);
            for (int slot = 0; slot < rewards; slot++) {
                paid.add(new Quest.Reward(buf.readUtf(), buf.readVarInt()));
            }
            quests.add(new Quest(kind, target, goal, progress, employer, standing, List.copyOf(paid), giver,
                    origin, giverId));
        }
        return quests;
    }

    /** A mark is a byte and a flag, clamped so a client dropped in from a newer build still reads. */
    private static Map<UUID, QuestLedger.Mark> readMarks(RegistryFriendlyByteBuf buf) {
        int count = buf.readVarInt();
        QuestLedger.Mark.State[] states = QuestLedger.Mark.State.values();
        Map<UUID, QuestLedger.Mark> marks = new LinkedHashMap<>(Math.max(1, count));
        for (int i = 0; i < count; i++) {
            UUID giver = buf.readUUID();
            QuestLedger.Mark.State state = states[Math.min(buf.readVarInt(), states.length - 1)];
            marks.put(giver, new QuestLedger.Mark(state, buf.readBoolean()));
        }
        return Map.copyOf(marks);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(QuestSyncManager payload, IPayloadContext context) {
        // The book and the nameplates are drawn from this cache, so the client stores only its own.
        QuestLedger.acceptClient(payload.quests(), payload.marks());
    }
}
