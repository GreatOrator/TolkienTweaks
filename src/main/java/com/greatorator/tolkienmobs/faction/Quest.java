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

import javax.annotation.Nullable;
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
                    List<Reward> rewards, String giver, BlockPos origin, UUID giverId, boolean story,
                    String turnIn) {
    /** The id a quest carries when no NPC handed it out, as when a command writes one into a book. */
    public static final UUID NOBODY = new UUID(0L, 0L);

    /** The most progress a quest can hold, so a kill after completion cannot write past the goal. */
    public enum Kind {
        FACTION, FETCH, HUNT, LOCATION, DIALOGUE;

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
            UUIDUtil.CODEC.optionalFieldOf("giver_id", NOBODY).forGetter(Quest::giverId),
            Codec.BOOL.optionalFieldOf("story", false).forGetter(Quest::story),
            Codec.STRING.optionalFieldOf("turn_in", "").forGetter(Quest::turnIn)
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
                giver, origin, giverId, story, turnIn);
    }

    public Quest withProgress(int value) {
        return new Quest(kind, target, goal, Math.max(0, Math.min(goal, value)), employer, standing, rewards,
                giver, origin, giverId, story, turnIn);
    }

    /** The same quest, noting who offered it, where they stood, and which body they were. */
    public Quest withGiver(String giver, BlockPos origin, UUID giverId) {
        return new Quest(kind, target, goal, progress, employer, standing, rewards, giver, origin, giverId,
                story, turnIn);
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

    /**
     * Where a location page ends: a named place, whose circle the world has drawn where the world put
     * it, or a point of the page's own with a reach round it. A place is named because a barrow is
     * where a world made it rather than where a page guessed; a point is written down for ground no
     * catalogue knows.
     */
    public record Spot(@Nullable String place, BlockPos centre, int radius) {
        /** The name a player reads: the place's own, or the point spelled out. */
        public Component name() {
            return place != null
                    ? Component.translatable("place.tolkienmobs." + place)
                    : Component.literal(centre.getX() + ", " + centre.getY() + ", " + centre.getZ());
        }
    }

    /** A place of the catalogue as a page's target, as in {@code place:greenway}. */
    public static String placeTarget(String id) {
        return "place:" + id;
    }

    /** A point as a page's target, as in {@code at:100,64,-20,16}. */
    public static String pointTarget(int x, int y, int z, int radius) {
        return "at:" + x + "," + y + "," + z + "," + radius;
    }

    /** A page of talk as a dialogue page's target, as in {@code leaf:campaign.tolkienmobs.page.muster}. */
    public static String leafTarget(String key) {
        return "leaf:" + key;
    }

    /** A body as a dialogue page's target, as in {@code person:Maelgrin}. */
    public static String personTarget(String name) {
        return "person:" + name;
    }

    /** Whether the work is done by walking to a place rather than by killing or fetching anything. */
    public boolean walks() {
        return kind == Kind.LOCATION;
    }

    /** Whether the work is done by hearing somebody out. */
    public boolean talks() {
        return kind == Kind.DIALOGUE;
    }

    /**
     * Whether the work settles itself where it happens instead of being carried back to a giver.
     * Reaching a place and hearing a conversation are each their own proof, so there is nothing for a
     * giver to check and no reason to walk back for the words "well done".
     */
    public boolean selfSettles() {
        return walks() || talks();
    }

    /** Where a location page ends, or null when the page is not one or names nowhere to walk. */
    @Nullable
    public Spot spot() {
        if (!walks()) return null;
        if (target.startsWith("at:")) {
            String[] parts = target.substring("at:".length()).split(",");
            if (parts.length < 3) return null;
            try {
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                int z = Integer.parseInt(parts[2].trim());
                int radius = parts.length > 3
                        ? Math.max(1, Integer.parseInt(parts[3].trim()))
                        : Places.DEFAULT_RADIUS;
                return new Spot(null, new BlockPos(x, y, z), radius);
            } catch (NumberFormatException malformed) {
                // A hand-written page whose point is not a point is a page nobody can walk, which is
                // what the book then says, rather than a throw in the middle of a tick.
                return null;
            }
        }
        // A bare id is read as a place, because that is the shorter way to write the commoner case.
        String id = target.startsWith("place:") ? target.substring("place:".length()) : target;
        return id.isEmpty() ? null : new Spot(id, BlockPos.ZERO, Places.DEFAULT_RADIUS);
    }

    /** The page of talk a dialogue page asks to be read, or null when it asks for a body instead. */
    @Nullable
    public String leaf() {
        return talks() && target.startsWith("leaf:") ? target.substring("leaf:".length()) : null;
    }

    /** The body a dialogue page asks to be spoken to, or null when it asks for a page instead. */
    @Nullable
    public String person() {
        if (!talks() || target.startsWith("leaf:")) return null;
        return target.startsWith("person:") ? target.substring("person:".length()) : target;
    }

    /** Who the work goes back to, or an empty component when any giver will take it. */
    public Component takenTo() {
        return turnIn.isEmpty()
                ? Component.empty()
                : Component.translatable("quest.tolkienmobs.turn_in", Component.literal(turnIn));
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
            case LOCATION -> Component.translatable("quest.tolkienmobs.reach", targetName());
            case DIALOGUE -> leaf() != null
                    ? Component.translatable("quest.tolkienmobs.read", targetName())
                    : Component.translatable("quest.tolkienmobs.speak", targetName());
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
            case LOCATION -> {
                Spot spot = spot();
                yield spot == null ? Component.literal(target) : spot.name();
            }
            case DIALOGUE -> {
                String leaf = leaf();
                yield leaf != null ? Component.translatable(leaf) : Component.literal(target);
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
            case LOCATION -> Component.translatable("quest.tolkienmobs.summary.reach", targetName());
            case DIALOGUE -> Component.translatable("quest.tolkienmobs.summary.dialogue");
        };
    }

    /** The line the ledger prints under a quest's name: what the work pays. */
    public Component rewardLine() {
        if (standing > 0) {
            Alliance alliance = employerAlliance();
            // A page every country pays for names no single employer, so it is read as what it is
            // rather than as standing with nobody.
            if (alliance == null) {
                return Component.translatable("quest.tolkienmobs.reward.standing.all",
                                Component.literal(String.valueOf(standing)))
                        .withStyle(ChatFormatting.GOLD);
            }
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
        // A page can pay nothing in goods at all - a front's errand pays standing and courage, and the
        // East's ends on a scroll it wants carried rather than on a purse - so the line is left off
        // rather than drawn as a reward of nothing.
        if (paid.isEmpty()) return Component.empty();
        // The words are read as text: a translation takes components, numbers, booleans or strings,
        // and the builder this was written with is none of them. Handing it one threw while the leaf
        // was being drawn, which is a crash rather than a page.
        return Component.translatable("quest.tolkienmobs.reward.items", paid.toString())
                .withStyle(ChatFormatting.GOLD);
    }
}
