package com.greatorator.tolkienmobs;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


@EventBusSubscriber(modid = TolkienMobsMain.MODID, bus = EventBusSubscriber.Bus.MOD)
public class TolkienMobsConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /* Client */
    /* Server */
    private static final ModConfigSpec.BooleanValue FAKE_PLAYER = BUILDER
            .comment("Skip fake players, such as deployers, when Hallowed Earth damages the undead, so a farm built on it is not eaten by its own machine (Default true)")
            .define("disableFakePlayer", true);
    private static final ModConfigSpec.BooleanValue AUTO_USE = BUILDER
            .comment("If enabled, players automatically attempt to use sleeping bags when placed.")
            .define("autoUse", true);
    private static final ModConfigSpec.BooleanValue HOBBIT_GROWTH = BUILDER
            .comment("Turns off abilities for the Hobbit Growth Ring if enabled")
            .define("disableHobbitGrowth", false);

    public static ModConfigSpec.IntValue MINIMUM_TICK_SPEED = BUILDER
            .comment("Minimum speed anything that ticks can be set to. Defaults to 1, or every tick")
            .defineInRange("minimum_tick_speed", 1, 1, 100);

    public static ModConfigSpec.IntValue DISTANCE_COST = BUILDER
            .comment("Minimum Default cost for fast-travel (Default 500")
            .defineInRange("distance_cost", 500, 500, 5000);
    public static ModConfigSpec.IntValue DIMENSION_COST = BUILDER
            .comment("Lowest cost the milestone's dimension button allows for fast-travel across dimensions (Default 1)")
            .defineInRange("dimension_cost", 1, 1, 10);
    public static final ModConfigSpec.BooleanValue PAYMENT_TYPE = BUILDER
            .comment("How milestones charge for fast-travel: true costs the item below, false costs experience levels (Default false, experience)")
            .define("paymentType", false);
    public static final ModConfigSpec.ConfigValue<String> PAYMENT_ITEM = BUILDER
            .comment("Item to use for payment to teleport")
            .define("item", "tolkienmobs:item_coin_bronze", TolkienMobsConfig::validateItemName);

    private static final ModConfigSpec.IntValue TRINKET_REPAIR_AMOUNT = BUILDER
            .comment("Damage the trinket table undoes for one ammolite gem and one repair material (Default 1000)")
            .defineInRange("trinket_repair_amount", 1000, 1, 1000000);

    private static final ModConfigSpec.IntValue PICKUP_RANGE = BUILDER
            .comment("Range of pickup for Hobbit Ring")
            .defineInRange("pickupRange", 7, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue GROWTH_RANGE = BUILDER
            .comment("Range of growth for Hobbit Ring")
            .defineInRange("growthRange", 4, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue GROW_TIMER = BUILDER
            .comment("How often the Hobbit Ring will try to grow a block (in ticks)")
            .defineInRange("growthTimer", 20, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue HOME_RADIUS = BUILDER
            .comment("How far the FellBeast will roam from home (Default 32, no less than 8)")
            .defineInRange("homeRadius", 32, 8, 128);

    private static final ModConfigSpec.DoubleValue FELLBEAST_WINGBEAT_VOLUME = BUILDER
            .comment("Volume of the FellBeast's wingbeat, before the beast's own sound scale. The default 2.0 is 3.0 in game, where the beast's idle voice plays at 1.5 (Default 2.0)")
            .defineInRange("fellbeastWingbeatVolume", 2.0, 0.0, 10.0);
    private static final ModConfigSpec.IntValue FELLBEAST_WINGBEAT_INTERVAL = BUILDER
            .comment("Shortest gap between two heard wingbeats as the FellBeast flies, in ticks. 20 ticks is one second. The flap clip is half a second, so anything under 10 runs two beats into each other, and a beast holding its path beats about every 26 ticks (Default 15)")
            .defineInRange("fellbeastWingbeatInterval", 15, 1, 200);
    private static final ModConfigSpec.DoubleValue FELLBEAST_DIVE_WIND_VOLUME = BUILDER
            .comment("Volume of the wind over a diving FellBeast. It is air rather than a voice, so unlike the wingbeat it is not scaled by the beast's own sound scale (Default 1.6)")
            .defineInRange("fellbeastDiveWindVolume", 1.6, 0.0, 10.0);
    private static final ModConfigSpec.DoubleValue FELLBEAST_DIVE_WIND_PITCH = BUILDER
            .comment("Pitch of the wind over a diving FellBeast. The loop is recorded at the depth the dive is meant to play at, so 1.0 is the wind as intended; below it the wind is deeper and slower, above it sharper and quicker (Default 1.0)")
            .defineInRange("fellbeastDiveWindPitch", 1.0, 0.5, 2.0);
    private static final ModConfigSpec.IntValue FELLBEAST_DIVE_ANGLE = BUILDER
            .comment("How steeply a ridden FellBeast must dive, in degrees below horizontal, before its wingbeat goes quiet (Default 45)")
            .defineInRange("fellbeastDiveAngle", 45, 15, 90);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean disableFakePlayer;
    public static boolean autoUse;
    public static boolean disableHobbitGrowth;
    public static boolean paymentType;

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

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(Identifier.parse(itemName));
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
    }
}
