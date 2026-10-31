package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Where the quests a giver offers come from.
 * <p>
 * Nothing here is a table of its own any more: the items, the mobs, the pay and every number a giver
 * uses live in the config, under {@code [server.quests]}, so a pack can put work to the content of its
 * own and tune what the work is worth without touching the mod. The pool only assembles a quest out of
 * them. Both kinds of target are strings, and a quest is saved with the target it was taken on, so a
 * change in the config reaches the next offer rather than the work already in a player's book.
 * <p>
 * Faction work is offered by a giver standing in a country, and pays standing with that country. The
 * rest is ordinary work - grain for a household, wolves out of a field - and pays goods and coin,
 * because a farmer has no standing to give away.
 */
public final class QuestPool {
    /** How many folk the mod ships, whose names are translated rather than written into the config. */
    private static final int SHIPPED_GIVERS = 8;

    private QuestPool() {
    }

    /**
     * Fresh work for whoever is standing in front of the giver.
     *
     * @param employer the country the giver stands in, or null when they stand in no country at all
     */
    public static Quest offer(RandomSource random, @Nullable Alliance employer) {
        if (employer != null && random.nextFloat() < TolkienMobsConfig.questFactionChance()) {
            return faction(random, employer);
        }
        return random.nextBoolean() ? fetch(random) : hunt(random);
    }

    /** Work for a country: its rivals' soldiers are the target, and the pay is standing. */
    private static Quest faction(RandomSource random, Alliance employer) {
        List<Alliance> rivals = employer.rivals();
        Alliance target = rivals.get(random.nextInt(rivals.size()));
        int goal = between(random, TolkienMobsConfig.questFactionGoalMin(), TolkienMobsConfig.questFactionGoalMax());
        // Paid at more than the kills themselves are worth, because the giver is asking on purpose.
        int standing = TolkienMobsConfig.questStandingBase() + goal * TolkienMobsConfig.questStandingPerGoal();
        return new Quest(Quest.Kind.FACTION, target.id(), goal, 0, employer.id(), standing, List.of(), "", BlockPos.ZERO, Quest.NOBODY);
    }

    private static Quest fetch(RandomSource random) {
        List<String> items = TolkienMobsConfig.questFetchItems();
        String item = items.get(random.nextInt(items.size()));
        int goal = between(random, TolkienMobsConfig.questFetchGoalMin(), TolkienMobsConfig.questFetchGoalMax());
        return new Quest(Quest.Kind.FETCH, item, goal, 0, "", 0, payment(random, goal), "", BlockPos.ZERO, Quest.NOBODY);
    }

    private static Quest hunt(RandomSource random) {
        List<String> mobs = TolkienMobsConfig.questHuntMobs();
        String mob = mobs.get(random.nextInt(mobs.size()));
        int goal = between(random, TolkienMobsConfig.questHuntGoalMin(), TolkienMobsConfig.questHuntGoalMax());
        return new Quest(Quest.Kind.HUNT, mob, goal, 0, "", 0, payment(random, goal), "", BlockPos.ZERO, Quest.NOBODY);
    }

    /** Coin, and sometimes a thing worth carrying: the harder the work, the more of both. */
    private static List<Quest.Reward> payment(RandomSource random, int goal) {
        List<Quest.Reward> paid = new ArrayList<>(2);
        int share = TolkienMobsConfig.questCoinSpread()
                + goal / Math.max(1, TolkienMobsConfig.questCoinPerGoal());
        paid.add(new Quest.Reward(TolkienMobsConfig.questCoinItem(),
                TolkienMobsConfig.questCoinBase() + random.nextInt(Math.max(1, share))));

        List<String> goods = TolkienMobsConfig.questRewardItems();
        if (!goods.isEmpty() && random.nextFloat() < TolkienMobsConfig.questGoodsChance()) {
            int low = TolkienMobsConfig.questGoodsMin();
            int high = TolkienMobsConfig.questGoodsMax();
            paid.add(new Quest.Reward(goods.get(random.nextInt(goods.size())), low + random.nextInt(high - low + 1)));
        }
        return List.copyOf(paid);
    }

    /** A goal between two settings, both ends counted, safe against a pack that set them the wrong way round. */
    private static int between(RandomSource random, int low, int high) {
        int min = Math.max(1, low);
        int max = Math.max(min, high);
        return min + random.nextInt(max - min + 1);
    }

    /**
     * What a giver is called: one name and one title, drawn separately and joined with a space. Both
     * tables are in the config, so a pack names its own folk; with both left empty the mod's own eight
     * are used instead, and those are translated rather than written, which is why they are kept as the
     * keys the client resolves.
     */
    public static String giverLabel(RandomSource random) {
        List<String> names = TolkienMobsConfig.questGiverNames();
        List<String> titles = TolkienMobsConfig.questGiverTitles();
        if (names.isEmpty() && titles.isEmpty()) {
            return shippedName(random);
        }
        String name = names.isEmpty() ? "" : names.get(random.nextInt(names.size()));
        String title = titles.isEmpty() ? "" : titles.get(random.nextInt(titles.size()));
        String label = (name + " " + title).trim();
        // A pack whose tables held nothing but blanks still leaves someone standing there.
        return label.isEmpty() ? shippedName(random) : label;
    }

    /** The giver's name as the game draws it, whether it was written in the config or comes from a key. */
    public static Component giverName(RandomSource random) {
        return Quest.label(giverLabel(random));
    }

    private static String shippedName(RandomSource random) {
        return "quest.tolkienmobs.name." + random.nextInt(SHIPPED_GIVERS);
    }
}
