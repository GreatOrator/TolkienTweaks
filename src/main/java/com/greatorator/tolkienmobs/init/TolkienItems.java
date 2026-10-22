package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.item.*;
import com.greatorator.tolkienmobs.item.custom.*;
import com.greatorator.tolkienmobs.item.projectile.BoulderItem;
import com.greatorator.tolkienmobs.item.projectile.FellBeastFireballItem;
import com.greatorator.tolkienmobs.item.projectile.GaladhrimArrowItem;
import com.greatorator.tolkienmobs.item.projectile.UtumnoArrowItem;
import com.greatorator.tolkienmobs.network.component.TrinketComponent;
import com.greatorator.tolkienmobs.util.ColorUtility;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

        // Mithril
    public static final DeferredItem<Item> RAW_MITHRIL = ITEMS.registerSimpleItem("raw_mithril", new Item.Properties());
    public static final DeferredItem<Item> DUST_MITHRIL = ITEMS.registerSimpleItem("dust_mithril", new Item.Properties());
    public static final DeferredItem<Item> NUGGET_MITHRIL = ITEMS.registerSimpleItem("nugget_mithril", new Item.Properties());
    public static final DeferredItem<Item> INGOT_MITHRIL = ITEMS.registerSimpleItem("ingot_mithril", new Item.Properties());

        // Morgul Iron
    public static final DeferredItem<Item> RAW_MORGULIRON = ITEMS.registerSimpleItem("raw_morguliron", new Item.Properties());
    public static final DeferredItem<Item> DUST_MORGULIRON = ITEMS.registerSimpleItem("dust_morguliron", new Item.Properties());
    public static final DeferredItem<Item> NUGGET_MORGULIRON = ITEMS.registerSimpleItem("nugget_morguliron", new Item.Properties());
    public static final DeferredItem<Item> INGOT_MORGULIRON = ITEMS.registerSimpleItem("ingot_morguliron", new Item.Properties());

        // Gems
    public static final DeferredItem<Item> GEM_AMMOLITE = ITEMS.registerItem("gem_ammolite",
            properties -> new TolkienItem(properties).setEffectOverride(),
            new Item.Properties().stacksTo(16));

        // Signs
    public static final DeferredItem<Item> MALLORN_HANGING_SIGN = ITEMS.registerItem("hanging_sign_mallorn", properties -> new HangingSignItem(TolkienBlocks.MALLORN_HANGING_SIGN.get(), TolkienBlocks.MALLORN_HANGING_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> MALLORN_SIGN = ITEMS.registerItem("sign_mallorn", properties -> new SignItem(TolkienBlocks.MALLORN_SIGN.get(), TolkienBlocks.MALLORN_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> MIRKWOOD_HANGING_SIGN = ITEMS.registerItem("hanging_sign_mirkwood", properties -> new HangingSignItem(TolkienBlocks.MIRKWOOD_HANGING_SIGN.get(), TolkienBlocks.MIRKWOOD_HANGING_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> MIRKWOOD_SIGN = ITEMS.registerItem("sign_mirkwood", properties -> new SignItem(TolkienBlocks.MIRKWOOD_SIGN.get(), TolkienBlocks.MIRKWOOD_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> CULUMALDA_HANGING_SIGN = ITEMS.registerItem("hanging_sign_culumalda", properties -> new HangingSignItem(TolkienBlocks.CULUMALDA_HANGING_SIGN.get(), TolkienBlocks.CULUMALDA_HANGING_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> CULUMALDA_SIGN = ITEMS.registerItem("sign_culumalda", properties -> new SignItem(TolkienBlocks.CULUMALDA_SIGN.get(), TolkienBlocks.CULUMALDA_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> LEBETHRON_HANGING_SIGN = ITEMS.registerItem("hanging_sign_lebethron", properties -> new HangingSignItem(TolkienBlocks.LEBETHRON_HANGING_SIGN.get(), TolkienBlocks.LEBETHRON_HANGING_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> LEBETHRON_SIGN = ITEMS.registerItem("sign_lebethron", properties -> new SignItem(TolkienBlocks.LEBETHRON_SIGN.get(), TolkienBlocks.LEBETHRON_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> FANGORNOAK_HANGING_SIGN = ITEMS.registerItem("hanging_sign_fangornoak", properties -> new HangingSignItem(TolkienBlocks.FANGORNOAK_HANGING_SIGN.get(), TolkienBlocks.FANGORNOAK_HANGING_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> FANGORNOAK_SIGN = ITEMS.registerItem("sign_fangornoak", properties -> new SignItem(TolkienBlocks.FANGORNOAK_SIGN.get(), TolkienBlocks.FANGORNOAK_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> DEADWOOD_HANGING_SIGN = ITEMS.registerItem("hanging_sign_deadwood", properties -> new HangingSignItem(TolkienBlocks.DEADWOOD_HANGING_SIGN.get(), TolkienBlocks.DEADWOOD_HANGING_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> DEADWOOD_SIGN = ITEMS.registerItem("sign_deadwood", properties -> new SignItem(TolkienBlocks.DEADWOOD_SIGN.get(), TolkienBlocks.DEADWOOD_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> DWARVEN_MAPLE_HANGING_SIGN = ITEMS.registerItem("hanging_sign_dwarven_maple", properties -> new HangingSignItem(TolkienBlocks.DWARVEN_MAPLE_HANGING_SIGN.get(), TolkienBlocks.DWARVEN_MAPLE_HANGING_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> DWARVEN_MAPLE_SIGN = ITEMS.registerItem("sign_dwarven_maple", properties -> new SignItem(TolkienBlocks.DWARVEN_MAPLE_SIGN.get(), TolkienBlocks.DWARVEN_MAPLE_WALL_SIGN.get(), properties),
            new Item.Properties().stacksTo(16));


    // Boats
    public static final DeferredItem<Item> MALLORN_BOAT = ITEMS.registerItem("boat_mallorn", properties -> new MallornBoatItem(false, properties, TolkienWoodTypes.MALLORN),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MALLORN_CHEST_BOAT = ITEMS.registerItem("chest_boat_mallorn", properties -> new MallornBoatItem(true, properties, TolkienWoodTypes.MALLORN),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MIRKWOOD_BOAT = ITEMS.registerItem("boat_mirkwood", properties -> new MirkwoodBoatItem(false, properties, TolkienWoodTypes.MIRKWOOD),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MIRKWOOD_CHEST_BOAT = ITEMS.registerItem("chest_boat_mirkwood", properties -> new MirkwoodBoatItem(true, properties, TolkienWoodTypes.MIRKWOOD),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> CULUMALDA_BOAT = ITEMS.registerItem("boat_culumalda", properties -> new CulumaldaBoatItem(false, properties, TolkienWoodTypes.CULUMALDA),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> CULUMALDA_CHEST_BOAT = ITEMS.registerItem("chest_boat_culumalda", properties -> new CulumaldaBoatItem(true, properties, TolkienWoodTypes.CULUMALDA),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> LEBETHRON_BOAT = ITEMS.registerItem("boat_lebethron", properties -> new LebethronBoatItem(false, properties, TolkienWoodTypes.LEBETHRON),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> LEBETHRON_CHEST_BOAT = ITEMS.registerItem("chest_boat_lebethron", properties -> new LebethronBoatItem(true, properties, TolkienWoodTypes.LEBETHRON),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> FANGORNOAK_BOAT = ITEMS.registerItem("boat_fangornoak", properties -> new FangornoakBoatItem(false, properties, TolkienWoodTypes.FANGORNOAK),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> FANGORNOAK_CHEST_BOAT = ITEMS.registerItem("chest_boat_fangornoak", properties -> new FangornoakBoatItem(true, properties, TolkienWoodTypes.FANGORNOAK),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> DEADWOOD_BOAT = ITEMS.registerItem("boat_deadwood", properties -> new DeadwoodBoatItem(false, properties, TolkienWoodTypes.DEADWOOD),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> DEADWOOD_CHEST_BOAT = ITEMS.registerItem("chest_boat_deadwood", properties -> new DeadwoodBoatItem(true, properties, TolkienWoodTypes.DEADWOOD),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> DWARVEN_MAPLE_BOAT = ITEMS.registerItem("boat_dwarven_maple", properties -> new DwarvenMapleBoatItem(false, properties, TolkienWoodTypes.DWARVEN_MAPLE),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> DWARVEN_MAPLE_CHEST_BOAT = ITEMS.registerItem("chest_boat_dwarven_maple", properties -> new DwarvenMapleBoatItem(true, properties, TolkienWoodTypes.DWARVEN_MAPLE),
            new Item.Properties().stacksTo(1));


    // Quest
    public static final DeferredItem<Item> ITEM_BERYL = ITEMS.registerItem("item_beryl",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_FORTRESSMAP = ITEMS.registerItem("item_fortressmap",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_WATCHERHEART = ITEMS.registerItem("item_watcherheart",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_WATCHERHEART_CRACKED = ITEMS.registerItem("item_watcherheart_cracked",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_KEYSTONE = ITEMS.registerItem("item_keystone",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_DARKSADDLE = ITEMS.registerItem("item_darksaddle",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_ARTIFACT = ITEMS.registerItem("item_artifact",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_BLANKPAPER = ITEMS.registerItem("item_blankpaper",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(12));
    public static final DeferredItem<Item> ITEM_FANCYARMOR = ITEMS.registerItem("item_fancyarmor",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_FANCYCLOTH = ITEMS.registerItem("item_fancycloth",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(3));
    public static final DeferredItem<Item> ITEM_FANCYHAMMER = ITEMS.registerItem("item_fancyhammer",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_FANCYHELM = ITEMS.registerItem("item_fancyhelm",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_FANCYKEY = ITEMS.registerItem("item_fancykey",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_FANCYPICK = ITEMS.registerItem("item_fancypick",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_FANCYSHIELD = ITEMS.registerItem("item_fancyshield",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_FANCYSHIELD2 = ITEMS.registerItem("item_fancyshield2",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_FANCYSWORD = ITEMS.registerItem("item_fancysword",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_FANCYSWORD2 = ITEMS.registerItem("item_fancysword2",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_LETTER = ITEMS.registerItem("item_letter",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_SCROLL = ITEMS.registerItem("item_scroll",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_SCROLL2 = ITEMS.registerItem("item_scroll2",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_SPECIALFLOWER = ITEMS.registerItem("item_specialflower",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(12));
    public static final DeferredItem<Item> ITEM_STORYBOOK = ITEMS.registerItem("item_storybook",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(12));
    public static final DeferredItem<Item> ITEM_STORYBOOK2 = ITEMS.registerItem("item_storybook2",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_STORYBOOK3 = ITEMS.registerItem("item_storybook3",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_STORYBOOK4 = ITEMS.registerItem("item_storybook4",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_WORNARMOR = ITEMS.registerItem("item_wornarmor",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_WORNHELM = ITEMS.registerItem("item_wornhelm",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_WORNKEY = ITEMS.registerItem("item_wornkey",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_WORNPICK = ITEMS.registerItem("item_wornpick",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_WORNSHIELD = ITEMS.registerItem("item_wornshield",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_WORNSHIELD2 = ITEMS.registerItem("item_wornshield2",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_WORNSWORD = ITEMS.registerItem("item_wornsword",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_WOVENBASKET = ITEMS.registerItem("item_wovenbasket",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(3));
    public static final DeferredItem<Item> ITEM_WRITTENPAPER = ITEMS.registerItem("item_writtenpaper",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_PUNGENTHERB = ITEMS.registerItem("item_pungentherb",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(3));
    public static final DeferredItem<Item> ITEM_LOCKPICK = ITEMS.registerItem("item_lockpick",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> ITEM_BROKENSWORD = ITEMS.registerItem("item_brokensword",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_REFORGEDSWORD = ITEMS.registerItem("item_reforgedsword",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_MAGIC_CLOTH = ITEMS.registerItem("item_magic_cloth",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_KEYFRAGMENT = ITEMS.registerItem("item_keyfragment",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(2));
    public static final DeferredItem<Item> ITEM_OILYKEY = ITEMS.registerItem("item_oilykey",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_MITHRILNUGGET = ITEMS.registerItem("item_mithrilnugget",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_REMAINS = ITEMS.registerItem("item_remains",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> ITEM_RUNE_STONE = ITEMS.registerItem("item_rune_stone",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(8));
    public static final DeferredItem<Item> ITEM_EXPERIENCE_ORB = ITEMS.registerItem("experience_orb",
            properties -> new TolkienItem(properties).setEffectOverride(),
            new Item.Properties().stacksTo(1));

        // Coins & Tokens
    public static final DeferredItem<Item> ITEM_COIN_BRONZE = ITEMS.registerItem("item_coin_bronze",
            properties -> new TolkienCoinItem(properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<Item> ITEM_COIN_SILVER = ITEMS.registerItem("item_coin_silver",
            properties -> new TolkienCoinItem(properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<Item> ITEM_COIN_GOLD = ITEMS.registerItem("item_coin_gold",
            properties -> new TolkienCoinItem(properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<Item> ITEM_COIN_MITHRIL = ITEMS.registerItem("item_coin_mithril",
            properties -> new TolkienCoinItem(properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<Item> ITEM_DARKSIGIL = ITEMS.registerItem("item_darksigil",
            properties -> new TolkienCoinItem(properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<Item> ITEM_FACTIONCOIN = ITEMS.registerItem("item_coin1",
            properties -> new TolkienCoinItem(properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<Item> ITEM_FACTIONTOKEN = ITEMS.registerItem("item_coin2",
            properties -> new TolkienCoinItem(properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<Item> ITEM_CAVECOMPLETE = ITEMS.registerItem("item_cavecomplete",
            properties -> new TolkienCoinItem(properties).setHasLore(),
            new Item.Properties().stacksTo(3));
    public static final DeferredItem<Item> ITEM_WATCHERCOMPLETE = ITEMS.registerItem("item_watchercomplete",
            properties -> new TolkienCoinItem(properties).setHasLore(),
            new Item.Properties().stacksTo(3));
    public static final DeferredItem<Item> ITEM_TOKEN_EASTERN_ALLIANCE = ITEMS.registerItem("item_token_eastern_alliance",
            properties -> new TolkienCoinItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_TOKEN_WESTERN_ALLIANCE = ITEMS.registerItem("item_token_western_alliance",
            properties -> new TolkienCoinItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));

        // Mob Drops
    public static final DeferredItem<Item> CREBAIN_FEATHER = ITEMS.registerSimpleItem("feather_crebain", new Item.Properties());
    public static final DeferredItem<Item> BIRD_FEATHER = ITEMS.registerSimpleItem("feather_bird", new Item.Properties());
    public static final DeferredItem<Item> MUMAKIL_LEATHER = ITEMS.registerSimpleItem("leather_mumakil", new Item.Properties());
    public static final DeferredItem<Item> MONSTER_FUR = ITEMS.registerSimpleItem("monster_fur", new Item.Properties());
    public static final DeferredItem<Item> BOTTLE_FANCY = ITEMS.registerSimpleItem("bottle_fancy", new Item.Properties());
    public static final DeferredItem<Item> GOLEM_STONE = ITEMS.registerSimpleItem("item_golem_stone", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> GOLEM_STONE_EARTH = ITEMS.registerSimpleItem("item_golem_stone_earth", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> GOLEM_STONE_AIR = ITEMS.registerSimpleItem("item_golem_stone_air", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> GOLEM_STONE_FIRE = ITEMS.registerSimpleItem("item_golem_stone_fire", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> GOLEM_STONE_WATER = ITEMS.registerSimpleItem("item_golem_stone_water", new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> GOLEM_STONE_SUMMON = ITEMS.registerItem("item_golem_stone_summon",
            properties -> new TolkienItem(properties).setEffectOverride().setHasLore().setItemHasUse().setSpawnInfo(),
            new Item.Properties().stacksTo(16));

        // Backpack Upgrades
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_BASE = ITEMS.registerItem("upgrade_item_backpack_upgrade_base",
            properties -> new TolkienItem(properties).setHasLore(),
            new Item.Properties().stacksTo(6));
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_SIZE = ITEMS.registerItem("item_backpack_upgrade_size",
            properties -> new UpgradeItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_SIZE_2 = ITEMS.registerItem("item_backpack_upgrade_size_2",
            properties -> new UpgradeItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_FLUID = ITEMS.registerItem("item_backpack_upgrade_fluid",
            properties -> new UpgradeItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_FLUID_2 = ITEMS.registerItem("item_backpack_upgrade_fluid_2",
            properties -> new UpgradeItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_FLUID_3 = ITEMS.registerItem("item_backpack_upgrade_fluid_3",
            properties -> new UpgradeItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_FLUID_4 = ITEMS.registerItem("item_backpack_upgrade_fluid_4",
            properties -> new UpgradeItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_FLUID_5 = ITEMS.registerItem("item_backpack_upgrade_fluid_5",
            properties -> new UpgradeItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_CRAFTING = ITEMS.registerItem("item_backpack_upgrade_crafting",
            properties -> new UpgradeItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_SLEEPING = ITEMS.registerItem("item_backpack_upgrade_sleeping",
            properties -> new UpgradeItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_CAMPFIRE = ITEMS.registerItem("item_backpack_upgrade_campfire",
            properties -> new UpgradeItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> TEMPLATE_MITHRIL = ITEMS.registerItem("template_mithril", properties -> new TolkienItem(properties),
            new Item.Properties());
    public static final DeferredItem<Item> TEMPLATE_MORGULIRON = ITEMS.registerItem("template_morguliron", properties -> new TolkienItem(properties),
            new Item.Properties());
    public static final DeferredItem<Item> TEMPLATE_AMMOLITE = ITEMS.registerItem("template_ammolite", properties -> new TolkienItem(properties),
            new Item.Properties());

        // Foods
    public static final DeferredItem<Item> LEMBAS = ITEMS.registerItem("food_lembas", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().food(TolkienFoods.LEMBAS, TolkienFoods.LEMBAS_CONSUMABLE).stacksTo(64));
    public static final DeferredItem<Item> HONEY_CAKE = ITEMS.registerItem("food_honeycake", TolkienFood::new,new Item.Properties().stacksTo(64).food(TolkienFoods.HONEY_CAKE, TolkienFoods.HONEY_CAKE_CONSUMABLE));
    public static final DeferredItem<Item> CRAM = ITEMS.registerItem("food_cram", TolkienFood::new, new Item.Properties().stacksTo(64).food(TolkienFoods.CRAM, TolkienFoods.CRAM_CONSUMABLE));
    public static final DeferredItem<Item> MONSTER_FLESH = ITEMS.registerItem("monster_flesh", TolkienFood::new, new Item.Properties().stacksTo(64).food(TolkienFoods.MONSTER_FLESH, TolkienFoods.MONSTER_FLESH_CONSUMABLE));
    public static final DeferredItem<Item> INSECT = ITEMS.registerItem("food_insect", TolkienFood::new, new Item.Properties().stacksTo(64).food(TolkienFoods.INSECT, TolkienFoods.INSECT_CONSUMABLE));
    public static final DeferredItem<Item> GOLDEN_INSECT = ITEMS.registerItem("food_golden_insect", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().stacksTo(64).food(TolkienFoods.GOLDEN_INSECT, TolkienFoods.GOLDEN_INSECT_CONSUMABLE));
    public static final DeferredItem<Item> TREE_ACORN = ITEMS.registerItem("food_tree_acorn", TolkienFood::new, new Item.Properties().stacksTo(64).food(TolkienFoods.TREE_ACORN, TolkienFoods.TREE_ACORN_CONSUMABLE));
    public static final DeferredItem<Item> GOLDEN_TREE_ACORN = ITEMS.registerItem("food_golden_tree_acorn", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().stacksTo(64).food(TolkienFoods.GOLDEN_TREE_ACORN, TolkienFoods.GOLDEN_TREE_ACORN_CONSUMABLE));
    public static final DeferredItem<Item> FOOD_HONEY = ITEMS.registerItem("food_honey", TolkienFood::new, new Item.Properties().stacksTo(64).food(TolkienFoods.FOOD_HONEY, TolkienFoods.FOOD_HONEY_CONSUMABLE));

        // Drinks
    public static final DeferredItem<Item> MIRUVOR = ITEMS.registerItem("drink_miruvor", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().stacksTo(64).food(TolkienFoods.MIRUVOR, TolkienFoods.MIRUVOR_CONSUMABLE));
    public static final DeferredItem<Item> GROG = ITEMS.registerItem("drink_grog", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().stacksTo(64).food(TolkienFoods.GROG, TolkienFoods.GROG_CONSUMABLE));
    public static final DeferredItem<Item> DRINK_ENT_DRAUGHT = ITEMS.registerItem("drink_ent_draught", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().stacksTo(16).food(TolkienFoods.DRINK_ENT_DRAUGHT, TolkienFoods.DRINK_ENT_DRAUGHT_CONSUMABLE));
    public static final DeferredItem<Item> DRINK_PERSONAL_BLACKSMITH = ITEMS.registerItem("drink_personal_blacksmith", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().stacksTo(16).food(TolkienFoods.DRINK_PERSONAL_BLACKSMITH, TolkienFoods.DRINK_PERSONAL_BLACKSMITH_CONSUMABLE));
    public static final DeferredItem<Item> DRINK_ELF_FLEETFOOT = ITEMS.registerItem("drink_elf_blessing", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().stacksTo(16).food(TolkienFoods.DRINK_ELF_FLEETFOOT, TolkienFoods.DRINK_ELF_FLEETFOOT_CONSUMABLE));
    public static final DeferredItem<Item> DRINK_ELF_VITALITY = ITEMS.registerItem("drink_elf_vitality", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().stacksTo(16).food(TolkienFoods.DRINK_ELF_VITALITY, TolkienFoods.DRINK_ELF_VITALITY_CONSUMABLE));
    public static final DeferredItem<Item> DRINK_ERU_BLESSING = ITEMS.registerItem("drink_eru_blessing", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().stacksTo(16).food(TolkienFoods.DRINK_ERU_BLESSING, TolkienFoods.DRINK_ERU_BLESSING_CONSUMABLE));

        // Crops & Foliage
    public static final DeferredItem<Item> PIPEWEED_ITEM = ITEMS.registerSimpleItem("pipeweed", new Item.Properties());
    public static final DeferredItem<Item> PIPEWEED_SEEDS = ITEMS.registerItem("pipeweed_seeds", properties -> new BlockItem(TolkienBlocks.PIPEWEED.get(), properties),
            new Item.Properties());
    public static final DeferredItem<Item> BRAMBLES_BERRY = ITEMS.registerItem("brambles_berry", properties -> new BlockItem(TolkienBlocks.FLOWER_BRAMBLES.get(), properties),
            new Item.Properties().food(TolkienFoods.FOOD_BRAMBLES_BERRY, TolkienFoods.FOOD_BRAMBLES_BERRY_CONSUMABLE));

        // Music Discs
    public static final DeferredItem<Item> RECORD_EREBOR = ITEMS.registerItem("record_erebor", properties -> new Item(properties.jukeboxPlayable(TolkienJukebox.EREBOR).stacksTo(1)));
    public static final DeferredItem<Item> RECORD_HOBBITS = ITEMS.registerItem("record_hobbits", properties -> new Item(properties.jukeboxPlayable(TolkienJukebox.HOBBITS).stacksTo(1)));
    public static final DeferredItem<Item> RECORD_MINASTIRITH = ITEMS.registerItem("record_minastirith", properties -> new Item(properties.jukeboxPlayable(TolkienJukebox.MINASTIRITH).stacksTo(1)));
    public static final DeferredItem<Item> RECORD_MURDERFROG = ITEMS.registerItem("record_murderfrog", properties -> new Item(properties.jukeboxPlayable(TolkienJukebox.MURDERFROG).stacksTo(1)));
    public static final DeferredItem<Item> RECORD_BOMBADIL = ITEMS.registerItem("record_bombadil", properties -> new Item(properties.jukeboxPlayable(TolkienJukebox.BOMBADIL).stacksTo(1)));
    public static final DeferredItem<Item> RECORD_REDER = ITEMS.registerItem("record_reder", properties -> new Item(properties.jukeboxPlayable(TolkienJukebox.REDER).stacksTo(1)));
    public static final DeferredItem<Item> RECORD_RIVENDELL = ITEMS.registerItem("record_rivendell", properties -> new Item(properties.jukeboxPlayable(TolkienJukebox.RIVENDELL).stacksTo(1)));
    public static final DeferredItem<Item> RECORD_LOTHLORIEN = ITEMS.registerItem("record_lothlorien", properties -> new Item(properties.jukeboxPlayable(TolkienJukebox.LOTHLORIEN).stacksTo(1)));
    public static final DeferredItem<Item> RECORD_WILLOW = ITEMS.registerItem("record_willow", properties -> new Item(properties.jukeboxPlayable(TolkienJukebox.SHADE).stacksTo(1)));
    public static final DeferredItem<Item> RECORD_FUMBLE = ITEMS.registerItem("record_fumble", properties -> new Item(properties.jukeboxPlayable(TolkienJukebox.FUMBLE).stacksTo(1)));
    public static final DeferredItem<Item> RECORD_EDORAS = ITEMS.registerItem("record_edoras", properties -> new Item(properties.jukeboxPlayable(TolkienJukebox.EDORAS).stacksTo(1)));
    public static final DeferredItem<Item> RECORD_WBATTLE = ITEMS.registerItem("record_wbattle", properties -> new Item(properties.jukeboxPlayable(TolkienJukebox.WITCHBATTLE).stacksTo(1)));

        // Sleeping Bags
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_WHITE = ITEMS.registerItem("sleeping_bag_white", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_WHITE.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_ORANGE = ITEMS.registerItem("sleeping_bag_orange", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_ORANGE.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_MAGENTA = ITEMS.registerItem("sleeping_bag_magenta", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_MAGENTA.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_LIGHT_BLUE = ITEMS.registerItem("sleeping_bag_light_blue", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_LIGHT_BLUE.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_YELLOW = ITEMS.registerItem("sleeping_bag_yellow", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_YELLOW.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_LIME = ITEMS.registerItem("sleeping_bag_lime", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_LIME.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_PINK = ITEMS.registerItem("sleeping_bag_pink", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_PINK.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_GRAY = ITEMS.registerItem("sleeping_bag_gray", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_GRAY.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_LIGHT_GRAY = ITEMS.registerItem("sleeping_bag_light_gray", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_LIGHT_GRAY.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_CYAN = ITEMS.registerItem("sleeping_bag_cyan", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_CYAN.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_PURPLE = ITEMS.registerItem("sleeping_bag_purple", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_PURPLE.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_BLUE = ITEMS.registerItem("sleeping_bag_blue", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_BLUE.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_BROWN = ITEMS.registerItem("sleeping_bag_brown", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_BROWN.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_GREEN = ITEMS.registerItem("sleeping_bag_green", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_GREEN.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_RED = ITEMS.registerItem("sleeping_bag_red", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_RED.get(), properties).setHasLore(),
            new Item.Properties());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_BLACK = ITEMS.registerItem("sleeping_bag_black", properties -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_BLACK.get(), properties).setHasLore(),
            new Item.Properties());

        // Tools
    public static final DeferredItem<Item> HYPE_HORN = ITEMS.registerItem("hype_horn", properties -> new HypeHornItem(properties),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<DwarvenHammerItem> DWARVEN_HAMMER = ITEMS.registerItem("dwarven_hammer", properties -> new DwarvenHammerItem(TolkienToolMaterials.MITHRIL, properties),
            new Item.Properties().attributes(TolkienPickaxeItem.createAttributes(TolkienToolMaterials.MITHRIL, 8, -3.0F)));

    public static final DeferredItem<SwordItem> SWORD_MITHRIL = ITEMS.registerItem("sword_mithril", properties -> new TolkienSwordItem(TolkienToolMaterials.MITHRIL, properties),
            new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.MITHRIL, 8, -1.0F)));
    public static final DeferredItem<SwordItem> SWORD_MORGULIRON = ITEMS.registerItem("sword_morguliron", properties -> new TolkienSwordItem(TolkienToolMaterials.MORGULIRON, properties),
            new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.MORGULIRON, 8, -2.0F)));
    public static final DeferredItem<SwordItem> SWORD_AMMOLITE = ITEMS.registerItem("sword_ammolite", properties -> new TolkienSwordItem(TolkienToolMaterials.AMMOLITE, properties).setEffectOverride(),
            new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.AMMOLITE, 9, -0.5F)));

    public static final DeferredItem<PickaxeItem> PICKAXE_MITHRIL = ITEMS.registerItem("pickaxe_mithril", properties -> new TolkienPickaxeItem(TolkienToolMaterials.MITHRIL, properties),
            new Item.Properties().attributes(TolkienPickaxeItem.createAttributes(TolkienToolMaterials.MITHRIL, 8, -1.0F)));
    public static final DeferredItem<PickaxeItem> PICKAXE_MORGULIRON = ITEMS.registerItem("pickaxe_morguliron", properties -> new TolkienPickaxeItem(TolkienToolMaterials.MORGULIRON, properties),
            new Item.Properties().attributes(TolkienPickaxeItem.createAttributes(TolkienToolMaterials.MORGULIRON, 8, -2.0F)));
    public static final DeferredItem<PickaxeItem> PICKAXE_AMMOLITE = ITEMS.registerItem("pickaxe_ammolite", properties -> new TolkienPickaxeItem(TolkienToolMaterials.AMMOLITE, properties).setEffectOverride(),
            new Item.Properties().attributes(TolkienPickaxeItem.createAttributes(TolkienToolMaterials.AMMOLITE, 9, -0.5F)));

    public static final DeferredItem<AxeItem> AXE_MITHRIL = ITEMS.registerItem("axe_mithril", properties -> new TolkienAxeItem(TolkienToolMaterials.MITHRIL, properties),
            new Item.Properties().attributes(TolkienAxeItem.createAttributes(TolkienToolMaterials.MITHRIL, 8, -1.0F)));
    public static final DeferredItem<AxeItem> AXE_MORGULIRON = ITEMS.registerItem("axe_morguliron", properties -> new TolkienAxeItem(TolkienToolMaterials.MORGULIRON, properties),
            new Item.Properties().attributes(TolkienAxeItem.createAttributes(TolkienToolMaterials.MORGULIRON, 8, -2.0F)));
    public static final DeferredItem<AxeItem> AXE_AMMOLITE = ITEMS.registerItem("axe_ammolite", properties -> new TolkienAxeItem(TolkienToolMaterials.AMMOLITE, properties).setEffectOverride(),
            new Item.Properties().attributes(TolkienAxeItem.createAttributes(TolkienToolMaterials.AMMOLITE, 9, -0.5F)));

    public static final DeferredItem<ShovelItem> SHOVEL_MITHRIL = ITEMS.registerItem("shovel_mithril", properties -> new TolkienShovelItem(TolkienToolMaterials.MITHRIL, properties),
            new Item.Properties().attributes(TolkienShovelItem.createAttributes(TolkienToolMaterials.MITHRIL, 8, -1.0F)));
    public static final DeferredItem<ShovelItem> SHOVEL_MORGULIRON = ITEMS.registerItem("shovel_morguliron", properties -> new TolkienShovelItem(TolkienToolMaterials.MORGULIRON, properties),
            new Item.Properties().attributes(TolkienShovelItem.createAttributes(TolkienToolMaterials.MORGULIRON, 8, -2.0F)));
    public static final DeferredItem<ShovelItem> SHOVEL_AMMOLITE = ITEMS.registerItem("shovel_ammolite", properties -> new TolkienShovelItem(TolkienToolMaterials.AMMOLITE, properties).setEffectOverride(),
            new Item.Properties().attributes(TolkienShovelItem.createAttributes(TolkienToolMaterials.AMMOLITE, 9, -0.5F)));

    public static final DeferredItem<HoeItem> HOE_MITHRIL = ITEMS.registerItem("hoe_mithril", properties -> new TolkienHoeItem(TolkienToolMaterials.MITHRIL, properties),
            new Item.Properties().attributes(TolkienHoeItem.createAttributes(TolkienToolMaterials.MITHRIL, 8, -1.0F)));
    public static final DeferredItem<HoeItem> HOE_MORGULIRON = ITEMS.registerItem("hoe_morguliron", properties -> new TolkienHoeItem(TolkienToolMaterials.MORGULIRON, properties),
            new Item.Properties().attributes(TolkienHoeItem.createAttributes(TolkienToolMaterials.MORGULIRON, 8, -2.0F)));
    public static final DeferredItem<HoeItem> HOE_AMMOLITE = ITEMS.registerItem("hoe_ammolite", properties -> new TolkienHoeItem(TolkienToolMaterials.AMMOLITE, properties).setEffectOverride(),
            new Item.Properties().attributes(TolkienHoeItem.createAttributes(TolkienToolMaterials.AMMOLITE, 9, -0.5F)));

    public static final DeferredItem<ShearsItem> SHEARS_MITHRIL = ITEMS.registerItem("shears_mithril", properties -> new TolkienShearsItem(TolkienToolMaterials.MITHRIL, properties),
            new Item.Properties().durability(512).component(DataComponents.TOOL, TolkienShearsItem.createToolProperties()));
    public static final DeferredItem<ShearsItem> SHEARS_MORGULIRON = ITEMS.registerItem("shears_morguliron", properties -> new TolkienShearsItem(TolkienToolMaterials.MORGULIRON, properties),
            new Item.Properties().durability(320).component(DataComponents.TOOL, TolkienShearsItem.createToolProperties()));
    public static final DeferredItem<ShearsItem> SHEARS_AMMOLITE = ITEMS.registerItem("shears_ammolite", properties -> new TolkienShearsItem(TolkienToolMaterials.AMMOLITE, properties).setEffectOverride(),
            new Item.Properties().durability(768).component(DataComponents.TOOL, TolkienShearsItem.createToolProperties()));

    public static final DeferredItem<SwordItem> SWORD_WITCHKING = ITEMS.registerItem("sword_witchking", properties -> new TolkienSwordItem(TolkienToolMaterials.MORGULIRON, properties).setEffectOverride().setHasLore(),
            new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.MORGULIRON, 9, -0.5F)));
    public static final DeferredItem<SwordItem> SWORD_URUK = ITEMS.registerItem("sword_uruk", properties -> new TolkienSwordItem(TolkienToolMaterials.MORGULIRON, properties).setEffectOverride().setHasLore(),
            new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.MORGULIRON, 11, -0.5F)));
    public static final DeferredItem<SwordItem> WHIP_FIRE = ITEMS.registerItem("whip_fire", properties -> new TolkienSwordItem(TolkienToolMaterials.MORGULIRON, properties).setEffectOverride().setHasLore(),
            new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.MORGULIRON, 15, -0.5F)));
    public static final DeferredItem<SwordItem> CLUB_WOODEN = ITEMS.registerItem("club_wooden", properties -> new TolkienSwordItem(TolkienToolMaterials.MORGULIRON, properties).setEffectOverride().setHasLore(),
            new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.MORGULIRON, 9, -0.5F)));

    public static final DeferredItem<Item> HOBBIT_RING = ITEMS.registerItem("hobbit_ring", properties -> new HobbitRingItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    public static final DeferredItem<Item> ELVEN_BOW = ITEMS.registerItem("elven_bow", properties -> new TolkienBowItem(properties, 0, 14400, 1.7F, null, ColorUtility.SILVER),
            new Item.Properties());
    public static final DeferredItem<Item> URUK_BOW = ITEMS.registerItem("uruk_bow", properties -> new TolkienBowItem(properties, 0, 36000, 1.5F, null, ColorUtility.SEAGREEN),
            new Item.Properties());

    public static final DeferredItem<Item> ITEM_DEV_TOOL = ITEMS.registerItem("item_dev_tool", properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ITEM_DEV_DEBUG_TOOL = ITEMS.registerItem("item_dev_debug_tool", properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ARDA_STAFF = ITEMS.registerItem("arda_staff", properties -> new CatalystItem(properties).setHasLore(),
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    public static final DeferredItem<Item> MORGUL_CRYSTAL = ITEMS.registerItem("morgul_crystal", properties -> new MorgulCrystalItem(properties),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> RING_OF_HOPE = ITEMS.registerItem("ring_of_hope", properties -> new TolkienItem(properties).setEffectOverride().setHasLore(),
            new Item.Properties().stacksTo(1));

        // Projectiles
    public static final DeferredItem<Item> BOULDER = ITEMS.registerItem("ammo_boulder", properties -> new BoulderItem(properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> FELLBEAST_FIREBALL = ITEMS.registerItem("ammo_fellbeast_fireball", properties -> new FellBeastFireballItem(properties),
            new Item.Properties().stacksTo(16));
    public static final DeferredItem<Item> GALADHRIM_ARROW = ITEMS.registerItem("ammo_galadhrim_arrow", properties -> new GaladhrimArrowItem(properties),
            new Item.Properties());
    public static final DeferredItem<Item> UTUMNO_ARROW = ITEMS.registerItem("ammo_utumno_arrow", properties -> new UtumnoArrowItem(properties),
            new Item.Properties());

        // Armor
    public static final DeferredItem<ArmorItem> HELMET_MITHRIL = ITEMS.registerItem("helmet_mithril",
                properties -> new TolkienArmorItem(TolkienArmorMaterials.MITHRIL_ARMOR_MATERIAL, ArmorType.HELMET, properties),
            new Item.Properties());
    public static final DeferredItem<ArmorItem> CHESTPLATE_MITHRIL = ITEMS.registerItem("chestplate_mithril",
            properties -> new TolkienArmorItem(TolkienArmorMaterials.MITHRIL_ARMOR_MATERIAL, ArmorType.CHESTPLATE, properties),
            new Item.Properties());
    public static final DeferredItem<ArmorItem> LEGGINGS_MITHRIL = ITEMS.registerItem("leggings_mithril",
            properties -> new TolkienArmorItem(TolkienArmorMaterials.MITHRIL_ARMOR_MATERIAL, ArmorType.LEGGINGS, properties),
            new Item.Properties());
    public static final DeferredItem<ArmorItem> BOOTS_MITHRIL = ITEMS.registerItem("boots_mithril",
            properties -> new TolkienArmorItem(TolkienArmorMaterials.MITHRIL_ARMOR_MATERIAL, ArmorType.BOOTS, properties),
            new Item.Properties());
    public static final DeferredItem<ArmorItem> HELMET_MORGULIRON = ITEMS.registerItem("helmet_morguliron",
            properties -> new TolkienArmorItem(TolkienArmorMaterials.MORGULIRON_ARMOR_MATERIAL, ArmorType.HELMET, properties),
            new Item.Properties());
    public static final DeferredItem<ArmorItem> CHESTPLATE_MORGULIRON = ITEMS.registerItem("chestplate_morguliron",
            properties -> new TolkienArmorItem(TolkienArmorMaterials.MORGULIRON_ARMOR_MATERIAL, ArmorType.CHESTPLATE, properties),
            new Item.Properties());
    public static final DeferredItem<ArmorItem> LEGGINGS_MORGULIRON = ITEMS.registerItem("leggings_morguliron",
            properties -> new TolkienArmorItem(TolkienArmorMaterials.MORGULIRON_ARMOR_MATERIAL, ArmorType.LEGGINGS, properties),
            new Item.Properties());
    public static final DeferredItem<ArmorItem> BOOTS_MORGULIRON = ITEMS.registerItem("boots_morguliron",
            properties -> new TolkienArmorItem(TolkienArmorMaterials.MORGULIRON_ARMOR_MATERIAL, ArmorType.BOOTS, properties),
            new Item.Properties());
    public static final DeferredItem<ArmorItem> HELMET_AMMOLITE = ITEMS.registerItem("helmet_ammolite",
            properties -> new TolkienArmorItem(TolkienArmorMaterials.AMMOLITE_ARMOR_MATERIAL, ArmorType.HELMET, properties),
            new Item.Properties());
    public static final DeferredItem<ArmorItem> CHESTPLATE_AMMOLITE = ITEMS.registerItem("chestplate_ammolite",
            properties -> new TolkienArmorItem(TolkienArmorMaterials.AMMOLITE_ARMOR_MATERIAL, ArmorType.CHESTPLATE, properties),
            new Item.Properties());
    public static final DeferredItem<ArmorItem> LEGGINGS_AMMOLITE = ITEMS.registerItem("leggings_ammolite",
            properties -> new TolkienArmorItem(TolkienArmorMaterials.AMMOLITE_ARMOR_MATERIAL, ArmorType.LEGGINGS, properties),
            new Item.Properties());
    public static final DeferredItem<ArmorItem> BOOTS_AMMOLITE = ITEMS.registerItem("boots_ammolite",
            properties -> new TolkienArmorItem(TolkienArmorMaterials.AMMOLITE_ARMOR_MATERIAL, ArmorType.BOOTS, properties),
            new Item.Properties());

    public static final DeferredItem<Item> MITHRIL_HORSE_ARMOR = ITEMS.registerItem("mithril_horse_armor", properties -> new TolkienAnimalArmorItem(TolkienArmorMaterials.MITHRIL_ARMOR_MATERIAL, AnimalArmorItem.BodyType.EQUESTRIAN, properties),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> MORGULIRON_HORSE_ARMOR = ITEMS.registerItem("morguliron_horse_armor", properties -> new TolkienAnimalArmorItem(TolkienArmorMaterials.MORGULIRON_ARMOR_MATERIAL, AnimalArmorItem.BodyType.EQUESTRIAN, properties),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> AMMOLITE_HORSE_ARMOR = ITEMS.registerItem("ammolite_horse_armor", properties -> new TolkienAnimalArmorItem(TolkienArmorMaterials.AMMOLITE_ARMOR_MATERIAL, AnimalArmorItem.BodyType.EQUESTRIAN, properties),
            new Item.Properties().stacksTo(1));

        // Keys
    public static DeferredItem<Item> BRONZE_KEY = ITEMS.registerItem("bronze_key", properties -> new KeyItem(properties),
            new Item.Properties().stacksTo(1));
    public static DeferredItem<Item> SILVER_KEY = ITEMS.registerItem("silver_key", properties -> new KeyItem(properties),
            new Item.Properties().stacksTo(1));
    public static DeferredItem<Item> GOLD_KEY = ITEMS.registerItem("gold_key", properties -> new KeyItem(properties),
            new Item.Properties().stacksTo(1));
    public static DeferredItem<Item> MITHRIL_KEY = ITEMS.registerItem("mithril_key", properties -> new KeyItem(properties),
            new Item.Properties().stacksTo(1));
    public static DeferredItem<Item> MASTER_KEY = ITEMS.registerItem("master_key", properties -> new KeyItem(properties),
            new Item.Properties().stacksTo(1));

        // Pouch & Key Ring
    public static final DeferredItem<Item> COIN_POUCH = ITEMS.registerItem("coin_pouch", properties -> new CoinPouchItem(properties),
            new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> KEY_RING = ITEMS.registerItem("key_ring", properties -> new KeyRingItem(properties),
            new Item.Properties().stacksTo(1));

        // Trinkets
    public static final DeferredItem<Item> TRINKET_RING = ITEMS.registerItem("trinket_ring", properties -> new TrinketItem(properties),
            new Item.Properties().component(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY).stacksTo(1));
    public static final DeferredItem<Item> TRINKET_AMULET = ITEMS.registerItem("trinket_amulet", properties -> new TrinketItem(properties),
            new Item.Properties().component(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY).stacksTo(1));
    public static final DeferredItem<Item> TRINKET_CHARM = ITEMS.registerItem("trinket_charm", properties -> new TrinketItem(properties),
            new Item.Properties().component(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY).stacksTo(1));
    public static final DeferredItem<Item> TRINKET_BELT = ITEMS.registerItem("trinket_belt", properties -> new TrinketItem(properties),
            new Item.Properties().component(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY).stacksTo(1));
    public static final DeferredItem<Item> TRINKET_GLOVE = ITEMS.registerItem("trinket_glove", properties -> new TrinketItem(properties),
            new Item.Properties().component(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY).stacksTo(1));
    public static final DeferredItem<Item> TRINKET_HAT = ITEMS.registerItem("trinket_hat", properties -> new TrinketItem(properties),
            new Item.Properties().component(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY).stacksTo(1));
    public static final DeferredItem<Item> TRINKET_CLOAK = ITEMS.registerItem("trinket_cloak", properties -> new TrinketItem(properties),
            new Item.Properties().component(TolkienDataComponents.POTION_CONTENTS, TrinketComponent.EMPTY).stacksTo(1));

        // Torches
    public static final DeferredItem<Item> MALLORN_TORCH_ITEM = ITEMS.registerItem("torch_mallorn", properties -> new StandingAndWallBlockItem(TolkienBlocks.TORCH_MALLORN.get(), TolkienBlocks.WALL_TORCH_MALLORN.get(), Direction.DOWN, properties), new Item.Properties());
    public static final DeferredItem<Item> MIRKWOOD_TORCH_ITEM = ITEMS.registerItem("torch_mirkwood", properties -> new StandingAndWallBlockItem(TolkienBlocks.TORCH_MIRKWOOD.get(), TolkienBlocks.WALL_TORCH_MIRKWOOD.get(), Direction.DOWN, properties), new Item.Properties());
    public static final DeferredItem<Item> CULUMALDA_TORCH_ITEM = ITEMS.registerItem("torch_culumalda", properties -> new StandingAndWallBlockItem(TolkienBlocks.TORCH_CULUMALDA.get(), TolkienBlocks.WALL_TORCH_CULUMALDA.get(), Direction.DOWN, properties), new Item.Properties());
    public static final DeferredItem<Item> LEBETHRON_TORCH_ITEM = ITEMS.registerItem("torch_lebethron", properties -> new StandingAndWallBlockItem(TolkienBlocks.TORCH_LEBETHRON.get(), TolkienBlocks.WALL_TORCH_LEBETHRON.get(), Direction.DOWN, properties), new Item.Properties());
    public static final DeferredItem<Item> FANGORNOAK_TORCH_ITEM = ITEMS.registerItem("torch_fangornoak", properties -> new StandingAndWallBlockItem(TolkienBlocks.TORCH_FANGORNOAK.get(), TolkienBlocks.WALL_TORCH_FANGORNOAK.get(), Direction.DOWN, properties), new Item.Properties());
    public static final DeferredItem<Item> DEADWOOD_TORCH_ITEM = ITEMS.registerItem("torch_deadwood", properties -> new StandingAndWallBlockItem(TolkienBlocks.TORCH_DEADWOOD.get(), TolkienBlocks.WALL_TORCH_DEADWOOD.get(), Direction.DOWN, properties), new Item.Properties());
    public static final DeferredItem<Item> DWARVEN_MAPLE_TORCH_ITEM = ITEMS.registerItem("torch_dwarven_maple", properties -> new StandingAndWallBlockItem(TolkienBlocks.TORCH_DWARVEN_MAPLE.get(), TolkienBlocks.WALL_TORCH_DWARVEN_MAPLE.get(), Direction.DOWN, properties), new Item.Properties());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
