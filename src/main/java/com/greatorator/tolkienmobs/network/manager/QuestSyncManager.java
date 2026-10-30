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
import java.util.List;
import java.util.UUID;

/**
 * Carries a player's book to that player alone: the quests they hold, and the givers still waiting for
 * an answer. The giver ids are on the wire because the client cannot read world data and the question
 * mark over a giver's head has to be drawn from something.
 * <p>
 * Written by hand rather than composed, because a quest is a record of six fields and a list that a
 * composite codec would spell out twice.
 */
public record QuestSyncManager(List<Quest> quests, List<UUID> givers) implements CustomPacketPayload {
    public static final Type<QuestSyncManager> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", "quest_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, QuestSyncManager> STREAM_CODEC =
            CustomPacketPayload.codec(QuestSyncManager::write, QuestSyncManager::new);

    public QuestSyncManager(RegistryFriendlyByteBuf buf) {
        this(readQuests(buf), readGivers(buf));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(quests.size());
        for (Quest quest : quests) {
            writeQuest(buf, quest);
        }
        buf.writeVarInt(givers.size());
        for (UUID giver : givers) {
            buf.writeUUID(giver);
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
            int rewards = buf.readVarInt();
            List<Quest.Reward> paid = new ArrayList<>(rewards);
            for (int slot = 0; slot < rewards; slot++) {
                paid.add(new Quest.Reward(buf.readUtf(), buf.readVarInt()));
            }
            quests.add(new Quest(kind, target, goal, progress, employer, standing, List.copyOf(paid), giver, origin));
        }
        return quests;
    }

    private static List<UUID> readGivers(RegistryFriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<UUID> givers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            givers.add(buf.readUUID());
        }
        return givers;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(QuestSyncManager payload, IPayloadContext context) {
        // The book and the nameplates are drawn from this cache, so the client stores only its own.
        QuestLedger.acceptClient(payload.quests(), payload.givers());
    }
}
