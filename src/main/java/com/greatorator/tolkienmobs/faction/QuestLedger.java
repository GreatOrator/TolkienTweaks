package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.network.manager.QuestSyncManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Every player's quests, and every giver's unanswered offer, kept in the overworld's saved data.
 * <p>
 * Quests live here rather than on an item because the book is a list, not a stack of papers: a player
 * with four quests has one book, and losing the book is not abandoning the work. The offers live here
 * for the opposite reason - a giver is a world entity with a UUID and no room for a quest of its own
 * unless it carries a data component, and keeping the offer beside the player's quests means one file
 * answers both "what am I doing" and "who is waiting for an answer".
 * <p>
 * As with the standings, the server owns the data and sends each player their own copy. What it sends
 * beside the book is one {@link Mark} per giver this player has business with - work to take, work in
 * hand, or work to hand in, and whether that work is the mainline's or an errand - because a nameplate
 * is drawn on the client and the client cannot read world data. The mark is per player, so two players
 * can stand in front of the same giver and see different things over its head.
 */
public final class QuestLedger extends SavedData {
    private static final String SAVE_DATA_NAME = "quest_ledger";
    /** How many quests a player may hold at once, which is how many the book draws. */
    public static final int MAX_ACTIVE = 4;
    /** How long after logging in a player is first met by a giver, in ticks. */
    private static final long WELCOME_DELAY = 1200L;

    private static final Map<UUID, List<Quest>> ACTIVE = new HashMap<>();
    private static final Map<UUID, Long> NEXT_GIVER = new HashMap<>();
    private static final Map<UUID, Offer> OFFERS = new HashMap<>();

    private static final List<Quest> CLIENT = new ArrayList<>();
    private static final Map<UUID, Mark> CLIENT_MARKS = new HashMap<>();

    /**
     * What is drawn over a giver's head, from the point of view of one player.
     *
     * @param state    what this player has to do about the giver
     * @param mainline whether the work is the campaign's rather than an errand, which decides the ink
     */
    public record Mark(State state, boolean mainline) {
        /**
         * The three states a mark can be in. The order matters: when a giver has two reasons to be
         * marked - a quest in hand and, somehow, a fresh offer as well - the later one wins, so a
         * hand-in is never hidden behind a new offer.
         */
        public enum State {
            /** There is work here this player has not taken: an exclamation mark. */
            AVAILABLE,
            /** This player has taken work from here and has not finished it: a dim exclamation mark. */
            IN_PROGRESS,
            /** The work is done and this giver will take it: a question mark. */
            TURN_IN
        }
    }

    public static final Codec<QuestLedger> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerEntry.CODEC.listOf().optionalFieldOf("players", List.of()).forGetter(QuestLedger::players),
            Offer.CODEC.listOf().optionalFieldOf("offers", List.of()).forGetter(QuestLedger::offers)
    ).apply(instance, QuestLedger::fromEntries));

    public static final SavedDataType<QuestLedger> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), QuestLedger::new, CODEC);

    private QuestLedger() {
        super();
    }

    // ---- reading ----

    /** The quests a player holds, as last synced from the server on a client. */
    public static List<Quest> clientQuests() {
        return CLIENT;
    }

    /** What to draw over a giver, or null when this player has no business with it. */
    @Nullable
    public static Mark clientMark(UUID giver) {
        return CLIENT_MARKS.get(giver);
    }

    /** Whether the giver this client knows by id is still waiting for an answer. */
    public static boolean clientHasOffer(UUID giver) {
        Mark mark = CLIENT_MARKS.get(giver);
        return mark != null && mark.state() == Mark.State.AVAILABLE;
    }

    public static void acceptClient(List<Quest> quests, Map<UUID, Mark> marks) {
        CLIENT.clear();
        CLIENT.addAll(quests);
        CLIENT_MARKS.clear();
        CLIENT_MARKS.putAll(marks);
    }

    public static List<Quest> active(Player player) {
        if (player.level().isClientSide()) return clientQuests();
        ensureLoaded(player.level());
        return row(player);
    }

    /** Whether the player is carrying one of the quest books already. */
    public static boolean hasBook(ServerPlayer player) {
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (inventory.getItem(slot).is(TolkienItems.QUEST_BOOK.get())) return true;
        }
        return false;
    }

    // ---- taking work ----

    /**
     * Writes a quest into the player's book, and hands them the book if they have never had one. That
     * is the only way a book enters a world: a player who has never been offered work has nothing to
     * read, so there is nothing to carry.
     */
    public static boolean accept(ServerPlayer player, Quest quest) {
        List<Quest> quests = row(player);
        if (quests.size() >= MAX_ACTIVE) {
            player.sendSystemMessage(Component.translatable("quest.tolkienmobs.full")
                    .withStyle(ChatFormatting.GRAY));
            return false;
        }
        quests.add(quest);
        store(player).setDirty();
        grantBook(player);
        player.sendSystemMessage(Component.translatable("quest.tolkienmobs.accepted", quest.objective())
                .withStyle(ChatFormatting.WHITE));
        player.level().playSound(null, player.blockPosition(), SoundEvents.VILLAGER_YES,
                SoundSource.PLAYERS, 0.6F, 1.0F);
        sync(player);
        return true;
    }

    /** Puts a book in the player's pack unless they already carry one. */
    public static void grantBook(ServerPlayer player) {
        if (hasBook(player)) return;
        ItemStack book = new ItemStack(TolkienItems.QUEST_BOOK.get());
        if (!player.getInventory().add(book)) {
            player.drop(book, false);
        }
        player.sendSystemMessage(Component.translatable("quest.tolkienmobs.book.given")
                .withStyle(ChatFormatting.GRAY));
    }

    /**
     * Sets a quest aside. Work is not a trap: a player who has changed their mind, or who cannot reach
     * the mobs a hunt asks for, should not be carrying the quest for ever. Nothing is paid, nothing is
     * taken out of the pack, and the slot it held is free again.
     *
     * @param slot the quest's place in the player's book, which is what the client sent
     * @return whether there was a quest there to set aside
     */
    public static boolean abandon(ServerPlayer player, int slot) {
        List<Quest> quests = row(player);
        if (slot < 0 || slot >= quests.size()) return false;
        Quest dropped = quests.remove(slot);
        store(player).setDirty();
        player.sendSystemMessage(Component.translatable("quest.tolkienmobs.abandoned", dropped.objective())
                .withStyle(ChatFormatting.GRAY));
        sync(player);
        return true;
    }

    /** Forgets every quest a player carries, for an operator starting a test over. Nothing is paid. */
    public static int clear(ServerPlayer player) {
        List<Quest> quests = row(player);
        int cleared = quests.size();
        if (cleared == 0) return 0;
        quests.clear();
        store(player).setDirty();
        sync(player);
        return cleared;
    }

    // ---- offers ----

    /** Notes what a giver is asking for, and tells the player so their nameplate shows it. */
    public static void offer(ServerPlayer player, UUID giver, Quest quest) {
        ensureLoaded(player.level());
        OFFERS.put(giver, new Offer(giver, player.getUUID(), quest));
        store(player).setDirty();
        sync(player);
    }

    @Nullable
    public static Quest offerOf(UUID giver) {
        Offer offer = OFFERS.get(giver);
        return offer == null ? null : offer.quest();
    }

    public static void dropOffer(ServerPlayer player, UUID giver) {
        if (OFFERS.remove(giver) != null) {
            store(player).setDirty();
            sync(player);
        }
    }

    /** Forgets every offer a giver held, for a giver that died or was removed. */
    public static void forgetGiver(ServerPlayer player, UUID giver) {
        dropOffer(player, giver);
    }

    // ---- progress ----

    /**
     * Moves every kill quest the player holds that this death answers. A faction quest is answered by
     * whatever its target alliance's soldier tag names, and a hunt quest by the exact mob it names.
     */
    public static void progressKill(ServerPlayer player, Entity victim) {
        List<Quest> quests = row(player);
        boolean changed = false;
        for (int slot = 0; slot < quests.size(); slot++) {
            Quest quest = quests.get(slot);
            if (quest.kind() == Quest.Kind.FETCH || quest.complete() || !answeredBy(quest, victim)) continue;
            Quest after = quest.advanced();
            quests.set(slot, after);
            changed = true;
            player.sendOverlayMessage(after.complete()
                    ? Component.translatable("quest.tolkienmobs.done").withStyle(ChatFormatting.GREEN)
                    : Component.translatable("quest.tolkienmobs.progress",
                            Component.literal(String.valueOf(after.progress())),
                            Component.literal(String.valueOf(after.goal())))
                    .withStyle(ChatFormatting.GRAY));
        }
        if (changed) {
            store(player).setDirty();
            sync(player);
        }
    }

    /**
     * Reads every fetch quest's progress off the pack, since what a player carries is the progress and
     * keeping a second copy of it in the file would only let the two drift apart.
     */
    public static void refreshFetch(ServerPlayer player) {
        List<Quest> quests = row(player);
        boolean changed = false;
        for (int slot = 0; slot < quests.size(); slot++) {
            Quest quest = quests.get(slot);
            if (quest.kind() != Quest.Kind.FETCH) continue;
            int carried = Math.min(quest.goal(), countOf(player, quest));
            if (carried == quest.progress()) continue;
            quests.set(slot, quest.withProgress(carried));
            changed = true;
        }
        if (changed) {
            store(player).setDirty();
            sync(player);
        }
    }

    private static boolean answeredBy(Quest quest, Entity victim) {
        if (quest.kind() == Quest.Kind.FACTION) {
            Alliance alliance = Alliance.byId(quest.target());
            return victim.getType().builtInRegistryHolder().is(alliance.soldiers());
        }
        return BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType()).toString().equals(quest.target());
    }

    // ---- handing work in ----

    /**
     * Pays the first quest the player has finished, if any.
     *
     * @return whether a quest was paid, which is what decides how a giver answers a right click
     */
    public static boolean turnInFirst(ServerPlayer player) {
        List<Quest> quests = row(player);
        for (int slot = 0; slot < quests.size(); slot++) {
            Quest quest = quests.get(slot);
            if (!ready(player, quest)) continue;
            if (quest.kind() == Quest.Kind.FETCH && !take(player, quest)) continue;
            pay(player, quest);
            quests.remove(slot);
            store(player).setDirty();
            sync(player);
            return true;
        }
        return false;
    }

    /** Whether a quest's terms are met: kills are counted, goods are read off the pack. */
    public static boolean ready(ServerPlayer player, Quest quest) {
        return quest.kind() == Quest.Kind.FETCH ? countOf(player, quest) >= quest.goal() : quest.complete();
    }

    private static void pay(ServerPlayer player, Quest quest) {
        if (quest.kind() == Quest.Kind.FACTION) {
            Alliance employer = quest.employerAlliance();
            if (employer != null) {
                FactionStandings.adjust(player, employer, quest.standing());
            }
        }
        for (Quest.Reward reward : quest.rewards()) {
            ItemStack stack = reward.stack();
            if (stack.isEmpty()) continue;
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
        // Work seen through is courage earned the long way: one deed, whatever the work was.
        CourageLedger.award(player, CourageLedger.Deed.WORK_DONE);
        player.sendSystemMessage(Component.translatable("quest.tolkienmobs.turned_in", quest.objective())
                .withStyle(ChatFormatting.GREEN));
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS, 0.7F, 1.2F);
    }

    private static int countOf(ServerPlayer player, Quest quest) {
        Item item = quest.item();
        if (item == null) return 0;
        int count = 0;
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(item)) count += stack.getCount();
        }
        return count;
    }

    /** Takes exactly the quest's worth of its item out of the pack. */
    private static boolean take(ServerPlayer player, Quest quest) {
        Item item = quest.item();
        if (item == null || countOf(player, quest) < quest.goal()) return false;
        int left = quest.goal();
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize() && left > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(item)) continue;
            int taken = Math.min(left, stack.getCount());
            stack.shrink(taken);
            left -= taken;
        }
        return left == 0;
    }

    // ---- the giver's clock ----

    /** When the player may next be met by a giver, in world time. */
    public static long nextGiver(Player player) {
        ensureLoaded(player.level());
        return NEXT_GIVER.getOrDefault(player.getUUID(), 0L);
    }

    public static void setNextGiver(ServerPlayer player, long when) {
        NEXT_GIVER.put(player.getUUID(), when);
        store(player).setDirty();
    }

    /** Gives a player who has never been offered work a short wait rather than a giver on login. */
    public static void welcome(ServerPlayer player) {
        ensureLoaded(player.level());
        if (NEXT_GIVER.containsKey(player.getUUID())) return;
        NEXT_GIVER.put(player.getUUID(), levelTime(player) + WELCOME_DELAY);
        store(player).setDirty();
    }

    private static long levelTime(ServerPlayer player) {
        return player.level().getGameTime();
    }

    // ---- pluming ----

    /**
     * Sends a player their own book, and the mark for every giver it mentions: an offer still waiting,
     * a quest still in hand, or a quest that is done and can be handed in. A giver of the mainline is
     * marked as such, which is what colours it gold rather than blue.
     */
    public static void sync(ServerPlayer player) {
        ensureLoaded(player.level());
        if (!(player.level() instanceof ServerLevel level)) return;
        Map<UUID, Mark> marks = new HashMap<>();
        for (Offer offer : OFFERS.values()) {
            if (offer.player().equals(player.getUUID())) {
                marks.put(offer.giver(), new Mark(Mark.State.AVAILABLE,
                        QuestGivers.isCampaign(level, offer.giver())));
            }
        }
        for (Quest quest : row(player)) {
            // A quest a command wrote into the book has no giver to mark, so it marks nobody.
            if (!quest.fromNpc()) continue;
            marks.merge(quest.giverId(), new Mark(
                            ready(player, quest) ? Mark.State.TURN_IN : Mark.State.IN_PROGRESS,
                            QuestGivers.isCampaign(level, quest.giverId())),
                    QuestLedger::later);
        }
        PacketDistributor.sendToPlayer(player, new QuestSyncManager(List.copyOf(row(player)), Map.copyOf(marks)));
    }

    /** The mark that wins when a giver has two claims on this player's attention. */
    private static Mark later(Mark first, Mark second) {
        return first.state().ordinal() >= second.state().ordinal() ? first : second;
    }

    private static List<Quest> row(Player player) {
        // The world file is read before the row is touched: decoding it rebuilds these maps, so a
        // write that landed before the load would be thrown away by it.
        ensureLoaded(player.level());
        return ACTIVE.computeIfAbsent(player.getUUID(), ignored -> new ArrayList<>());
    }

    private static void ensureLoaded(Level level) {
        instance(level);
    }

    private static QuestLedger store(Player player) {
        return instance(player.level());
    }

    private static QuestLedger instance(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) return overworld.getDataStorage().computeIfAbsent(TYPE);
        }
        return new QuestLedger();
    }

    // ---- saving ----

    private static QuestLedger fromEntries(List<PlayerEntry> players, List<Offer> offers) {
        ACTIVE.clear();
        NEXT_GIVER.clear();
        OFFERS.clear();
        for (PlayerEntry entry : players) {
            ACTIVE.put(entry.player(), new ArrayList<>(entry.quests()));
            NEXT_GIVER.put(entry.player(), entry.nextGiver());
        }
        for (Offer offer : offers) {
            OFFERS.put(offer.giver(), offer);
        }
        return new QuestLedger();
    }

    private List<PlayerEntry> players() {
        List<PlayerEntry> entries = new ArrayList<>(ACTIVE.size());
        for (Map.Entry<UUID, List<Quest>> row : ACTIVE.entrySet()) {
            entries.add(new PlayerEntry(row.getKey(), List.copyOf(row.getValue()),
                    NEXT_GIVER.getOrDefault(row.getKey(), 0L)));
        }
        return entries;
    }

    private List<Offer> offers() {
        return List.copyOf(OFFERS.values());
    }

    public record PlayerEntry(UUID player, List<Quest> quests, long nextGiver) {
        public static final Codec<PlayerEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("player").forGetter(PlayerEntry::player),
                Quest.CODEC.listOf().optionalFieldOf("quests", List.of()).forGetter(PlayerEntry::quests),
                Codec.LONG.optionalFieldOf("next_giver", 0L).forGetter(PlayerEntry::nextGiver)
        ).apply(instance, PlayerEntry::new));
    }

    /** One giver's unanswered offer: who is waiting, and what they are asking for. */
    public record Offer(UUID giver, UUID player, Quest quest) {
        public static final Codec<Offer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("giver").forGetter(Offer::giver),
                UUIDUtil.CODEC.fieldOf("player").forGetter(Offer::player),
                Quest.CODEC.fieldOf("quest").forGetter(Offer::quest)
        ).apply(instance, Offer::new));
    }
}
