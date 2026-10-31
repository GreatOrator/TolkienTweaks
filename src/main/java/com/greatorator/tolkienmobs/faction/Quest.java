package com.greatorator.tolkienmobs.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * One piece of work a quest giver hands out, and the terms it is done on.
 * <p>
 * Unlike a mercenary contract, a quest is not carried on paper: it is written down in the player's
 * ledger, which is what the quest book opens. That is why the giver has to keep existing after the
 * work is taken - the ledger is the player's, not the NPC's.
 * <p>
 * Three shapes of work are known:
 * <ul>
 *   <li>{@link Kind#FACTION} - kill soldiers of one alliance for another. It pays standing, because
 *   the work is political: the employer is named, and the soldier killed is the one its tag names.</li>
 *   <li>{@link Kind#FETCH} - bring a number of one item. It pays goods and coin.</li>
 *   <li>{@link Kind#HUNT} - kill a number of one mob. It pays goods and coin.</li>
 * </ul>
 * A target is an id rather than an object so a quest survives a reload: an alliance id, an item id or
 * an entity type id, read back through the registries on demand. Both registries are defaulted in
 * 26.1, so every lookup is guarded by {@code containsKey} - a typo then reads as a quest nobody can
 * finish rather than as an arbitrary other quest.
 * <p>
 * {@code giver} is the name the NPC was handed. A name drawn from the mod's own table is kept as the
 * key it is translated from, so a client reads it in its own language; a name a pack set in the config
 * is kept as the words it was written in. {@code origin} is where the giver stood when the work was
 * offered, which is what the book calls the place it was taken. {@code giverId} is that same giver's
 * entity id, which is what lets the mark over their head follow <em>this</em> player's copy of the
 * work: the name is for reading, the id is for finding them again.
 */
public record Quest(Kind kind, String target, int goal, int progress, String employer, int standing,
                    List<Reward> rewards, String giver, BlockPos origin, UUID giverId) {
    /** The id a quest carries when no NPC handed it out, as when a command writes one into a book. */
    public static final UUID NOBODY = new UUID(0L, 0L);

    /** The most progress a quest can hold, so a kill after completion cannot write past the goal. */
    public enum Kind {
        FACTION, FETCH, HUNT;

        public static final Codec<Kind> CODEC = Codec.STRING.xmap(Kind::byName, Kind::name);

        static Kind byName(String name) {
            for (Kind kind : values()) {
                if (kind.name().equalsIgnoreCase(name)) return kind;
            }
            return FETCH;
        }
    }

    /** One thing a quest pays: an item id and how many of it. */
    public record Reward(String item, int count) {
        public static final Codec<Reward> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("item").forGetter(Reward::item),
                Codec.INT.optionalFieldOf("count", 1).forGetter(Reward::count)
        ).apply(instance, Reward::new));

        /** The stack this pays, or an empty one when the id names nothing the item registry knows. */
        public ItemStack stack() {
            Identifier id = Identifier.tryParse(item);
            if (id == null || count <= 0 || !BuiltInRegistries.ITEM.containsKey(id)) return ItemStack.EMPTY;
            return new ItemStack(BuiltInRegistries.ITEM.getValue(id), count);
        }
    }

    public static final Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Kind.CODEC.fieldOf("kind").forGetter(Quest::kind),
            Codec.STRING.fieldOf("target").forGetter(Quest::target),
            Codec.INT.optionalFieldOf("goal", 1).forGetter(Quest::goal),
            Codec.INT.optionalFieldOf("progress", 0).forGetter(Quest::progress),
            Codec.STRING.optionalFieldOf("employer", "").forGetter(Quest::employer),
            Codec.INT.optionalFieldOf("standing", 0).forGetter(Quest::standing),
            Reward.CODEC.listOf().optionalFieldOf("rewards", List.of()).forGetter(Quest::rewards),
            Codec.STRING.optionalFieldOf("giver", "").forGetter(Quest::giver),
            BlockPos.CODEC.optionalFieldOf("origin", BlockPos.ZERO).forGetter(Quest::origin),
            UUIDUtil.CODEC.optionalFieldOf("giver_id", NOBODY).forGetter(Quest::giverId)
    ).apply(instance, Quest::new));

    public boolean complete() {
        return progress >= goal;
    }

    /** Whether an NPC is behind this work, which is what puts a mark over their head. */
    public boolean fromNpc() {
        return !NOBODY.equals(giverId);
    }

    /** The same quest one kill further on. Fetch quests are read off the pack instead. */
    public Quest advanced() {
        return new Quest(kind, target, goal, Math.min(goal, progress + 1), employer, standing, rewards,
                giver, origin, giverId);
    }

    public Quest withProgress(int value) {
        return new Quest(kind, target, goal, Math.max(0, Math.min(goal, value)), employer, standing, rewards,
                giver, origin, giverId);
    }

    /** The same quest, noting who offered it, where they stood, and which body they were. */
    public Quest withGiver(String giver, BlockPos origin, UUID giverId) {
        return new Quest(kind, target, goal, progress, employer, standing, rewards, giver, origin, giverId);
    }

    /** The alliance the work is done for, or null when the work is nobody's. */
    public Alliance employerAlliance() {
        return employer.isEmpty() ? null : Alliance.byId(employer);
    }

    /** The item a fetch quest asks for, or null when the id is not one this world knows. */
    public Item item() {
        Identifier id = Identifier.tryParse(target);
        if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) return null;
        return BuiltInRegistries.ITEM.getValue(id);
    }

    /** The mob a hunt quest asks for, or null when the id is not one this world knows. */
    public EntityType<?> entityType() {
        Identifier id = Identifier.tryParse(target);
        if (id == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(id)) return null;
        return BuiltInRegistries.ENTITY_TYPE.getValue(id);
    }

    /** What the quest asks for, in the terms the player reads it in. */
    public Component objective() {
        return switch (kind) {
            case FACTION -> Component.translatable("quest.tolkienmobs.faction",
                    Component.literal(String.valueOf(goal)), targetName());
            case FETCH -> Component.translatable("quest.tolkienmobs.fetch",
                    Component.literal(String.valueOf(goal)), targetName());
            case HUNT -> Component.translatable("quest.tolkienmobs.hunt",
                    Component.literal(String.valueOf(goal)), targetName());
        };
    }

    /** The alliance, item or mob the quest is about, named the way its own screen names it. */
    public Component targetName() {
        return switch (kind) {
            case FACTION -> Alliance.byId(target).displayName();
            case FETCH -> {
                Item item = item();
                yield item == null ? Component.literal(target) : new ItemStack(item).getHoverName();
            }
            case HUNT -> {
                EntityType<?> type = entityType();
                yield type == null ? Component.literal(target) : type.getDescription();
            }
        };
    }

    /**
     * The giver's own name, resolved on the client so it reads in the player's language. A name a pack
     * set in the config carries no key and is shown as it was written.
     */
    public Component giverName() {
        if (giver.isEmpty()) return Component.translatable("quest.tolkienmobs.giver.unknown");
        return label(giver);
    }

    /**
     * A giver's name as the game draws it: the mod's own eight are keys, so a client reads them in its
     * own language, and a name a pack set in the config is the words it was written in.
     */
    public static Component label(String name) {
        return name.startsWith("quest.tolkienmobs.name.")
                ? Component.translatable(name)
                : Component.literal(name);
    }

    /** Where the work was taken: the block the giver stood on when they offered it. */
    public Component takenAt() {
        return Component.translatable("quest.tolkienmobs.taken_at",
                Component.literal(origin.getX() + ", " + origin.getY() + ", " + origin.getZ()));
    }

    /** A sentence of why the work exists, so the book is not only a shopping list. */
    public Component summary() {
        return switch (kind) {
            case FACTION -> Component.translatable("quest.tolkienmobs.summary.faction",
                    targetName(), Component.literal(String.valueOf(goal)));
            case FETCH -> Component.translatable("quest.tolkienmobs.summary.fetch",
                    Component.literal(String.valueOf(goal)), targetName());
            case HUNT -> Component.translatable("quest.tolkienmobs.summary.hunt",
                    targetName(), Component.literal(String.valueOf(goal)));
        };
    }

    /** The line the ledger prints under a quest's name: what the work pays. */
    public Component rewardLine() {
        if (kind == Kind.FACTION) {
            Alliance alliance = employerAlliance();
            return Component.translatable("quest.tolkienmobs.reward.standing",
                            Component.literal(String.valueOf(standing)),
                            alliance == null ? Component.empty() : alliance.displayName())
                    .withStyle(ChatFormatting.GOLD);
        }
        StringBuilder paid = new StringBuilder();
        for (Reward reward : rewards) {
            ItemStack stack = reward.stack();
            if (stack.isEmpty()) continue;
            if (!paid.isEmpty()) paid.append(", ");
            paid.append(stack.getCount()).append(' ').append(stack.getHoverName().getString());
        }
        return Component.translatable("quest.tolkienmobs.reward.items", paid)
                .withStyle(ChatFormatting.GOLD);
    }
}
