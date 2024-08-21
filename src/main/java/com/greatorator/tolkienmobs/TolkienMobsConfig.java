package com.greatorator.tolkienmobs;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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
    private static final ModConfigSpec.BooleanValue STOP_PHANTOM = BUILDER
            .comment("If enabled, attempting to sleep in sleeping bags stops phantoms from spawning.")
            .define("sleepingBagsStopPhantoms", true);
    private static final ModConfigSpec.BooleanValue AUTO_USE = BUILDER
            .comment("If enabled, players automatically attempt to use sleeping bags when placed.")
            .define("autoUse", true);
    private static final ModConfigSpec.EnumValue<ComfortsTimeUse> SLEEPING_BAG_TIME_USE = BUILDER
            .comment("The time of day that sleeping bags can be used.")
            .defineEnum("sleepingBagUse", ComfortsTimeUse.NIGHT);

    private static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), TolkienMobsConfig::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean sleepingBagsStopPhantoms;
    public static boolean autoUse;
    public enum sleepingBagUse {};
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        sleepingBagsStopPhantoms = STOP_PHANTOM.get();
        autoUse = AUTO_USE.get();
        ComfortsTimeUse sleepingBagUse = SLEEPING_BAG_TIME_USE.get();
        magicNumber = MAGIC_NUMBER.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

        // convert the list of strings into a set of items
        items = ITEM_STRINGS.get().stream()
                .map(itemName -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName)))
                .collect(Collectors.toSet());
    }

    public enum ComfortsTimeUse {
        NONE(Component.translatable("block.comforts.no_sleep")),
        DAY(Component.translatable("block.comforts.hammock.no_sleep")),
        NIGHT(Component.translatable("block.minecraft.bed.no_sleep")),
        DAY_OR_NIGHT(Component.translatable("block.comforts.hammock.no_sleep.2"));

        private final Component message;

        ComfortsTimeUse(Component message) {
            this.message = message;
        }

        public Component getMessage() {
            return this.message;
        }
    }
}
