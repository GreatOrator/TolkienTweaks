package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.item.BaseItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    // Mithril
    public static final DeferredItem<Item> RAW_MITHRIL = ITEMS.register("raw_mithril",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DUST_MITHRIL = ITEMS.register("dust_mithril",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NUGGET_MITHRIL = ITEMS.register("nugget_mithril",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INGOT_MITHRIL = ITEMS.register("ingot_mithril",
            () -> new Item(new Item.Properties()));

    // Morgul Iron
    public static final DeferredItem<Item> RAW_MORGULIRON = ITEMS.register("raw_morguliron",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DUST_MORGULIRON = ITEMS.register("dust_morguliron",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NUGGET_MORGULIRON = ITEMS.register("nugget_morguliron",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INGOT_MORGULIRON = ITEMS.register("ingot_morguliron",
            () -> new Item(new Item.Properties()));

    // Gems
    public static final DeferredItem<Item> GEM_AMMOLITE = ITEMS.register("gem_ammolite",
            () -> new BaseItem(new Item.Properties().stacksTo(16)).setEffectOverride());

    // Quest
    public static final DeferredItem<Item> ITEM_BERYL = ITEMS.register("item_beryl",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_FORTRESSMAP = ITEMS.register("item_fortressmap",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WATCHERHEART = ITEMS.register("item_watcherheart",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_WATCHERHEART_CRACKED = ITEMS.register("item_watcherheart_cracked",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_KEYSTONE = ITEMS.register("item_keystone",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_DARKSADDLE = ITEMS.register("item_darksaddle",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_ARTIFACT = ITEMS.register("item_artifact",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_BLANKPAPER = ITEMS.register("item_blankpaper",
            () -> new BaseItem(new Item.Properties().stacksTo(12)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYARMOR = ITEMS.register("item_fancyarmor",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYCLOTH = ITEMS.register("item_fancycloth",
            () -> new BaseItem(new Item.Properties().stacksTo(3)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYHAMMER = ITEMS.register("item_fancyhammer",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYHELM = ITEMS.register("item_fancyhelm",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYKEY = ITEMS.register("item_fancykey",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYPICK = ITEMS.register("item_fancypick",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYSHIELD = ITEMS.register("item_fancyshield",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYSHIELD2 = ITEMS.register("item_fancyshield2",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYSWORD = ITEMS.register("item_fancysword",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYSWORD2 = ITEMS.register("item_fancysword2",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_LETTER = ITEMS.register("item_letter",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_SCROLL = ITEMS.register("item_scroll",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_SCROLL2 = ITEMS.register("item_scroll2",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_SPECIALFLOWER = ITEMS.register("item_specialflower",
            () -> new BaseItem(new Item.Properties().stacksTo(12)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_STORYBOOK = ITEMS.register("item_storybook",
            () -> new BaseItem(new Item.Properties().stacksTo(12)).setHasLore());
    public static final DeferredItem<Item> ITEM_STORYBOOK2 = ITEMS.register("item_storybook2",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_STORYBOOK3 = ITEMS.register("item_storybook3",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_STORYBOOK4 = ITEMS.register("item_storybook4",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNARMOR = ITEMS.register("item_wornarmor",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNHELM = ITEMS.register("item_wornhelm",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNKEY = ITEMS.register("item_wornkey",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNPICK = ITEMS.register("item_wornpick",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNSHIELD = ITEMS.register("item_wornshield",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNSHIELD2 = ITEMS.register("item_wornshield2",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNSWORD = ITEMS.register("item_wornsword",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WOVENBASKET = ITEMS.register("item_wovenbasket",
            () -> new BaseItem(new Item.Properties().stacksTo(3)).setHasLore());
    public static final DeferredItem<Item> ITEM_WRITTENPAPER = ITEMS.register("item_writtenpaper",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_PUNGENTHERB = ITEMS.register("item_pungentherb",
            () -> new BaseItem(new Item.Properties().stacksTo(3)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_LOCKPICK = ITEMS.register("item_lockpick",
            () -> new BaseItem(new Item.Properties().stacksTo(16)).setHasLore());
    public static final DeferredItem<Item> ITEM_BROKENSWORD = ITEMS.register("item_brokensword",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_REFORGEDSWORD = ITEMS.register("item_reforgedsword",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_MAGIC_CLOTH = ITEMS.register("item_magic_cloth",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_KEYFRAGMENT = ITEMS.register("item_keyfragment",
            () -> new BaseItem(new Item.Properties().stacksTo(2)).setHasLore());
    public static final DeferredItem<Item> ITEM_OILYKEY = ITEMS.register("item_oilykey",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_MITHRILNUGGET = ITEMS.register("item_mithrilnugget",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_REMAINS = ITEMS.register("item_remains",
            () -> new BaseItem(new Item.Properties().stacksTo(16)).setHasLore());
    public static final DeferredItem<Item> ITEM_RUNE_STONE = ITEMS.register("item_rune_stone",
            () -> new BaseItem(new Item.Properties().stacksTo(8)).setEffectOverride().setHasLore());

    // Coins & Tokens
    public static final DeferredItem<Item> ITEM_COIN_BRONZE = ITEMS.register("item_coin_bronze",
            () -> new BaseItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_COIN_SILVER = ITEMS.register("item_coin_silver",
            () -> new BaseItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_COIN_GOLD = ITEMS.register("item_coin_gold",
            () -> new BaseItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_COIN_MITHRIL = ITEMS.register("item_coin_mithril",
            () -> new BaseItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_DARKSIGIL = ITEMS.register("item_darksigil",
            () -> new BaseItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_FACTIONCOIN = ITEMS.register("item_coin1",
            () -> new BaseItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_FACTIONTOKEN = ITEMS.register("item_coin2",
            () -> new BaseItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_CAVECOMPLETE = ITEMS.register("item_cavecomplete",
            () -> new BaseItem(new Item.Properties().stacksTo(3)).setHasLore());
    public static final DeferredItem<Item> ITEM_WATCHERCOMPLETE = ITEMS.register("item_watchercomplete",
            () -> new BaseItem(new Item.Properties().stacksTo(3)).setHasLore());
    public static final DeferredItem<Item> ITEM_TOKEN_EASTERN_ALLIANCE = ITEMS.register("item_token_eastern_alliance",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_TOKEN_WESTERN_ALLIANCE = ITEMS.register("item_token_western_alliance",
            () -> new BaseItem(new Item.Properties().stacksTo(1)).setHasLore());

    // Mob Drops
    public static final DeferredItem<Item> CREBAIN_FEATHER = ITEMS.register("feather_crebain",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BIRD_FEATHER = ITEMS.register("feather_bird",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MUMAKIL_LEATHER = ITEMS.register("leather_mumakil",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MONSTER_FUR = ITEMS.register("monster_fur",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BOTTLE_FANCY = ITEMS.register("bottle_fancy",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLEM_STONE = ITEMS.register("item_golem_stone",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> GOLEM_STONE_EARTH = ITEMS.register("item_golem_stone_earth",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> GOLEM_STONE_AIR = ITEMS.register("item_golem_stone_air",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> GOLEM_STONE_FIRE = ITEMS.register("item_golem_stone_fire",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> GOLEM_STONE_WATER = ITEMS.register("item_golem_stone_water",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> GOLEM_STONE_SUMMON = ITEMS.register("item_golem_stone_summon",
            () -> new BaseItem(new Item.Properties().stacksTo(16)).setEffectOverride().setHasLore().setItemHasUse().setSpawnInfo());


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
