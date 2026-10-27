package com.greatorator.tolkienmobs;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The mod's settings, in one file, split into what the server owns and what the client owns.
 *
 * <p>The split is by who reads the value, not by who can change it. Everything under
 * {@code [server]} decides what happens in the world - what spawns, what can be crafted, what the
 * milestones charge - so on a dedicated server it is that server's copy of the file that governs
 * play. Everything under {@code [client]} is read by the client that has it open: the two fell
 * beast wind volumes are heard by each listener from their own settings, so a player can turn them
 * down without touching the server's file.
 */
@EventBusSubscriber(modid = TolkienMobsMain.MODID)
public class TolkienMobsConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /** The mod's own namespace, which is what marks an id as one of ours. */
    private static final String MODID = TolkienMobsMain.MODID;

    /* Server: the world's own settings. */
    private static final ModConfigSpec.BooleanValue DISABLE_ALL_MOBS;
    private static final ModConfigSpec.BooleanValue DISABLE_ALL_BLOCKS;
    private static final ModConfigSpec.BooleanValue DISABLE_ALL_ITEMS;
    private static final ModConfigSpec.BooleanValue FAKE_PLAYER;
    private static final ModConfigSpec.BooleanValue AUTO_USE;
    private static final ModConfigSpec.BooleanValue HOBBIT_GROWTH;
    private static final ModConfigSpec.IntValue PICKUP_RANGE;
    private static final ModConfigSpec.IntValue GROWTH_RANGE;
    private static final ModConfigSpec.IntValue GROW_TIMER;
    private static final ModConfigSpec.IntValue TRINKET_REPAIR_AMOUNT;
    private static final ModConfigSpec.IntValue REGION_WEIGHT;

    /* Client: the settings a listener keeps for themselves. */
    private static final ModConfigSpec.DoubleValue FELLBEAST_DIVE_WIND_VOLUME;
    private static final ModConfigSpec.DoubleValue FELLBEAST_DIVE_WIND_PITCH;

    /* Server: the backpack's magnet, the camo spawner's defaults and the two crafting times. */
    private static final ModConfigSpec.IntValue MAGNET_IRON_RANGE;
    private static final ModConfigSpec.IntValue MAGNET_GOLD_RANGE;
    private static final ModConfigSpec.IntValue MAGNET_MITHRIL_RANGE;
    private static final ModConfigSpec.IntValue SPAWNER_ACTIVATION_RANGE;
    private static final ModConfigSpec.IntValue SPAWNER_SPAWN_RANGE;
    private static final ModConfigSpec.IntValue SPAWNER_SPAWN_COUNT;
    private static final ModConfigSpec.IntValue SPAWNER_MAX_CLUSTER;
    private static final ModConfigSpec.IntValue SPAWNER_CLUSTER_RANGE;
    private static final ModConfigSpec.IntValue SPAWNER_MIN_DELAY;
    private static final ModConfigSpec.IntValue SPAWNER_MAX_DELAY;
    private static final ModConfigSpec.IntValue TRINKET_CRAFT_TIME;
    private static final ModConfigSpec.IntValue FIREPLACE_CRAFT_TIME;

    /* Server: the milestone and the fell beast, which read their own copies of these. */
    public static final ModConfigSpec.IntValue MINIMUM_TICK_SPEED;
    public static final ModConfigSpec.IntValue DISTANCE_COST;
    public static final ModConfigSpec.IntValue DIMENSION_COST;
    public static final ModConfigSpec.BooleanValue PAYMENT_TYPE;
    public static final ModConfigSpec.ConfigValue<String> PAYMENT_ITEM;
    private static final ModConfigSpec.IntValue HOME_RADIUS;
    private static final ModConfigSpec.DoubleValue FELLBEAST_WINGBEAT_VOLUME;
    private static final ModConfigSpec.IntValue FELLBEAST_WINGBEAT_INTERVAL;
    private static final ModConfigSpec.IntValue FELLBEAST_DIVE_ANGLE;

    /* The headings the switches are grouped under, which are the mod's own families of mobs. */
    private static final String AMBIENT = "Ambient mobs";
    private static final String PASSIVE = "Passive mobs";
    private static final String NPCS = "NPCs";
    private static final String HOSTILE = "Hostile mobs";
    private static final String BOSSES = "Boss mobs";

    /**
     * Every mob the mod registers, in the order it registers them: the config key, the entity id path
     * the switch stands for, the name its comment shows, and the group of mobs its comment sits under.
     *
     * <p>The table is written out rather than read off the entity registry, because the file is built
     * while the mod is still being constructed - the mod's entities are not in that registry yet, and
     * a display name would have to come from a language file that is not loaded this early either.
     * The log names any mob that has no line here, so the table and the entities cannot drift apart
     * unnoticed.
     */
    private static final List<MobToggle> MOB_TOGGLES = List.of(
            new MobToggle("enableGecko", "entityttmgecko", "Gecko", AMBIENT),
            new MobToggle("enableRat", "entityttmrat", "Rat", AMBIENT),
            new MobToggle("enableSquirrel", "entityttmsquirrel", "Squirrel", AMBIENT),
            new MobToggle("enableFrog", "entityttmfrog", "Frog", AMBIENT),
            new MobToggle("enableThrush", "entityttmthrush", "Thrush", AMBIENT),
            new MobToggle("enableCrebain", "entityttmcrebain", "Crebain", AMBIENT),
            new MobToggle("enableMidgeFly", "entityttmswarm", "MidgeFly", AMBIENT),
            new MobToggle("enableGreatEagle", "entityttmgreateagle", "Great Eagle", AMBIENT),
            new MobToggle("enableAuroch", "entityttmauroch", "Auroch", PASSIVE),
            new MobToggle("enableMumakil", "entityttmmumakil", "Mumakil", PASSIVE),
            new MobToggle("enableBattleGoat", "entityttmgoat", "Battle Goat", PASSIVE),
            new MobToggle("enableNazgulSteed", "entityttmnazgulsteed", "Nazgul Steed", PASSIVE),
            new MobToggle("enableShadowfax", "entityttmshadowfax", "Shadowfax", PASSIVE),
            new MobToggle("enableGollum", "entityttmgollum", "Gollum", PASSIVE),
            new MobToggle("enableIstari", "entityttmistari", "Istari", PASSIVE),
            new MobToggle("enableHuman", "entityttmhuman", "Human", NPCS),
            new MobToggle("enableDwarf", "entityttmdwarf", "Dwarf", NPCS),
            new MobToggle("enableElf", "entityttmelves", "Elf", NPCS),
            new MobToggle("enableHobbit", "entityttmhobbit", "Hobbit", NPCS),
            new MobToggle("enableSouthronTrader", "entityttmsouthron", "Southron trader", NPCS),
            new MobToggle("enableOrcTrader", "entityttmorc_trader", "Orc Trader", NPCS),
            new MobToggle("enableOrc", "entityttmorc", "Orc", NPCS),
            new MobToggle("enableSouthron", "entityttmsouthron_npc", "Southron villager", NPCS),
            new MobToggle("enableBarrowWight", "entityttmbarrow", "Barrow Wight", HOSTILE),
            new MobToggle("enableOathBreaker", "entityttmoathbreaker", "Oath Breaker", HOSTILE),
            new MobToggle("enableFellSpirit", "entityttmfellspirit", "Fell Spirit", HOSTILE),
            new MobToggle("enableEnragedFellSpirit", "entityttmenragedfellspirit", "Enraged Fell Spirit", HOSTILE),
            new MobToggle("enableBrigand", "entityttmbrigand", "Brigand", HOSTILE),
            new MobToggle("enableHaradrim", "entityttmharadrim", "Haradrim", HOSTILE),
            new MobToggle("enableRomieWalker", "entityttmromiewalker", "Romie Walker", HOSTILE),
            new MobToggle("enableMimicChest", "entityttmmimicchest", "Mimic Chest", HOSTILE),
            new MobToggle("enableMordorOrc", "entityttmmordororc", "Mordor Orc", HOSTILE),
            new MobToggle("enableUrukHai", "entityttmurukhai", "Uruk Hai", HOSTILE),
            new MobToggle("enableDuergar", "entityttmduergar", "Duergar", HOSTILE),
            new MobToggle("enableGoblin", "entityttmgoblin", "Goblin", HOSTILE),
            new MobToggle("enableMirkwoodSpider", "entityttmmirkwoodspider", "Mirkwood Spider", HOSTILE),
            new MobToggle("enableRockGolem", "entityttmrockgolem", "Rock Golem", HOSTILE),
            new MobToggle("enableHuron", "entityttmhuron", "Huron", HOSTILE),
            new MobToggle("enableMinotaur", "entityttmminotaur", "Minotaur", HOSTILE),
            new MobToggle("enableDeepclaw", "entityttmdeepclaw", "Deepclaw", HOSTILE),
            new MobToggle("enableCaveTroll", "entityttmtroll", "Cave Troll", HOSTILE),
            new MobToggle("enableTreeEnt", "entityttmtreeent", "Tree Ent", HOSTILE),
            new MobToggle("enableSwampHag", "entityttmswamphag", "Swamp Hag", HOSTILE),
            new MobToggle("enableWarg", "entityttmwarg", "Warg", HOSTILE),
            new MobToggle("enableElementalGolem", "entityttmelementalgolem", "Elemental Golem", HOSTILE),
            new MobToggle("enableNazgul", "entityttmnazgul", "Nazgul", HOSTILE),
            new MobToggle("enableGoblinKing", "entityttmgoblinking", "Goblin King", BOSSES),
            new MobToggle("enableBalrog", "entityttmbalrog", "Balrog", BOSSES),
            new MobToggle("enableShelob", "entityttmshelob", "Shelob", BOSSES),
            new MobToggle("enableWitchKing", "entityttmwitchking", "Witch King of Angmar", BOSSES),
            new MobToggle("enableWatcher", "entityttmwatcher", "Watcher of the Water", BOSSES),
            new MobToggle("enableMithrilGolem", "entityttmmithrilgolem", "Mithril Golem", BOSSES),
            new MobToggle("enableMorgulIronGolem", "entityttmmorgulirongolem", "Morgul Iron Golem", BOSSES),
            new MobToggle("enableGwahir", "entityttmgwahir", "Gwahir, Lord of the Eagles", BOSSES),
            new MobToggle("enableFellBeast", "entityttmfellbeast", "Fell Beast", BOSSES));

    /** The switches, in the order the file lists them, each beside the entity id it stands for. */
    private static final List<MobSwitch> MOB_SWITCHES = new ArrayList<>();

    static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("What the world does. A dedicated server reads its own copy of these; the values here",
                        "are the ones in play for every player on it.")
                .push("server");

        // The content switches. Nothing can be pulled back out of the registries once the game is
        // running, so these act on the paths that hand content out: a mob is refused where it enters
        // the world, and a block or an item is taken out of the creative tabs and the recipes.
        BUILDER.comment("Whether the mod's mobs may enter the world at all. The master switch comes first, then one",
                        "switch for each of the mod's mobs, grouped as the mod registers them: true lets a mob in as",
                        "it always did, false keeps that one mob out. The master overrides them all.")
                .push("mobs");
        DISABLE_ALL_MOBS = BUILDER
                .comment("Stops every Tolkien mob from entering the world: they no longer spawn, a spawn egg for",
                        "one does nothing, and summoning one is refused. Mobs already in a world stay where they",
                        "are until they leave it, so this is not a way to clear a world of them. This switch",
                        "overrides every per-mob switch below it (Default false)")
                .define("disableAllMobs", false);
        // Each family of mobs gets a heading of its own, written as the first line of the comment on the
        // first switch below it - the file has no other way to carry a comment that belongs to no entry.
        String group = "";
        for (MobToggle mob : MOB_TOGGLES) {
            List<String> lines = new ArrayList<>(2);
            if (!mob.group().equals(group)) {
                group = mob.group();
                lines.add(group);
            }
            lines.add("The " + mob.label() + " (`" + MODID + ":" + mob.path() + "`)");
            MOB_SWITCHES.add(new MobSwitch(mob.path(),
                    BUILDER.comment(lines.toArray(String[]::new)).define(mob.key(), true)));
        }
        BUILDER.pop();

        BUILDER.comment("Whether the mod hands out the blocks it adds.").push("blocks");
        DISABLE_ALL_BLOCKS = BUILDER
                .comment("Takes the mod's blocks out of the creative tabs and stops the recipes that make them",
                        "from making them. Blocks already in a world, in a chest or in a player's hands stay as",
                        "they are, and the recipes that produce the mod's items from its blocks still work",
                        "(Default false)")
                .define("disableAllBlocks", false);
        BUILDER.pop();

        BUILDER.comment("Whether the mod hands out the items it adds.").push("items");
        DISABLE_ALL_ITEMS = BUILDER
                .comment("The same for every item the mod adds, block items included. This covers the mod's own",
                        "recipes and any other pack's recipe that produces one of its items (Default false)")
                .define("disableAllItems", false);
        BUILDER.pop();

        BUILDER.comment("The mod's own rules for how often things happen.").push("gameplay");
        FAKE_PLAYER = BUILDER
                .comment("Skip fake players, such as deployers, when Hallowed Earth damages the undead, so a farm built on it is not eaten by its own machine (Default true)")
                .define("disableFakePlayer", true);
        AUTO_USE = BUILDER
                .comment("If enabled, players automatically attempt to use sleeping bags when placed.")
                .define("autoUse", true);
        HOBBIT_GROWTH = BUILDER
                .comment("Turns off abilities for the Hobbit Growth Ring if enabled")
                .define("disableHobbitGrowth", false);
        PICKUP_RANGE = BUILDER
                .comment("Range of pickup for Hobbit Ring")
                .defineInRange("pickupRange", 7, 0, Integer.MAX_VALUE);
        GROWTH_RANGE = BUILDER
                .comment("Range of growth for Hobbit Ring")
                .defineInRange("growthRange", 4, 0, Integer.MAX_VALUE);
        GROW_TIMER = BUILDER
                .comment("How often the Hobbit Ring will try to grow a block (in ticks)")
                .defineInRange("growthTimer", 20, 0, Integer.MAX_VALUE);
        TRINKET_REPAIR_AMOUNT = BUILDER
                .comment("Damage the trinket table undoes for one ammolite gem and one repair material (Default 1000)")
                .defineInRange("trinketRepairAmount", 1000, 1, 1000000);
        TRINKET_CRAFT_TIME = BUILDER
                .comment("Ticks the trinket table takes to make a trinket, twenty to the second. A table already in a",
                        "world keeps the number it saved (Default 100, or five seconds)")
                .defineInRange("trinketCraftTime", 100, 1, 2000);
        FIREPLACE_CRAFT_TIME = BUILDER
                .comment("Ticks the fireplace takes to cook one thing, twenty to the second. A fireplace in a world",
                        "picks this up the next time it loads (Default 100, or five seconds)")
                .defineInRange("fireplaceCookTime", 100, 1, 2000);
        BUILDER.pop();

        BUILDER.comment("How far the backpack's magnet reaches at each tier. The best magnet fitted sets the reach,",
                        "and the magnet items' own lore states these numbers (Default: 16, 32 and 64)").push("backpack");
        MAGNET_IRON_RANGE = BUILDER
                .comment("Reach of the iron magnet, in blocks, which is the least a magnet pulls (Default 16)")
                .defineInRange("magnetIronRange", 16, 1, 128);
        MAGNET_GOLD_RANGE = BUILDER
                .comment("Reach of the gold magnet, in blocks (Default 32)")
                .defineInRange("magnetGoldRange", 32, 1, 128);
        MAGNET_MITHRIL_RANGE = BUILDER
                .comment("Reach of the mithril magnet, in blocks, which is the most a magnet pulls (Default 64)")
                .defineInRange("magnetMithrilRange", 64, 1, 128);
        BUILDER.pop();

        BUILDER.comment("The numbers a freshly placed camo spawner starts with, which are vanilla's own. Each spawner",
                        "keeps its own copy and its screen can change them; the screen offers these same ranges, so a",
                        "value set here stays editable in game").push("spawner");
        SPAWNER_ACTIVATION_RANGE = BUILDER
                .comment("How close a player has to be for the spawner to run, in blocks, when it is set to need one (Default 16)")
                .defineInRange("activationRange", 16, 1, 32);
        SPAWNER_SPAWN_RANGE = BUILDER
                .comment("How far from itself the spawner may place a mob, in blocks (Default 4)")
                .defineInRange("spawnRange", 4, 1, 32);
        SPAWNER_SPAWN_COUNT = BUILDER
                .comment("How many mobs one spawn attempt makes, at most (Default 4)")
                .defineInRange("spawnCount", 4, 1, 16);
        SPAWNER_MAX_CLUSTER = BUILDER
                .comment("How many of the same mob may stand near the spawner before it stops spawning (Default 6)")
                .defineInRange("maxCluster", 6, 1, 16);
        SPAWNER_CLUSTER_RANGE = BUILDER
                .comment("How far from the spawner that count is taken, in blocks (Default 16)")
                .defineInRange("clusterRange", 16, 1, 32);
        SPAWNER_MIN_DELAY = BUILDER
                .comment("Shortest wait between two spawn attempts, in ticks (Default 200, or ten seconds)")
                .defineInRange("minSpawnDelay", 200, 20, 200);
        SPAWNER_MAX_DELAY = BUILDER
                .comment("Longest wait between two spawn attempts, in ticks, picked at random within this and the",
                        "shortest above (Default 800, or forty seconds)")
                .defineInRange("maxSpawnDelay", 800, 40, 800);
        BUILDER.pop();

        BUILDER.comment("The keystone's timers and what a milestone charges for fast travel.").push("milestone");
        MINIMUM_TICK_SPEED = BUILDER
                .comment("Minimum speed anything that ticks can be set to. Defaults to 1, or every tick")
                .defineInRange("minimumTickSpeed", 1, 1, 100);
        DISTANCE_COST = BUILDER
                .comment("Minimum Default cost for fast-travel (Default 500")
                .defineInRange("distanceCost", 500, 500, 5000);
        DIMENSION_COST = BUILDER
                .comment("Lowest cost the milestone's dimension button allows for fast-travel across dimensions (Default 1)")
                .defineInRange("dimensionCost", 1, 1, 10);
        PAYMENT_TYPE = BUILDER
                .comment("How milestones charge for fast-travel: true costs the item below, false costs experience levels (Default false, experience)")
                .define("paymentType", false);
        PAYMENT_ITEM = BUILDER
                .comment("Item to use for payment to teleport")
                .define("paymentItem", "tolkienmobs:item_coin_bronze", TolkienMobsConfig::validateItemName);
        BUILDER.pop();

        BUILDER.comment("How the fell beast flies and how loud it is heard by the players near it.").push("fellbeast");
        HOME_RADIUS = BUILDER
                .comment("How far the FellBeast will roam from home (Default 32, no less than 8)")
                .defineInRange("homeRadius", 32, 8, 128);
        FELLBEAST_WINGBEAT_VOLUME = BUILDER
                .comment("Volume of the FellBeast's wingbeat, before the beast's own sound scale. The default 2.0 is 3.0 in game, where the beast's idle voice plays at 1.5 (Default 2.0)")
                .defineInRange("wingbeatVolume", 2.0, 0.0, 10.0);
        FELLBEAST_WINGBEAT_INTERVAL = BUILDER
                .comment("Shortest gap between two heard wingbeats as the FellBeast flies, in ticks. 20 ticks is one second. The flap clip is half a second, so anything under 10 runs two beats into each other, and a beast holding its path beats about every 26 ticks (Default 15)")
                .defineInRange("wingbeatInterval", 15, 1, 200);
        FELLBEAST_DIVE_ANGLE = BUILDER
                .comment("How steeply a ridden FellBeast must dive, in degrees below horizontal, before its wingbeat goes quiet (Default 45)")
                .defineInRange("diveAngle", 45, 15, 90);
        BUILDER.pop();

        BUILDER.comment("How much of a new world belongs to the mod's region.").push("worldgen");
        REGION_WEIGHT = BUILDER
                .comment("The mod's region beside vanilla's, which TerraBlender weighs at 50, so the mod's share",
                        "of the overworld is weight / (weight + 50): 7 is about 12%, and 1 is about 2%. The region",
                        "is built into the biome source a world is created with, so a change here only shows in a",
                        "world generated after the change (Default 7)")
                .defineInRange("tolkienRegionWeight", 7, 1, 50);
        BUILDER.pop();

        BUILDER.pop();

        BUILDER.comment("What this client does for itself. These are read by whichever client has the file,",
                        "so a player can change them without asking the server.")
                .push("client");

        FELLBEAST_DIVE_WIND_VOLUME = BUILDER
                .comment("Volume of the wind over a diving FellBeast. It is air rather than a voice, so unlike the wingbeat it is not scaled by the beast's own sound scale (Default 1.6)")
                .defineInRange("fellbeastDiveWindVolume", 1.6, 0.0, 10.0);
        FELLBEAST_DIVE_WIND_PITCH = BUILDER
                .comment("Pitch of the wind over a diving FellBeast. The loop is recorded at the depth the dive is meant to play at, so 1.0 is the wind as intended; below it the wind is deeper and slower, above it sharper and quicker (Default 1.0)")
                .defineInRange("fellbeastDiveWindPitch", 1.0, 0.5, 2.0);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static boolean disableFakePlayer;
    public static boolean autoUse;
    public static boolean disableHobbitGrowth;
    public static boolean paymentType;
    public static boolean disableAllBlocks;
    public static boolean disableAllItems;

    public static int distanceCost;
    public static int dimensionCost;
    public static int pickupRange;
    public static int growthRange;
    public static int growthTimer;
    public static int homeRadius;
    public static double fellbeastWingbeatVolume;
    public static double fellbeastDiveWindVolume;
    public static double fellbeastDiveWindPitch;
    public static int fellbeastWingbeatInterval;
    public static int fellbeastDiveAngle;
    public static int trinketRepairAmount;

    /** The entity types a spawn attempt is refused for; rebuilt whenever the config is read. */
    private static Set<EntityType<?>> disabledMobs = Set.of();

    /** Whether a mob of this type may enter the world, as the switches stand. */
    public static boolean isMobDisabled(EntityType<?> type) {
        return !disabledMobs.isEmpty() && disabledMobs.contains(type);
    }

    /**
     * Whether the creative tabs and the recipe book should leave this item out. A block item of the
     * mod's answers to either switch: {@code disableAllItems} covers everything the mod adds, and
     * {@code disableAllBlocks} covers the ones that place a block.
     */
    public static boolean isItemHidden(Item item) {
        if (item == null || item == Items.AIR || (!disableAllItems && !disableAllBlocks)) {
            return false;
        }
        Identifier name = BuiltInRegistries.ITEM.getKey(item);
        if (name == null || !MODID.equals(name.getNamespace())) {
            return false;
        }
        return disableAllItems || item instanceof BlockItem;
    }
    public static int tolkienRegionWeight() {
        return SPEC.isLoaded() ? REGION_WEIGHT.get() : 7;
    }

    /** How far the magnet of a tier reaches, as configured: 1 iron, 2 gold, 3 mithril, 0 for no magnet. */
    public static int magnetRange(int tier) {
        return switch (tier) {
            case 3 -> value(MAGNET_MITHRIL_RANGE, 64);
            case 2 -> value(MAGNET_GOLD_RANGE, 32);
            case 1 -> value(MAGNET_IRON_RANGE, 16);
            default -> 0;
        };
    }

    /** The furthest any magnet reaches, which is how wide a sweep for drops has to look. */
    public static int magnetMaxRange() {
        return Math.max(magnetRange(1), Math.max(magnetRange(2), magnetRange(3)));
    }

    public static int spawnerActivationRange() {
        return value(SPAWNER_ACTIVATION_RANGE, 16);
    }

    public static int spawnerSpawnRange() {
        return value(SPAWNER_SPAWN_RANGE, 4);
    }

    public static int spawnerSpawnCount() {
        return value(SPAWNER_SPAWN_COUNT, 4);
    }

    public static int spawnerMaxCluster() {
        return value(SPAWNER_MAX_CLUSTER, 6);
    }

    public static int spawnerClusterRange() {
        return value(SPAWNER_CLUSTER_RANGE, 16);
    }

    public static int spawnerMinDelay() {
        return value(SPAWNER_MIN_DELAY, 200);
    }

    /** The longest wait, never under the shortest one, whatever the two settings say. */
    public static int spawnerMaxDelay() {
        return Math.max(spawnerMinDelay(), value(SPAWNER_MAX_DELAY, 800));
    }

    public static int trinketCraftTime() {
        return value(TRINKET_CRAFT_TIME, 100);
    }

    public static int fireplaceCookTime() {
        return value(FIREPLACE_CRAFT_TIME, 100);
    }

    /** A setting's own value once the file has been read, or the default it was defined with before that. */
    private static int value(ModConfigSpec.IntValue setting, int fallback) {
        return SPEC.isLoaded() ? setting.get() : fallback;
    }

    private static boolean validateItemName(final Object obj) {
        // 26.1 validates a spec's default as the spec is built, which happens while the mod is still
        // being constructed - the mod's own coins are not in the item registry yet, so looking the name
        // up rejected the default. The check is that the name is a well-formed item id; a name no item
        // answers to leaves the milestone unable to charge, because the item registry hands back air.
        return obj instanceof String itemName && Identifier.tryParse(itemName) != null;
    }

    /**
     * One mob's switch: the config key, the entity id path it stands for - the part after the mod's
     * own namespace, which is added when the id is resolved - the name its comment shows, and the
     * heading the comment is grouped under.
     */
    private record MobToggle(String key, String path, String label, String group) {
    }

    /** A built switch beside the entity id it stands for. */
    private record MobSwitch(String path, ModConfigSpec.BooleanValue value) {
    }

    /**
     * Names every mob the mod registers that has no switch of its own. The table above is written by
     * hand, so this is what keeps it and the entities from drifting apart: a mob added without a line
     * there says so in the log instead of being a mob no config can switch off.
     */
    private static void warnOnMobsWithoutSwitch() {
        Set<String> switched = new HashSet<>();
        for (MobToggle mob : MOB_TOGGLES) {
            switched.add(MODID + ":" + mob.path());
        }
        for (String id : tolkienMobIds()) {
            if (!switched.contains(id)) {
                LOGGER.warn("The mod's mob `{}` has no switch in the config; add a MobToggle line for it", id);
            }
        }
    }

    /** The entity id of every mob the mod registers, read off the spawn egg that goes with it. */
    private static List<String> tolkienMobIds() {
        List<String> ids = new ArrayList<>();
        for (DeferredHolder<Item, ? extends Item> egg : TolkienEntities.SPAWN_EGGS.getEntries()) {
            String path = egg.getKey().identifier().getPath();
            if (path.endsWith(EGG_SUFFIX)) {
                ids.add(MODID + ":" + path.substring(0, path.length() - EGG_SUFFIX.length()));
            }
        }
        return ids;
    }

    private static final String EGG_SUFFIX = "_spawn_egg";

    /**
     * The entity types this game keeps out of the world: every mob the mod adds while the master
     * switch is on, and otherwise the ones whose own switch was turned off.
     *
     * <p>The entity registry is a defaulted one - 26.1 answers an unknown id with its default entry
     * rather than with nothing - so every id is checked for membership first. Looking one up without
     * that check would hand back a real entity type for a name that matches nothing, and quietly
     * switch off whichever mob happens to be the registry's default.
     */
    private static Set<EntityType<?>> resolveDisabledMobs(boolean all) {
        Set<EntityType<?>> disabled = new LinkedHashSet<>();
        for (MobSwitch mob : MOB_SWITCHES) {
            if (!all && mob.value().get()) {
                continue;
            }
            // The table holds the path alone; the namespace has to be put back on, because a bare path
            // parses as `minecraft:` and every one of these mobs is one of ours.
            EntityType<?> type = entityType(MODID + ":" + mob.path());
            if (type == null) {
                LOGGER.warn("The config's switch for `{}` names an entity this game does not have; it does nothing", mob.path());
            } else {
                disabled.add(type);
            }
        }
        return disabled;
    }

    /** The entity type an id names, or null if this game has no entity by that name. */
    private static EntityType<?> entityType(String id) {
        Identifier name = Identifier.tryParse(id);
        return name != null && BuiltInRegistries.ENTITY_TYPE.containsKey(name)
                ? BuiltInRegistries.ENTITY_TYPE.getValue(name) : null;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        disableFakePlayer = FAKE_PLAYER.get();
        autoUse = AUTO_USE.get();
        disableHobbitGrowth = HOBBIT_GROWTH.get();
        pickupRange = PICKUP_RANGE.get();
        growthRange = GROWTH_RANGE.get();
        growthTimer = GROW_TIMER.get();
        homeRadius = HOME_RADIUS.get();
        fellbeastWingbeatVolume = FELLBEAST_WINGBEAT_VOLUME.get();
        fellbeastDiveWindVolume = FELLBEAST_DIVE_WIND_VOLUME.get();
        fellbeastDiveWindPitch = FELLBEAST_DIVE_WIND_PITCH.get();
        fellbeastWingbeatInterval = FELLBEAST_WINGBEAT_INTERVAL.get();
        fellbeastDiveAngle = FELLBEAST_DIVE_ANGLE.get();
        trinketRepairAmount = TRINKET_REPAIR_AMOUNT.get();
        distanceCost = DISTANCE_COST.get();
        dimensionCost = DIMENSION_COST.get();
        paymentType = PAYMENT_TYPE.get();

        boolean disableAllMobs = DISABLE_ALL_MOBS.get();
        disableAllBlocks = DISABLE_ALL_BLOCKS.get();
        disableAllItems = DISABLE_ALL_ITEMS.get();
        warnOnMobsWithoutSwitch();
        disabledMobs = resolveDisabledMobs(disableAllMobs);
        // The two content switches are read straight from the spec - the tabs and the recipes only ask
        // about an item, so they call `isItemHidden` rather than keeping a list of their own.
        if (disableAllBlocks || disableAllItems || !disabledMobs.isEmpty()) {
            LOGGER.info("Tolkien content switches: {} mob(s) off{}, blocks={}, items={}",
                    disabledMobs.size(), disableAllMobs ? " (all of them, by the master switch)" : "",
                    disableAllBlocks ? "off" : "on", disableAllItems ? "off" : "on");
        }
    }
}
