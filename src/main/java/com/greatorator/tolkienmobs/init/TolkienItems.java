package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.item.*;
import com.greatorator.tolkienmobs.item.custom.*;
import com.greatorator.tolkienmobs.util.TolkienToolMaterials;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
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
            () -> new TolkienItem(new Item.Properties().stacksTo(16)).setEffectOverride());

        // Quest
    public static final DeferredItem<Item> ITEM_BERYL = ITEMS.register("item_beryl",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_FORTRESSMAP = ITEMS.register("item_fortressmap",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WATCHERHEART = ITEMS.register("item_watcherheart",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_WATCHERHEART_CRACKED = ITEMS.register("item_watcherheart_cracked",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_KEYSTONE = ITEMS.register("item_keystone",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_DARKSADDLE = ITEMS.register("item_darksaddle",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_ARTIFACT = ITEMS.register("item_artifact",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_BLANKPAPER = ITEMS.register("item_blankpaper",
            () -> new TolkienItem(new Item.Properties().stacksTo(12)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYARMOR = ITEMS.register("item_fancyarmor",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYCLOTH = ITEMS.register("item_fancycloth",
            () -> new TolkienItem(new Item.Properties().stacksTo(3)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYHAMMER = ITEMS.register("item_fancyhammer",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYHELM = ITEMS.register("item_fancyhelm",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYKEY = ITEMS.register("item_fancykey",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYPICK = ITEMS.register("item_fancypick",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYSHIELD = ITEMS.register("item_fancyshield",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYSHIELD2 = ITEMS.register("item_fancyshield2",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYSWORD = ITEMS.register("item_fancysword",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_FANCYSWORD2 = ITEMS.register("item_fancysword2",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_LETTER = ITEMS.register("item_letter",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_SCROLL = ITEMS.register("item_scroll",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_SCROLL2 = ITEMS.register("item_scroll2",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_SPECIALFLOWER = ITEMS.register("item_specialflower",
            () -> new TolkienItem(new Item.Properties().stacksTo(12)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_STORYBOOK = ITEMS.register("item_storybook",
            () -> new TolkienItem(new Item.Properties().stacksTo(12)).setHasLore());
    public static final DeferredItem<Item> ITEM_STORYBOOK2 = ITEMS.register("item_storybook2",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_STORYBOOK3 = ITEMS.register("item_storybook3",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_STORYBOOK4 = ITEMS.register("item_storybook4",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNARMOR = ITEMS.register("item_wornarmor",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNHELM = ITEMS.register("item_wornhelm",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNKEY = ITEMS.register("item_wornkey",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNPICK = ITEMS.register("item_wornpick",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNSHIELD = ITEMS.register("item_wornshield",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNSHIELD2 = ITEMS.register("item_wornshield2",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WORNSWORD = ITEMS.register("item_wornsword",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_WOVENBASKET = ITEMS.register("item_wovenbasket",
            () -> new TolkienItem(new Item.Properties().stacksTo(3)).setHasLore());
    public static final DeferredItem<Item> ITEM_WRITTENPAPER = ITEMS.register("item_writtenpaper",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_PUNGENTHERB = ITEMS.register("item_pungentherb",
            () -> new TolkienItem(new Item.Properties().stacksTo(3)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_LOCKPICK = ITEMS.register("item_lockpick",
            () -> new TolkienItem(new Item.Properties().stacksTo(16)).setHasLore());
    public static final DeferredItem<Item> ITEM_BROKENSWORD = ITEMS.register("item_brokensword",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_REFORGEDSWORD = ITEMS.register("item_reforgedsword",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_MAGIC_CLOTH = ITEMS.register("item_magic_cloth",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_KEYFRAGMENT = ITEMS.register("item_keyfragment",
            () -> new TolkienItem(new Item.Properties().stacksTo(2)).setHasLore());
    public static final DeferredItem<Item> ITEM_OILYKEY = ITEMS.register("item_oilykey",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_MITHRILNUGGET = ITEMS.register("item_mithrilnugget",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setEffectOverride().setHasLore());
    public static final DeferredItem<Item> ITEM_REMAINS = ITEMS.register("item_remains",
            () -> new TolkienItem(new Item.Properties().stacksTo(16)).setHasLore());
    public static final DeferredItem<Item> ITEM_RUNE_STONE = ITEMS.register("item_rune_stone",
            () -> new TolkienItem(new Item.Properties().stacksTo(8)).setEffectOverride().setHasLore());

        // Coins & Tokens
    public static final DeferredItem<Item> ITEM_COIN_BRONZE = ITEMS.register("item_coin_bronze",
            () -> new TolkienCoinItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_COIN_SILVER = ITEMS.register("item_coin_silver",
            () -> new TolkienCoinItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_COIN_GOLD = ITEMS.register("item_coin_gold",
            () -> new TolkienCoinItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_COIN_MITHRIL = ITEMS.register("item_coin_mithril",
            () -> new TolkienCoinItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_DARKSIGIL = ITEMS.register("item_darksigil",
            () -> new TolkienCoinItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_FACTIONCOIN = ITEMS.register("item_coin1",
            () -> new TolkienCoinItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_FACTIONTOKEN = ITEMS.register("item_coin2",
            () -> new TolkienCoinItem(new Item.Properties()).setHasLore());
    public static final DeferredItem<Item> ITEM_CAVECOMPLETE = ITEMS.register("item_cavecomplete",
            () -> new TolkienCoinItem(new Item.Properties().stacksTo(3)).setHasLore());
    public static final DeferredItem<Item> ITEM_WATCHERCOMPLETE = ITEMS.register("item_watchercomplete",
            () -> new TolkienCoinItem(new Item.Properties().stacksTo(3)).setHasLore());
    public static final DeferredItem<Item> ITEM_TOKEN_EASTERN_ALLIANCE = ITEMS.register("item_token_eastern_alliance",
            () -> new TolkienCoinItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_TOKEN_WESTERN_ALLIANCE = ITEMS.register("item_token_western_alliance",
            () -> new TolkienCoinItem(new Item.Properties().stacksTo(1)).setHasLore());

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
            () -> new TolkienItem(new Item.Properties().stacksTo(16)).setEffectOverride().setHasLore().setItemHasUse().setSpawnInfo());

        // Backpack Upgrades
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_BASE = ITEMS.register("upgrade_item_backpack_upgrade_base",
            () -> new TolkienItem(new Item.Properties().stacksTo(6)).setHasLore());
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_SIZE = ITEMS.register("item_backpack_upgrade_size",
            () -> new TolkienItem(new Item.Properties().stacksTo(2)).setHasLore());
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_FLUID = ITEMS.register("item_backpack_upgrade_fluid",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_CRAFTING = ITEMS.register("item_backpack_upgrade_crafting",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_SLEEPING = ITEMS.register("item_backpack_upgrade_sleeping",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());
    public static final DeferredItem<Item> ITEM_BACKPACK_UPGRADE_CAMPFIRE = ITEMS.register("item_backpack_upgrade_campfire",
            () -> new TolkienItem(new Item.Properties().stacksTo(1)).setHasLore());

        // Foods
    public static final DeferredItem<Item> LEMBAS = ITEMS.registerItem("food_lembas", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().food(TolkienFoods.LEMBAS).stacksTo(64));
    public static final DeferredItem<Item> HONEY_CAKE = ITEMS.registerItem("food_honeycake", TolkienFood::new,new Item.Properties().stacksTo(64).food(TolkienFoods.HONEY_CAKE));
    public static final DeferredItem<Item> CRAM = ITEMS.registerItem("food_cram", TolkienFood::new, new Item.Properties().stacksTo(64).food(TolkienFoods.CRAM));
    public static final DeferredItem<Item> MONSTER_FLESH = ITEMS.registerItem("monster_flesh", TolkienFood::new, new Item.Properties().stacksTo(64).food(TolkienFoods.MONSTER_FLESH));
    public static final DeferredItem<Item> INSECT = ITEMS.registerItem("food_insect", TolkienFood::new, new Item.Properties().stacksTo(64).food(TolkienFoods.INSECT));
    public static final DeferredItem<Item> GOLDEN_INSECT = ITEMS.registerItem("food_golden_insect", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().stacksTo(64).food(TolkienFoods.GOLDEN_INSECT));
    public static final DeferredItem<Item> TREE_ACORN = ITEMS.registerItem("food_tree_acorn", TolkienFood::new, new Item.Properties().stacksTo(64).food(TolkienFoods.TREE_ACORN));
    public static final DeferredItem<Item> GOLDEN_TREE_ACORN = ITEMS.registerItem("food_golden_tree_acorn", properties -> new TolkienFood(properties).setEffectOverride(), new Item.Properties().stacksTo(64).food(TolkienFoods.GOLDEN_TREE_ACORN));
    public static final DeferredItem<Item> FOOD_HONEY = ITEMS.registerItem("food_honey", TolkienFood::new, new Item.Properties().stacksTo(64).food(TolkienFoods.FOOD_HONEY));

        // Drinks
    public static final DeferredItem<Item> MIRUVOR = ITEMS.registerItem("drink_miruvor", properties -> new TolkienFood(properties).setEffectOverride().setItemUseAction(true), new Item.Properties().stacksTo(64).food(TolkienFoods.MIRUVOR));
    public static final DeferredItem<Item> GROG = ITEMS.registerItem("drink_grog", properties -> new TolkienFood(properties).setEffectOverride().setItemUseAction(true), new Item.Properties().stacksTo(64).food(TolkienFoods.GROG));
    public static final DeferredItem<Item> DRINK_ENT_DRAUGHT = ITEMS.registerItem("drink_ent_draught", properties -> new TolkienFood(properties).setEffectOverride().setItemUseAction(true), new Item.Properties().stacksTo(16).food(TolkienFoods.DRINK_ENT_DRAUGHT));
    public static final DeferredItem<Item> DRINK_PERSONAL_BLACKSMITH = ITEMS.registerItem("drink_personal_blacksmith", properties -> new TolkienFood(properties).setEffectOverride().setItemUseAction(true), new Item.Properties().stacksTo(16).food(TolkienFoods.DRINK_PERSONAL_BLACKSMITH));
    public static final DeferredItem<Item> DRINK_ELF_FLEETFOOT = ITEMS.registerItem("drink_elf_blessing", properties -> new TolkienFood(properties).setEffectOverride().setItemUseAction(true), new Item.Properties().stacksTo(16).food(TolkienFoods.DRINK_ELF_FLEETFOOT));
    public static final DeferredItem<Item> DRINK_ELF_VITALITY = ITEMS.registerItem("drink_elf_vitality", properties -> new TolkienFood(properties).setEffectOverride().setItemUseAction(true), new Item.Properties().stacksTo(16).food(TolkienFoods.DRINK_ELF_VITALITY));
    public static final DeferredItem<Item> DRINK_ERU_BLESSING = ITEMS.registerItem("drink_eru_blessing", properties -> new TolkienFood(properties).setEffectOverride().setItemUseAction(true), new Item.Properties().stacksTo(16).food(TolkienFoods.DRINK_ERU_BLESSING));

        // Crops & Foliage
    public static final DeferredItem<Item> PIPEWEED_ITEM = ITEMS.register("pipeweed", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PIPEWEED_SEEDS = ITEMS.register("pipeweed_seeds", () -> new ItemNameBlockItem(TolkienBlocks.PIPEWEED.get(), new Item.Properties()));

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
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_WHITE = ITEMS.register("sleeping_bag_white", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_WHITE.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_ORANGE = ITEMS.register("sleeping_bag_orange", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_ORANGE.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_MAGENTA = ITEMS.register("sleeping_bag_magenta", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_MAGENTA.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_LIGHT_BLUE = ITEMS.register("sleeping_bag_light_blue", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_LIGHT_BLUE.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_YELLOW = ITEMS.register("sleeping_bag_yellow", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_YELLOW.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_LIME = ITEMS.register("sleeping_bag_lime", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_LIME.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_PINK = ITEMS.register("sleeping_bag_pink", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_PINK.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_GRAY = ITEMS.register("sleeping_bag_gray", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_GRAY.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_LIGHT_GRAY = ITEMS.register("sleeping_bag_light_gray", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_LIGHT_GRAY.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_CYAN = ITEMS.register("sleeping_bag_cyan", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_CYAN.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_PURPLE = ITEMS.register("sleeping_bag_purple", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_PURPLE.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_BLUE = ITEMS.register("sleeping_bag_blue", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_BLUE.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_BROWN = ITEMS.register("sleeping_bag_brown", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_BROWN.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_GREEN = ITEMS.register("sleeping_bag_green", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_GREEN.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_RED = ITEMS.register("sleeping_bag_red", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_RED.get(), new Item.Properties()).setHasLore());
    public static final DeferredItem<SleepingBagItem> SLEEPING_BAG_BLACK = ITEMS.register("sleeping_bag_black", () -> new SleepingBagItem(TolkienBlocks.SLEEPING_BAG_BLACK.get(), new Item.Properties()).setHasLore());

        // Tools
    public static final DeferredItem<Item> HYPE_HORN = ITEMS.register("hype_horn", () -> new HypeHornItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<SwordItem> SWORD_MITHRIL = ITEMS.register("sword_mithril", () -> new TolkienSwordItem(TolkienToolMaterials.MITHRIL, new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.MITHRIL, 8, -1.0F))));
    public static final DeferredItem<SwordItem> SWORD_MORGULIRON = ITEMS.register("sword_morguliron", () -> new TolkienSwordItem(TolkienToolMaterials.MORGULIRON, new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.MORGULIRON, 8, -2.0F))));
    public static final DeferredItem<SwordItem> SWORD_AMMOLITE = ITEMS.register("sword_ammolite", () -> new TolkienSwordItem(TolkienToolMaterials.AMMOLITE, new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.AMMOLITE, 9, -0.5F))).setEffectOverride());

    public static final DeferredItem<PickaxeItem> PICKAXE_MITHRIL = ITEMS.register("pickaxe_mithril", () -> new TolkienPickaxeItem(TolkienToolMaterials.MITHRIL, new Item.Properties().attributes(TolkienPickaxeItem.createAttributes(TolkienToolMaterials.MITHRIL, 8, -1.0F))));
    public static final DeferredItem<PickaxeItem> PICKAXE_MORGULIRON = ITEMS.register("pickaxe_morguliron", () -> new TolkienPickaxeItem(TolkienToolMaterials.MORGULIRON, new Item.Properties().attributes(TolkienPickaxeItem.createAttributes(TolkienToolMaterials.MORGULIRON, 8, -2.0F))));
    public static final DeferredItem<PickaxeItem> PICKAXE_AMMOLITE = ITEMS.register("pickaxe_ammolite", () -> new TolkienPickaxeItem(TolkienToolMaterials.AMMOLITE, new Item.Properties().attributes(TolkienPickaxeItem.createAttributes(TolkienToolMaterials.AMMOLITE, 9, -0.5F))).setEffectOverride());

    public static final DeferredItem<AxeItem> AXE_MITHRIL = ITEMS.register("axe_mithril", () -> new TolkienAxeItem(TolkienToolMaterials.MITHRIL, new Item.Properties().attributes(TolkienAxeItem.createAttributes(TolkienToolMaterials.MITHRIL, 8, -1.0F))));
    public static final DeferredItem<AxeItem> AXE_MORGULIRON = ITEMS.register("axe_morguliron", () -> new TolkienAxeItem(TolkienToolMaterials.MORGULIRON, new Item.Properties().attributes(TolkienAxeItem.createAttributes(TolkienToolMaterials.MORGULIRON, 8, -2.0F))));
    public static final DeferredItem<AxeItem> AXE_AMMOLITE = ITEMS.register("axe_ammolite", () -> new TolkienAxeItem(TolkienToolMaterials.AMMOLITE, new Item.Properties().attributes(TolkienAxeItem.createAttributes(TolkienToolMaterials.AMMOLITE, 9, -0.5F))).setEffectOverride());

    public static final DeferredItem<ShovelItem> SHOVEL_MITHRIL = ITEMS.register("shovel_mithril", () -> new TolkienShovelItem(TolkienToolMaterials.MITHRIL, new Item.Properties().attributes(TolkienShovelItem.createAttributes(TolkienToolMaterials.MITHRIL, 8, -1.0F))));
    public static final DeferredItem<ShovelItem> SHOVEL_MORGULIRON = ITEMS.register("shovel_morguliron", () -> new TolkienShovelItem(TolkienToolMaterials.MORGULIRON, new Item.Properties().attributes(TolkienShovelItem.createAttributes(TolkienToolMaterials.MORGULIRON, 8, -2.0F))));
    public static final DeferredItem<ShovelItem> SHOVEL_AMMOLITE = ITEMS.register("shovel_ammolite", () -> new TolkienShovelItem(TolkienToolMaterials.AMMOLITE, new Item.Properties().attributes(TolkienShovelItem.createAttributes(TolkienToolMaterials.AMMOLITE, 9, -0.5F))).setEffectOverride());

    public static final DeferredItem<HoeItem> HOE_MITHRIL = ITEMS.register("hoe_mithril", () -> new TolkienHoeItem(TolkienToolMaterials.MITHRIL, new Item.Properties().attributes(TolkienHoeItem.createAttributes(TolkienToolMaterials.MITHRIL, 8, -1.0F))));
    public static final DeferredItem<HoeItem> HOE_MORGULIRON = ITEMS.register("hoe_morguliron", () -> new TolkienHoeItem(TolkienToolMaterials.MORGULIRON, new Item.Properties().attributes(TolkienHoeItem.createAttributes(TolkienToolMaterials.MORGULIRON, 8, -2.0F))));
    public static final DeferredItem<HoeItem> HOE_AMMOLITE = ITEMS.register("hoe_ammolite", () -> new TolkienHoeItem(TolkienToolMaterials.AMMOLITE, new Item.Properties().attributes(TolkienHoeItem.createAttributes(TolkienToolMaterials.AMMOLITE, 9, -0.5F))).setEffectOverride());

    public static final DeferredItem<ShearsItem> SHEARS_MITHRIL = ITEMS.register("shears_mithril", () -> new TolkienShearsItem(new Item.Properties().durability(512).component(DataComponents.TOOL, TolkienShearsItem.createToolProperties())));
    public static final DeferredItem<ShearsItem> SHEARS_MORGULIRON = ITEMS.register("shears_morguliron", () -> new TolkienShearsItem(new Item.Properties().durability(320).component(DataComponents.TOOL, TolkienShearsItem.createToolProperties())));
    public static final DeferredItem<ShearsItem> SHEARS_AMMOLITE = ITEMS.register("shears_ammolite", () -> new TolkienShearsItem(new Item.Properties().durability(768).component(DataComponents.TOOL, TolkienShearsItem.createToolProperties())).setEffectOverride());

    public static final DeferredItem<SwordItem> SWORD_WITCHKING = ITEMS.register("sword_witchking", () -> new TolkienSwordItem(TolkienToolMaterials.MORGULIRON, new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.MORGULIRON, 9, -0.5F))).setEffectOverride().setHasLore());
    public static final DeferredItem<SwordItem> SWORD_URUK = ITEMS.register("sword_uruk", () -> new TolkienSwordItem(TolkienToolMaterials.MORGULIRON, new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.MORGULIRON, 11, -0.5F))).setEffectOverride().setHasLore());
    public static final DeferredItem<SwordItem> WHIP_FIRE = ITEMS.register("whip_fire", () -> new TolkienSwordItem(TolkienToolMaterials.MORGULIRON, new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.MORGULIRON, 15, -0.5F))).setEffectOverride().setHasLore());
    public static final DeferredItem<SwordItem> CLUB_WOODEN = ITEMS.register("club_wooden", () -> new TolkienSwordItem(TolkienToolMaterials.MORGULIRON, new Item.Properties().attributes(TolkienSwordItem.createAttributes(TolkienToolMaterials.MORGULIRON, 9, -0.5F))).setEffectOverride().setHasLore());

//    public static RegistryObject<BowItem> ELVEN_BOW = ITEMS.register("elven_bow", () -> new BowItem(new Item.Properties().stacksTo(1).tab(toolsGroup)));
//    public static RegistryObject<BowItem> URUK_BOW = ITEMS.register("uruk_bow", () -> new BowItem(new Item.Properties().stacksTo(1).tab(toolsGroup)));

//    public static RegistryObject<Item> ITEM_DEV_TOOL = ITEMS.register("item_dev_tool", () -> new BaseItem(new Item.Properties().stacksTo(1).tab(toolsGroup)).setEffectOverride().setHasLore());
//    public static RegistryObject<Item> ITEM_DEV_DEBUG_TOOL = ITEMS.register("item_dev_debug_tool", () -> new BaseItem(new Item.Properties().stacksTo(1).tab(toolsGroup)).setEffectOverride().setHasLore());
//    public static RegistryObject<Item> ARDA_STAFF = ITEMS.register("arda_staff", () -> new CatalystItem(new Item.Properties().stacksTo(1).tab(toolsGroup).rarity(Rarity.RARE)).setHasLore());
//    public static DeferredItem<Item> HOBBIT_RING = ITEMS.register("hobbit_ring", () -> new HobbitRingItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)).setHasLore());
//    public static RegistryObject<Item> MORGUL_CRYSTAL = ITEMS.register("morgul_crystal", () -> new MorgulCrystalItem(new Item.Properties().stacksTo(1).tab(decoGroup)));

        // Projectiles
//    public static RegistryObject<ArrowItem> GALADHRIM_ARROW = ITEMS.register("ammo_galadhrim_arrow", () -> new GaladhrimArrowItem((new Item.Properties()).tab(toolsGroup)));
//    public static RegistryObject<ArrowItem> UTUMNO_ARROW = ITEMS.register("ammo_utumno_arrow", () -> new UtumnoArrowItem((new Item.Properties()).tab(toolsGroup)));
//    public static RegistryObject<Item> BOULDER = ITEMS.register("ammo_boulder", () -> new BoulderItem((new Item.Properties()).stacksTo(16).tab(toolsGroup)));
//    public static RegistryObject<Item> FELLBEAST_FIREBALL = ITEMS.register("ammo_fellbeast_fireball", () -> new FellBeastFireballItem((new Item.Properties()).stacksTo(16).tab(toolsGroup)));

        // Armor
//    public static RegistryObject<ArmorItem> HELMET_MITHRIL = ITEMS.register("helmet_mithril", () -> new MithrilArmorItem(TolkienArmorMaterial.MITHRIL, EquipmentSlot.HEAD, (new Item.Properties()).tab(toolsGroup)));
//    public static RegistryObject<ArmorItem> CHESTPLATE_MITHRIL = ITEMS.register("chestplate_mithril", () -> new MithrilArmorItem(TolkienArmorMaterial.MITHRIL, EquipmentSlot.CHEST, (new Item.Properties()).tab(toolsGroup)));
//    public static RegistryObject<ArmorItem> LEGGINGS_MITHRIL = ITEMS.register("leggings_mithril", () -> new MithrilArmorItem(TolkienArmorMaterial.MITHRIL, EquipmentSlot.LEGS, (new Item.Properties()).tab(toolsGroup)));
//    public static RegistryObject<ArmorItem> BOOTS_MITHRIL = ITEMS.register("boots_mithril", () -> new MithrilArmorItem(TolkienArmorMaterial.MITHRIL, EquipmentSlot.FEET, (new Item.Properties()).tab(toolsGroup)));
//    public static RegistryObject<Item> MITHRIL_HORSE_ARMOR = ITEMS.register("mithril_horse_armor", () -> new BaseHorseArmorItem(15, "mithril", (new Item.Properties()).stacksTo(1).tab(toolsGroup)));
//    public static RegistryObject<ArmorItem> HELMET_MORGULIRON = ITEMS.register("helmet_morguliron", () -> new MorgulironArmorItem(TolkienArmorMaterial.MORGULIRON, EquipmentSlot.HEAD, (new Item.Properties()).tab(toolsGroup)));
//    public static RegistryObject<ArmorItem> CHESTPLATE_MORGULIRON = ITEMS.register("chestplate_morguliron", () -> new MorgulironArmorItem(TolkienArmorMaterial.MORGULIRON, EquipmentSlot.CHEST, (new Item.Properties()).tab(toolsGroup)));
//    public static RegistryObject<ArmorItem> LEGGINGS_MORGULIRON = ITEMS.register("leggings_morguliron", () -> new MorgulironArmorItem(TolkienArmorMaterial.MORGULIRON, EquipmentSlot.LEGS, (new Item.Properties()).tab(toolsGroup)));
//    public static RegistryObject<ArmorItem> BOOTS_MORGULIRON = ITEMS.register("boots_morguliron", () -> new MorgulironArmorItem(TolkienArmorMaterial.MORGULIRON, EquipmentSlot.FEET, (new Item.Properties()).tab(toolsGroup)));
//    public static RegistryObject<Item> MORGULIRON_HORSE_ARMOR = ITEMS.register("morguliron_horse_armor", () -> new BaseHorseArmorItem(13, "morguliron", (new Item.Properties()).stacksTo(1).tab(toolsGroup)));

        // Keys
    public static DeferredItem<Item> BRONZE_KEY = ITEMS.register("bronze_key", () -> new KeyItem(new Item.Properties().stacksTo(1)));
    public static DeferredItem<Item> SILVER_KEY = ITEMS.register("silver_key", () -> new KeyItem(new Item.Properties().stacksTo(1)));
    public static DeferredItem<Item> GOLD_KEY = ITEMS.register("gold_key", () -> new KeyItem(new Item.Properties().stacksTo(1)));
    public static DeferredItem<Item> MITHRIL_KEY = ITEMS.register("mithril_key", () -> new KeyItem(new Item.Properties().stacksTo(1)));
    public static DeferredItem<Item> MASTER_KEY = ITEMS.register("master_key", () -> new KeyItem(new Item.Properties().stacksTo(1)));

        // Pouch & Key Ring
    public static final DeferredItem<Item> COIN_POUCH = ITEMS.register("coin_pouch", () -> new CoinPouchItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> KEY_RING = ITEMS.register("key_ring", () -> new KeyRingItem(new Item.Properties().stacksTo(1)));

        // Trinkets
//    public static RegistryObject<TrinketItem> TRINKET_AMULET = ITEMS.register("trinket_amulet", () -> new TrinketItem(new Item.Properties().stacksTo(1).tab(toolsGroup)));
//    public static RegistryObject<TrinketItem> TRINKET_CHARM = ITEMS.register("trinket_charm", () -> new TrinketItem(new Item.Properties().stacksTo(1).tab(toolsGroup)));
//    public static RegistryObject<TrinketItem> TRINKET_RING = ITEMS.register("trinket_ring", () -> new TrinketItem(new Item.Properties().stacksTo(1).tab(toolsGroup)));
//    public static RegistryObject<TrinketItem> TRINKET_BELT = ITEMS.register("trinket_belt", () -> new TrinketItem(new Item.Properties().stacksTo(1).tab(toolsGroup)));
//    public static RegistryObject<TrinketItem> TRINKET_GLOVE = ITEMS.register("trinket_glove", () -> new TrinketItem(new Item.Properties().stacksTo(1).tab(toolsGroup)));
//    public static RegistryObject<TrinketItem> TRINKET_HAT = ITEMS.register("trinket_hat", () -> new TrinketItem(new Item.Properties().stacksTo(1).tab(toolsGroup)));
//    public static RegistryObject<TrinketItem> TRINKET_CLOAK = ITEMS.register("trinket_cloak", () -> new TrinketItem(new Item.Properties().stacksTo(1).tab(toolsGroup)));

    //Potions
    //Enchants
    //Biomes
    //Villagers
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
