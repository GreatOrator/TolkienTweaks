package com.greatorator.tolkienmobs;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = TolkienMobsMain.MODID, bus = EventBusSubscriber.Bus.MOD)
public class TolkienMobsConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /* Client */
    /* Server */
    private static final ModConfigSpec.BooleanValue FAKE_PLAYER = BUILDER
            .comment("Disable fake player for Hallowed Earth (Default false)")
            .define("disableFakePlayer", true);
    private static final ModConfigSpec.BooleanValue AUTO_USE = BUILDER
            .comment("If enabled, players automatically attempt to use sleeping bags when placed.")
            .define("autoUse", true);
    private static final ModConfigSpec.BooleanValue HOBBIT_GROWTH = BUILDER
            .comment("Turns off abilities for the Hobbit Growth Ring if enabled")
            .define("disableHobbitGrowth", false);
//    private static final ModConfigSpec.EnumValue<ComfortsTimeUse> SLEEPING_BAG_TIME_USE = BUILDER
//            .comment("The time of day that sleeping bags can be used.")
//            .defineEnum("sleepingBagUse", ComfortsTimeUse.NIGHT);

    public static ModConfigSpec.IntValue MINIMUM_TICK_SPEED = BUILDER
            .comment("Minimum speed anything that ticks can be set to. Defaults to 1, or every tick")
            .defineInRange("minimum_tick_speed", 1, 1, 100);
    public static ModConfigSpec.IntValue BLOCKS_PER_LEVEL = BUILDER
            .comment("Range per level for Dwarven Mining Enchant.  Default is 1 (3x3)")
            .defineInRange("minimum_tick_speed", 9, 9, 81);

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
            .comment("How far the FellBeast will roam from home (Default 32)")
            .defineInRange("homeRadius", 32, 32, 128);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), TolkienMobsConfig::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean disableFakePlayer;
    public static boolean autoUse;
    public static boolean disableHobbitGrowth;
    public enum sleepingBagUse {}

    public static int pickupRange;
    public static int growthRange;
    public static int growthTimer;
    public static int homeRadius;
    public static String magicNumberIntroduction;
    public static Set<Item> items;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        disableFakePlayer = FAKE_PLAYER.get();
        autoUse = AUTO_USE.get();
        disableHobbitGrowth = AUTO_USE.get();
        pickupRange = PICKUP_RANGE.get();
        growthRange = GROWTH_RANGE.get();
        growthTimer = GROW_TIMER.get();
        homeRadius = HOME_RADIUS.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

        // convert the list of strings into a set of items
        items = ITEM_STRINGS.get().stream()
                .map(itemName -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName)))
                .collect(Collectors.toSet());
    }
}
