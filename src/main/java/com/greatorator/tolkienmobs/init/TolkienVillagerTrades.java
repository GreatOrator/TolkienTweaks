package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.datagen.loot.functions.TrinketRandomlyFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The mod's six professions' offers, as 26.1 wants them: every offer is a {@code villager_trade}
 * registry entry and a profession level points at a {@code trade_set} that lists the entries it may
 * draw from. Both are datapack registries, so {@link #bootstrap} and {@link #bootstrapSets} are
 * handed to the datapack generator instead of being registered at runtime, which is what replaced
 * NeoForge's villager trade event. Each set offers the same two-at-a-time draw vanilla's do.
 */
public final class TolkienVillagerTrades {
    private static final int MAX_USES = 16;
    /** Vanilla's default trade set draws this many offers from the entries listed in it. */
    private static final float OFFERS_PER_LEVEL = 2.0F;

    // Coin trader
    public static final ResourceKey<VillagerTrade> COIN_1_BRONZE_SILVER = tradeKey("coin_trader/1/bronze_silver");
    public static final ResourceKey<VillagerTrade> COIN_1_SILVER_BRONZE = tradeKey("coin_trader/1/silver_bronze");
    public static final ResourceKey<VillagerTrade> COIN_2_SILVER_GOLD = tradeKey("coin_trader/2/silver_gold");
    public static final ResourceKey<VillagerTrade> COIN_2_GOLD_SILVER = tradeKey("coin_trader/2/gold_silver");
    public static final ResourceKey<VillagerTrade> COIN_3_GOLD_MITHRIL = tradeKey("coin_trader/3/gold_mithril");
    public static final ResourceKey<VillagerTrade> COIN_3_MITHRIL_GOLD = tradeKey("coin_trader/3/mithril_gold");
    public static final ResourceKey<TradeSet> COIN_TRADER_LEVEL_1 = tradeSetKey("coin_trader/level_1");
    public static final ResourceKey<TradeSet> COIN_TRADER_LEVEL_2 = tradeSetKey("coin_trader/level_2");
    public static final ResourceKey<TradeSet> COIN_TRADER_LEVEL_3 = tradeSetKey("coin_trader/level_3");

    // Grocer
    public static final ResourceKey<VillagerTrade> GROCERY_1_BRONZE_HONEY_CAKE = tradeKey("grocery_store/1/bronze_honey_cake");
    public static final ResourceKey<VillagerTrade> GROCERY_1_BRONZE_CRAM = tradeKey("grocery_store/1/bronze_cram");
    public static final ResourceKey<VillagerTrade> GROCERY_1_BRONZE_HONEY = tradeKey("grocery_store/1/bronze_honey");
    public static final ResourceKey<VillagerTrade> GROCERY_2_SILVER_LEMBAS = tradeKey("grocery_store/2/silver_lembas");
    public static final ResourceKey<VillagerTrade> GROCERY_2_SILVER_MIRUVOR = tradeKey("grocery_store/2/silver_miruvor");
    public static final ResourceKey<VillagerTrade> GROCERY_3_GOLD_ERU_BLESSING = tradeKey("grocery_store/3/gold_eru_blessing");
    public static final ResourceKey<VillagerTrade> GROCERY_3_GOLD_ELF_VITALITY = tradeKey("grocery_store/3/gold_elf_vitality");
    public static final ResourceKey<VillagerTrade> GROCERY_4_MITHRIL_BLACKSMITH = tradeKey("grocery_store/4/mithril_blacksmith");
    public static final ResourceKey<VillagerTrade> GROCERY_4_MITHRIL_ENT_DRAUGHT = tradeKey("grocery_store/4/mithril_ent_draught");
    public static final ResourceKey<TradeSet> GROCERY_STORE_LEVEL_1 = tradeSetKey("grocery_store/level_1");
    public static final ResourceKey<TradeSet> GROCERY_STORE_LEVEL_2 = tradeSetKey("grocery_store/level_2");
    public static final ResourceKey<TradeSet> GROCERY_STORE_LEVEL_3 = tradeSetKey("grocery_store/level_3");
    public static final ResourceKey<TradeSet> GROCERY_STORE_LEVEL_4 = tradeSetKey("grocery_store/level_4");

    // Pet merchant
    public static final ResourceKey<VillagerTrade> PET_1_BRONZE_INSECT = tradeKey("pet_merchant/1/bronze_insect");
    public static final ResourceKey<VillagerTrade> PET_1_BRONZE_ACORN = tradeKey("pet_merchant/1/bronze_acorn");
    public static final ResourceKey<VillagerTrade> PET_2_SILVER_GOLDEN_INSECT = tradeKey("pet_merchant/2/silver_golden_insect");
    public static final ResourceKey<VillagerTrade> PET_2_SILVER_GOLDEN_ACORN = tradeKey("pet_merchant/2/silver_golden_acorn");
    public static final ResourceKey<TradeSet> PET_MERCHANT_LEVEL_1 = tradeSetKey("pet_merchant/level_1");
    public static final ResourceKey<TradeSet> PET_MERCHANT_LEVEL_2 = tradeSetKey("pet_merchant/level_2");

    // Trinket smith
    public static final ResourceKey<VillagerTrade> SMITH_1_CHARM = tradeKey("trinket_smith/1/charm");
    public static final ResourceKey<VillagerTrade> SMITH_1_AMULET = tradeKey("trinket_smith/1/amulet");
    public static final ResourceKey<VillagerTrade> SMITH_1_RING = tradeKey("trinket_smith/1/ring");
    public static final ResourceKey<VillagerTrade> SMITH_2_CHARM = tradeKey("trinket_smith/2/charm");
    public static final ResourceKey<VillagerTrade> SMITH_2_AMULET = tradeKey("trinket_smith/2/amulet");
    public static final ResourceKey<VillagerTrade> SMITH_2_RING = tradeKey("trinket_smith/2/ring");
    public static final ResourceKey<VillagerTrade> SMITH_3_CHARM = tradeKey("trinket_smith/3/charm");
    public static final ResourceKey<VillagerTrade> SMITH_3_AMULET = tradeKey("trinket_smith/3/amulet");
    public static final ResourceKey<VillagerTrade> SMITH_3_RING = tradeKey("trinket_smith/3/ring");
    public static final ResourceKey<VillagerTrade> SMITH_4_CHARM = tradeKey("trinket_smith/4/charm");
    public static final ResourceKey<VillagerTrade> SMITH_4_AMULET = tradeKey("trinket_smith/4/amulet");
    public static final ResourceKey<VillagerTrade> SMITH_4_RING = tradeKey("trinket_smith/4/ring");
    public static final ResourceKey<TradeSet> TRINKET_SMITH_LEVEL_1 = tradeSetKey("trinket_smith/level_1");
    public static final ResourceKey<TradeSet> TRINKET_SMITH_LEVEL_2 = tradeSetKey("trinket_smith/level_2");
    public static final ResourceKey<TradeSet> TRINKET_SMITH_LEVEL_3 = tradeSetKey("trinket_smith/level_3");
    public static final ResourceKey<TradeSet> TRINKET_SMITH_LEVEL_4 = tradeSetKey("trinket_smith/level_4");

    // Trinket tailor
    public static final ResourceKey<VillagerTrade> TAILOR_1_BELT = tradeKey("trinket_tailor/1/belt");
    public static final ResourceKey<VillagerTrade> TAILOR_1_GLOVE = tradeKey("trinket_tailor/1/glove");
    public static final ResourceKey<VillagerTrade> TAILOR_1_CLOAK = tradeKey("trinket_tailor/1/cloak");
    public static final ResourceKey<VillagerTrade> TAILOR_1_HAT = tradeKey("trinket_tailor/1/hat");
    public static final ResourceKey<VillagerTrade> TAILOR_2_BELT = tradeKey("trinket_tailor/2/belt");
    public static final ResourceKey<VillagerTrade> TAILOR_2_GLOVE = tradeKey("trinket_tailor/2/glove");
    public static final ResourceKey<VillagerTrade> TAILOR_2_CLOAK = tradeKey("trinket_tailor/2/cloak");
    public static final ResourceKey<VillagerTrade> TAILOR_2_HAT = tradeKey("trinket_tailor/2/hat");
    public static final ResourceKey<VillagerTrade> TAILOR_3_BELT = tradeKey("trinket_tailor/3/belt");
    public static final ResourceKey<VillagerTrade> TAILOR_3_GLOVE = tradeKey("trinket_tailor/3/glove");
    public static final ResourceKey<VillagerTrade> TAILOR_3_CLOAK = tradeKey("trinket_tailor/3/cloak");
    public static final ResourceKey<VillagerTrade> TAILOR_3_HAT = tradeKey("trinket_tailor/3/hat");
    public static final ResourceKey<VillagerTrade> TAILOR_4_BELT = tradeKey("trinket_tailor/4/belt");
    public static final ResourceKey<VillagerTrade> TAILOR_4_GLOVE = tradeKey("trinket_tailor/4/glove");
    public static final ResourceKey<VillagerTrade> TAILOR_4_CLOAK = tradeKey("trinket_tailor/4/cloak");
    public static final ResourceKey<VillagerTrade> TAILOR_4_HAT = tradeKey("trinket_tailor/4/hat");
    public static final ResourceKey<TradeSet> TRINKET_TAILOR_LEVEL_1 = tradeSetKey("trinket_tailor/level_1");
    public static final ResourceKey<TradeSet> TRINKET_TAILOR_LEVEL_2 = tradeSetKey("trinket_tailor/level_2");
    public static final ResourceKey<TradeSet> TRINKET_TAILOR_LEVEL_3 = tradeSetKey("trinket_tailor/level_3");
    public static final ResourceKey<TradeSet> TRINKET_TAILOR_LEVEL_4 = tradeSetKey("trinket_tailor/level_4");

    // Junk trader
    public static final ResourceKey<VillagerTrade> JUNK_1_BOTTLE = tradeKey("junk_trader/1/bottle");
    public static final ResourceKey<VillagerTrade> JUNK_1_FUR = tradeKey("junk_trader/1/fur");
    public static final ResourceKey<VillagerTrade> JUNK_1_FLESH = tradeKey("junk_trader/1/flesh");
    public static final ResourceKey<VillagerTrade> JUNK_2_MUMAKIL_LEATHER = tradeKey("junk_trader/2/mumakil_leather");
    public static final ResourceKey<VillagerTrade> JUNK_2_CREBAIN_FEATHER = tradeKey("junk_trader/2/crebain_feather");
    public static final ResourceKey<VillagerTrade> JUNK_2_BIRD_FEATHER = tradeKey("junk_trader/2/bird_feather");
    public static final ResourceKey<VillagerTrade> JUNK_3_GOLEM_STONE = tradeKey("junk_trader/3/golem_stone");
    public static final ResourceKey<VillagerTrade> JUNK_3_GOLEM_STONE_EARTH = tradeKey("junk_trader/3/golem_stone_earth");
    public static final ResourceKey<VillagerTrade> JUNK_3_GOLEM_STONE_AIR = tradeKey("junk_trader/3/golem_stone_air");
    public static final ResourceKey<VillagerTrade> JUNK_3_GOLEM_STONE_FIRE = tradeKey("junk_trader/3/golem_stone_fire");
    public static final ResourceKey<VillagerTrade> JUNK_3_GOLEM_STONE_WATER = tradeKey("junk_trader/3/golem_stone_water");
    public static final ResourceKey<TradeSet> JUNK_TRADER_LEVEL_1 = tradeSetKey("junk_trader/level_1");
    public static final ResourceKey<TradeSet> JUNK_TRADER_LEVEL_2 = tradeSetKey("junk_trader/level_2");
    public static final ResourceKey<TradeSet> JUNK_TRADER_LEVEL_3 = tradeSetKey("junk_trader/level_3");

    public static void bootstrap(BootstrapContext<VillagerTrade> context) {
        // Coin trader
        trade(context, COIN_1_BRONZE_SILVER, TolkienItems.ITEM_COIN_BRONZE.get(), 64, TolkienItems.ITEM_COIN_SILVER.get(), 1, 25, 0.0F);
        trade(context, COIN_1_SILVER_BRONZE, TolkienItems.ITEM_COIN_SILVER.get(), 1, TolkienItems.ITEM_COIN_BRONZE.get(), 64, 25, 0.0F);
        trade(context, COIN_2_SILVER_GOLD, TolkienItems.ITEM_COIN_SILVER.get(), 64, TolkienItems.ITEM_COIN_GOLD.get(), 1, 25, 0.0F);
        trade(context, COIN_2_GOLD_SILVER, TolkienItems.ITEM_COIN_GOLD.get(), 1, TolkienItems.ITEM_COIN_SILVER.get(), 64, 25, 0.0F);
        trade(context, COIN_3_GOLD_MITHRIL, TolkienItems.ITEM_COIN_GOLD.get(), 64, TolkienItems.ITEM_COIN_MITHRIL.get(), 1, 25, 0.0F);
        trade(context, COIN_3_MITHRIL_GOLD, TolkienItems.ITEM_COIN_MITHRIL.get(), 1, TolkienItems.ITEM_COIN_GOLD.get(), 64, 25, 0.0F);

        // Grocer
        trade(context, GROCERY_1_BRONZE_HONEY_CAKE, TolkienItems.ITEM_COIN_BRONZE.get(), 13, TolkienItems.HONEY_CAKE.get(), 3, 25, 0.5F);
        trade(context, GROCERY_1_BRONZE_CRAM, TolkienItems.ITEM_COIN_BRONZE.get(), 8, TolkienItems.CRAM.get(), 3, 25, 0.5F);
        trade(context, GROCERY_1_BRONZE_HONEY, TolkienItems.ITEM_COIN_BRONZE.get(), 6, TolkienItems.FOOD_HONEY.get(), 2, 25, 0.5F);
        trade(context, GROCERY_2_SILVER_LEMBAS, TolkienItems.ITEM_COIN_SILVER.get(), 8, TolkienItems.LEMBAS.get(), 2, 50, 0.5F);
        trade(context, GROCERY_2_SILVER_MIRUVOR, TolkienItems.ITEM_COIN_SILVER.get(), 4, TolkienItems.MIRUVOR.get(), 2, 50, 0.5F);
        trade(context, GROCERY_3_GOLD_ERU_BLESSING, TolkienItems.ITEM_COIN_GOLD.get(), 3, TolkienItems.DRINK_ERU_BLESSING.get(), 1, 75, 0.5F);
        trade(context, GROCERY_3_GOLD_ELF_VITALITY, TolkienItems.ITEM_COIN_GOLD.get(), 4, TolkienItems.DRINK_ELF_VITALITY.get(), 1, 75, 0.5F);
        trade(context, GROCERY_4_MITHRIL_BLACKSMITH, TolkienItems.ITEM_COIN_MITHRIL.get(), 2, TolkienItems.DRINK_PERSONAL_BLACKSMITH.get(), 2, 100, 0.5F);
        trade(context, GROCERY_4_MITHRIL_ENT_DRAUGHT, TolkienItems.ITEM_COIN_MITHRIL.get(), 2, TolkienItems.DRINK_ENT_DRAUGHT.get(), 2, 100, 0.5F);

        // Pet merchant
        trade(context, PET_1_BRONZE_INSECT, TolkienItems.ITEM_COIN_BRONZE.get(), 36, TolkienItems.INSECT.get(), 6, 25, 0.5F);
        trade(context, PET_1_BRONZE_ACORN, TolkienItems.ITEM_COIN_BRONZE.get(), 24, TolkienItems.TREE_ACORN.get(), 4, 25, 0.5F);
        trade(context, PET_2_SILVER_GOLDEN_INSECT, TolkienItems.ITEM_COIN_SILVER.get(), 3, TolkienItems.GOLDEN_INSECT.get(), 6, 50, 0.5F);
        trade(context, PET_2_SILVER_GOLDEN_ACORN, TolkienItems.ITEM_COIN_SILVER.get(), 5, TolkienItems.GOLDEN_TREE_ACORN.get(), 4, 50, 0.5F);

        // Trinket smith
        trinket(context, SMITH_1_CHARM, TolkienItems.TRINKET_CHARM.get(), 50);
        trinket(context, SMITH_1_AMULET, TolkienItems.TRINKET_AMULET.get(), 50);
        trinket(context, SMITH_1_RING, TolkienItems.TRINKET_RING.get(), 50);
        trinket(context, SMITH_2_CHARM, TolkienItems.TRINKET_CHARM.get(), 50);
        trinket(context, SMITH_2_AMULET, TolkienItems.TRINKET_AMULET.get(), 50);
        trinket(context, SMITH_2_RING, TolkienItems.TRINKET_RING.get(), 50);
        trinket(context, SMITH_3_CHARM, TolkienItems.TRINKET_CHARM.get(), 50);
        trinket(context, SMITH_3_AMULET, TolkienItems.TRINKET_AMULET.get(), 50);
        trinket(context, SMITH_3_RING, TolkienItems.TRINKET_RING.get(), 50);
        trinket(context, SMITH_4_CHARM, TolkienItems.TRINKET_CHARM.get(), 50);
        trinket(context, SMITH_4_AMULET, TolkienItems.TRINKET_AMULET.get(), 50);
        trinket(context, SMITH_4_RING, TolkienItems.TRINKET_RING.get(), 50);

        // Trinket tailor
        trinket(context, TAILOR_1_BELT, TolkienItems.TRINKET_BELT.get(), 50);
        trinket(context, TAILOR_1_GLOVE, TolkienItems.TRINKET_GLOVE.get(), 50);
        trinket(context, TAILOR_1_CLOAK, TolkienItems.TRINKET_CLOAK.get(), 50);
        trinket(context, TAILOR_1_HAT, TolkienItems.TRINKET_HAT.get(), 50);
        trinket(context, TAILOR_2_BELT, TolkienItems.TRINKET_BELT.get(), 50);
        trinket(context, TAILOR_2_GLOVE, TolkienItems.TRINKET_GLOVE.get(), 50);
        trinket(context, TAILOR_2_CLOAK, TolkienItems.TRINKET_CLOAK.get(), 50);
        trinket(context, TAILOR_2_HAT, TolkienItems.TRINKET_HAT.get(), 50);
        trinket(context, TAILOR_3_BELT, TolkienItems.TRINKET_BELT.get(), 50);
        trinket(context, TAILOR_3_GLOVE, TolkienItems.TRINKET_GLOVE.get(), 50);
        trinket(context, TAILOR_3_CLOAK, TolkienItems.TRINKET_CLOAK.get(), 50);
        trinket(context, TAILOR_3_HAT, TolkienItems.TRINKET_HAT.get(), 50);
        trinket(context, TAILOR_4_BELT, TolkienItems.TRINKET_BELT.get(), 50);
        trinket(context, TAILOR_4_GLOVE, TolkienItems.TRINKET_GLOVE.get(), 50);
        trinket(context, TAILOR_4_CLOAK, TolkienItems.TRINKET_CLOAK.get(), 50);
        trinket(context, TAILOR_4_HAT, TolkienItems.TRINKET_HAT.get(), 50);

        // Junk trader
        trade(context, JUNK_1_BOTTLE, TolkienItems.BOTTLE_FANCY.get(), 16, TolkienItems.ITEM_COIN_BRONZE.get(), 6, 25, 0.5F);
        trade(context, JUNK_1_FUR, TolkienItems.MONSTER_FUR.get(), 16, TolkienItems.ITEM_COIN_BRONZE.get(), 6, 25, 0.5F);
        trade(context, JUNK_1_FLESH, TolkienItems.MONSTER_FLESH.get(), 16, TolkienItems.ITEM_COIN_BRONZE.get(), 6, 25, 0.5F);
        trade(context, JUNK_2_MUMAKIL_LEATHER, TolkienItems.MUMAKIL_LEATHER.get(), 16, TolkienItems.ITEM_COIN_SILVER.get(), 6, 50, 0.5F);
        trade(context, JUNK_2_CREBAIN_FEATHER, TolkienItems.CREBAIN_FEATHER.get(), 16, TolkienItems.ITEM_COIN_SILVER.get(), 6, 50, 0.5F);
        trade(context, JUNK_2_BIRD_FEATHER, TolkienItems.BIRD_FEATHER.get(), 16, TolkienItems.ITEM_COIN_SILVER.get(), 6, 50, 0.5F);
        trade(context, JUNK_3_GOLEM_STONE, TolkienItems.GOLEM_STONE.get(), 6, TolkienItems.ITEM_COIN_GOLD.get(), 1, 75, 0.5F);
        trade(context, JUNK_3_GOLEM_STONE_EARTH, TolkienItems.GOLEM_STONE_EARTH.get(), 6, TolkienItems.ITEM_COIN_GOLD.get(), 1, 75, 0.5F);
        trade(context, JUNK_3_GOLEM_STONE_AIR, TolkienItems.GOLEM_STONE_AIR.get(), 6, TolkienItems.ITEM_COIN_GOLD.get(), 1, 75, 0.5F);
        trade(context, JUNK_3_GOLEM_STONE_FIRE, TolkienItems.GOLEM_STONE_FIRE.get(), 6, TolkienItems.ITEM_COIN_GOLD.get(), 1, 75, 0.5F);
        trade(context, JUNK_3_GOLEM_STONE_WATER, TolkienItems.GOLEM_STONE_WATER.get(), 6, TolkienItems.ITEM_COIN_GOLD.get(), 1, 75, 0.5F);
    }

    /**
     * Holds the trade keys in the sets the professions point at. A set lists its entries directly
     * rather than through a tag, which is how vanilla's own sets are built, and draws two of them
     * per level like vanilla's do.
     */
    public static void bootstrapSets(BootstrapContext<TradeSet> context) {
        set(context, COIN_TRADER_LEVEL_1, COIN_1_BRONZE_SILVER, COIN_1_SILVER_BRONZE);
        set(context, COIN_TRADER_LEVEL_2, COIN_2_SILVER_GOLD, COIN_2_GOLD_SILVER);
        set(context, COIN_TRADER_LEVEL_3, COIN_3_GOLD_MITHRIL, COIN_3_MITHRIL_GOLD);

        set(context, GROCERY_STORE_LEVEL_1, GROCERY_1_BRONZE_HONEY_CAKE, GROCERY_1_BRONZE_CRAM, GROCERY_1_BRONZE_HONEY);
        set(context, GROCERY_STORE_LEVEL_2, GROCERY_2_SILVER_LEMBAS, GROCERY_2_SILVER_MIRUVOR);
        set(context, GROCERY_STORE_LEVEL_3, GROCERY_3_GOLD_ERU_BLESSING, GROCERY_3_GOLD_ELF_VITALITY);
        set(context, GROCERY_STORE_LEVEL_4, GROCERY_4_MITHRIL_BLACKSMITH, GROCERY_4_MITHRIL_ENT_DRAUGHT);

        set(context, PET_MERCHANT_LEVEL_1, PET_1_BRONZE_INSECT, PET_1_BRONZE_ACORN);
        set(context, PET_MERCHANT_LEVEL_2, PET_2_SILVER_GOLDEN_INSECT, PET_2_SILVER_GOLDEN_ACORN);

        set(context, TRINKET_SMITH_LEVEL_1, SMITH_1_CHARM, SMITH_1_AMULET, SMITH_1_RING);
        set(context, TRINKET_SMITH_LEVEL_2, SMITH_2_CHARM, SMITH_2_AMULET, SMITH_2_RING);
        set(context, TRINKET_SMITH_LEVEL_3, SMITH_3_CHARM, SMITH_3_AMULET, SMITH_3_RING);
        set(context, TRINKET_SMITH_LEVEL_4, SMITH_4_CHARM, SMITH_4_AMULET, SMITH_4_RING);

        set(context, TRINKET_TAILOR_LEVEL_1, TAILOR_1_BELT, TAILOR_1_GLOVE, TAILOR_1_CLOAK, TAILOR_1_HAT);
        set(context, TRINKET_TAILOR_LEVEL_2, TAILOR_2_BELT, TAILOR_2_GLOVE, TAILOR_2_CLOAK, TAILOR_2_HAT);
        set(context, TRINKET_TAILOR_LEVEL_3, TAILOR_3_BELT, TAILOR_3_GLOVE, TAILOR_3_CLOAK, TAILOR_3_HAT);
        set(context, TRINKET_TAILOR_LEVEL_4, TAILOR_4_BELT, TAILOR_4_GLOVE, TAILOR_4_CLOAK, TAILOR_4_HAT);

        set(context, JUNK_TRADER_LEVEL_1, JUNK_1_BOTTLE, JUNK_1_FUR, JUNK_1_FLESH);
        set(context, JUNK_TRADER_LEVEL_2, JUNK_2_MUMAKIL_LEATHER, JUNK_2_CREBAIN_FEATHER, JUNK_2_BIRD_FEATHER);
        set(context, JUNK_TRADER_LEVEL_3, JUNK_3_GOLEM_STONE, JUNK_3_GOLEM_STONE_EARTH,
                JUNK_3_GOLEM_STONE_AIR, JUNK_3_GOLEM_STONE_FIRE, JUNK_3_GOLEM_STONE_WATER);
    }

    private static Holder.Reference<VillagerTrade> trade(BootstrapContext<VillagerTrade> context, ResourceKey<VillagerTrade> key,
                                                         ItemLike wants, int wantsCount, ItemLike gives, int givesCount,
                                                         int xp, float priceMultiplier) {
        return context.register(key, new VillagerTrade(
                new TradeCost(wants, wantsCount),
                Optional.empty(),
                new ItemStackTemplate(gives.asItem(), givesCount),
                MAX_USES, xp, priceMultiplier, Optional.empty(), List.of()));
    }

    /**
     * A trinket is sold with a potion already bound to it. The potion is rolled by the trade's item
     * modifier when the offer is first generated, which is the loot-function form of the random
     * potion the old trade listing picked by hand.
     */
    private static Holder.Reference<VillagerTrade> trinket(BootstrapContext<VillagerTrade> context, ResourceKey<VillagerTrade> key,
                                                           Item gives, int xp) {
        return context.register(key, new VillagerTrade(
                new TradeCost(TolkienItems.ITEM_COIN_SILVER.get(), 5),
                Optional.empty(),
                new ItemStackTemplate(gives),
                MAX_USES, xp, 0.5F, Optional.empty(),
                List.<LootItemFunction>of(TrinketRandomlyFunction.randomPotion().build())));
    }

    @SafeVarargs
    private static Holder.Reference<TradeSet> set(BootstrapContext<TradeSet> context, ResourceKey<TradeSet> key,
                                                  ResourceKey<VillagerTrade>... trades) {
        HolderGetter<VillagerTrade> lookup = context.lookup(Registries.VILLAGER_TRADE);
        List<Holder<VillagerTrade>> holders = new ArrayList<>(trades.length);
        for (ResourceKey<VillagerTrade> trade : trades) {
            holders.add(lookup.getOrThrow(trade));
        }
        return context.register(key, new TradeSet(
                HolderSet.direct(holders),
                ConstantValue.exactly(OFFERS_PER_LEVEL),
                false,
                Optional.of(key.identifier().withPrefix("trade_set/"))));
    }

    private static ResourceKey<VillagerTrade> tradeKey(String path) {
        return ResourceKey.create(Registries.VILLAGER_TRADE, Identifier.fromNamespaceAndPath(MODID, path));
    }

    private static ResourceKey<TradeSet> tradeSetKey(String path) {
        return ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(MODID, path));
    }

    private TolkienVillagerTrades() {
    }
}
