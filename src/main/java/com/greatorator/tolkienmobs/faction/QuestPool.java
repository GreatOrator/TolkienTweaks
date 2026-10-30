package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Where the quests a giver offers come from.
 * <p>
 * The pool is written here rather than read from a datapack because the mod's trades are written the
 * same way, and a table in code can be read while the mod is still being constructed. Every id is a
 * string: a quest is saved with its target, so moving an entry here cannot rewrite work already taken.
 * <p>
 * Faction work is offered by a giver standing in a country, and pays standing with that country. The
 * rest is ordinary work - grain for a household, wolves out of a field - and pays goods and coin,
 * because a farmer has no standing to give away.
 */
public final class QuestPool {
    /** How often a giver with no country behind them offers political work. */
    private static final float FACTION_CHANCE = 0.45F;

    private static final List<String> FETCH = List.of(
            "minecraft:wheat",
            "minecraft:bread",
            "minecraft:leather",
            "minecraft:coal",
            "minecraft:string",
            "minecraft:bone",
            "minecraft:iron_ingot",
            "tolkienmobs:leather_mumakil",
            "tolkienmobs:item_blankpaper");

    private static final List<String> HUNT = List.of(
            "tolkienmobs:entityttmgoblin",
            "tolkienmobs:entityttmwarg",
            "tolkienmobs:entityttmmirkwoodspider",
            "minecraft:zombie",
            "minecraft:skeleton",
            "minecraft:spider");

    /** What a non-faction quest pays on top of its coin. */
    private static final List<String> GOODS = List.of(
            "tolkienmobs:food_lembas",
            "tolkienmobs:food_cram",
            "tolkienmobs:item_blankpaper",
            "tolkienmobs:leather_mumakil",
            "minecraft:iron_ingot");

    /** The coin a non-faction quest pays, as the id of the mod's own coin. */
    private static final String GOLD_COIN = "tolkienmobs:item_coin_gold";

    private static final int NAMES = 8;

    private QuestPool() {
    }

    /**
     * Fresh work for whoever is standing in front of the giver.
     *
     * @param employer the country the giver stands in, or null when they stand in no country at all
     */
    public static Quest offer(RandomSource random, @Nullable Alliance employer) {
        if (employer != null && random.nextFloat() < FACTION_CHANCE) {
            return faction(random, employer);
        }
        return random.nextBoolean() ? fetch(random) : hunt(random);
    }

    /** Work for a country: its rivals' soldiers are the target, and the pay is standing. */
    private static Quest faction(RandomSource random, Alliance employer) {
        List<Alliance> rivals = employer.rivals();
        Alliance target = rivals.get(random.nextInt(rivals.size()));
        int goal = 3 + random.nextInt(4);
        // Paid at more than the kills themselves are worth, because the giver is asking on purpose.
        int standing = 20 + goal * 5;
        return new Quest(Quest.Kind.FACTION, target.id(), goal, 0, employer.id(), standing, List.of(), "", BlockPos.ZERO);
    }

    private static Quest fetch(RandomSource random) {
        String item = FETCH.get(random.nextInt(FETCH.size()));
        int goal = 4 + random.nextInt(9);
        return new Quest(Quest.Kind.FETCH, item, goal, 0, "", 0, payment(random, goal), "", BlockPos.ZERO);
    }

    private static Quest hunt(RandomSource random) {
        String mob = HUNT.get(random.nextInt(HUNT.size()));
        int goal = 3 + random.nextInt(5);
        return new Quest(Quest.Kind.HUNT, mob, goal, 0, "", 0, payment(random, goal), "", BlockPos.ZERO);
    }

    /** Coin, and sometimes a thing worth carrying: the harder the work, the more of both. */
    private static List<Quest.Reward> payment(RandomSource random, int goal) {
        List<Quest.Reward> paid = new ArrayList<>(2);
        paid.add(new Quest.Reward(GOLD_COIN, 1 + random.nextInt(2 + goal / 3)));
        if (random.nextFloat() < 0.6F) {
            paid.add(new Quest.Reward(GOODS.get(random.nextInt(GOODS.size())), 1 + random.nextInt(3)));
        }
        return List.copyOf(paid);
    }

    /**
     * What a giver is called. The names are translated rather than stored, so a pack can rename the
     * folk who hand out work without touching a saved quest.
     */
    public static Component giverName(RandomSource random) {
        return Component.translatable(giverKey(random));
    }

    /**
     * The same name as the key it is translated from, for a quest to keep. Storing the key rather than
     * the words means the book reads the name in whatever language its reader uses.
     */
    public static String giverKey(RandomSource random) {
        return "quest.tolkienmobs.name." + random.nextInt(NAMES);
    }

    /** Every item id a fetch quest can ask for, for the command that rolls one by hand. */
    public static List<String> fetchTargets() {
        return FETCH;
    }
}
