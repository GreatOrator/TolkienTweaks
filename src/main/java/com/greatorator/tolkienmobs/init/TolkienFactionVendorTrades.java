package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.faction.Alliance;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * What the three faction envoys stand behind, as 26.1 wants it: every offer is a {@code villager_trade}
 * registry entry and every shelf is a {@code trade_set} listing the offers on it.
 * <p>
 * Both currencies are on every offer. A {@code VillagerTrade} carries a `wants` and an `additional_wants`,
 * which the trade screen shows as the two cost slots of one row, so a shelf asks for faction coin and a
 * faction token together. That is deliberate: a token costs a soldier's life to get, so it is what makes
 * the counters worth walking to rather than another way to spend loose change. A zero-count additional
 * cost would make the offer vanish altogether, so every offer names at least one token.
 * <p>
 * The shelves are read by {@link com.greatorator.tolkienmobs.entity.npc.FactionVendorEntity} from the
 * player's rank, so the three sets below are the three ranks and not three villager levels.
 */
public final class TolkienFactionVendorTrades {
    /** A shelf is a shop's stock, not a levelling reward, so it is worth a stack of purchases. */
    private static final int MAX_USES = 12;

    // West - Gondor, Rohan, the Shire, Lindon
    public static final ResourceKey<VillagerTrade> WEST_1_LEMBAS = key("west/1/lembas");
    public static final ResourceKey<VillagerTrade> WEST_1_CRAM = key("west/1/cram");
    public static final ResourceKey<VillagerTrade> WEST_1_MIRUVOR = key("west/1/miruvor");
    public static final ResourceKey<VillagerTrade> WEST_2_MITHRIL_INGOT = key("west/2/mithril_ingot");
    public static final ResourceKey<VillagerTrade> WEST_2_ELVEN_BOW = key("west/2/elven_bow");
    public static final ResourceKey<VillagerTrade> WEST_2_MITHRIL_PICKAXE = key("west/2/mithril_pickaxe");
    public static final ResourceKey<VillagerTrade> WEST_3_AMMOLITE_SWORD = key("west/3/ammolite_sword");
    public static final ResourceKey<VillagerTrade> WEST_3_MITHRIL_CHESTPLATE = key("west/3/mithril_chestplate");
    public static final ResourceKey<VillagerTrade> WEST_3_STAR = key("west/3/star_of_earendil");
    public static final ResourceKey<TradeSet> WEST_SHELF_1 = shelfKey("west/shelf_1");
    public static final ResourceKey<TradeSet> WEST_SHELF_2 = shelfKey("west/shelf_2");
    public static final ResourceKey<TradeSet> WEST_SHELF_3 = shelfKey("west/shelf_3");

    // East - Mordor, Isengard, Mirkwood
    public static final ResourceKey<VillagerTrade> EAST_1_GROG = key("east/1/grog");
    public static final ResourceKey<VillagerTrade> EAST_1_MONSTER_FLESH = key("east/1/monster_flesh");
    public static final ResourceKey<VillagerTrade> EAST_1_UTUMNO_ARROWS = key("east/1/utumno_arrows");
    public static final ResourceKey<VillagerTrade> EAST_2_MORGUL_INGOT = key("east/2/morgul_ingot");
    public static final ResourceKey<VillagerTrade> EAST_2_URUK_BOW = key("east/2/uruk_bow");
    public static final ResourceKey<VillagerTrade> EAST_2_MORGUL_HELMET = key("east/2/morgul_helmet");
    public static final ResourceKey<VillagerTrade> EAST_3_MORGUL_SWORD = key("east/3/morgul_sword");
    public static final ResourceKey<VillagerTrade> EAST_3_MORGUL_CHESTPLATE = key("east/3/morgul_chestplate");
    public static final ResourceKey<VillagerTrade> EAST_3_KNOT = key("east/3/morgul_knot");
    public static final ResourceKey<TradeSet> EAST_SHELF_1 = shelfKey("east/shelf_1");
    public static final ResourceKey<TradeSet> EAST_SHELF_2 = shelfKey("east/shelf_2");
    public static final ResourceKey<TradeSet> EAST_SHELF_3 = shelfKey("east/shelf_3");

    // Harad - Haradwaith and Umbar
    public static final ResourceKey<VillagerTrade> HARAD_1_CRAM = key("harad/1/cram");
    public static final ResourceKey<VillagerTrade> HARAD_1_GROG = key("harad/1/grog");
    public static final ResourceKey<VillagerTrade> HARAD_1_MUMAKIL_LEATHER = key("harad/1/mumakil_leather");
    public static final ResourceKey<VillagerTrade> HARAD_2_MORGUL_INGOT = key("harad/2/morgul_ingot");
    public static final ResourceKey<VillagerTrade> HARAD_2_MORGUL_BOOTS = key("harad/2/morgul_boots");
    public static final ResourceKey<VillagerTrade> HARAD_2_AMULET = key("harad/2/amulet");
    public static final ResourceKey<VillagerTrade> HARAD_3_MORGUL_SWORD = key("harad/3/morgul_sword");
    public static final ResourceKey<VillagerTrade> HARAD_3_MORGUL_CHESTPLATE = key("harad/3/morgul_chestplate");
    public static final ResourceKey<VillagerTrade> HARAD_3_SUNBRASS = key("harad/3/sunbrass_amulet");
    public static final ResourceKey<TradeSet> HARAD_SHELF_1 = shelfKey("harad/shelf_1");
    public static final ResourceKey<TradeSet> HARAD_SHELF_2 = shelfKey("harad/shelf_2");
    public static final ResourceKey<TradeSet> HARAD_SHELF_3 = shelfKey("harad/shelf_3");

    /** The offers themselves, before the villager trades' own so one bootstrap fills the whole registry. */
    public static void bootstrap(BootstrapContext<VillagerTrade> context) {
        TolkienVillagerTrades.bootstrap(context);

        // West
        offer(context, WEST_1_LEMBAS, 8, 1, TolkienItems.LEMBAS.get(), 4);
        offer(context, WEST_1_CRAM, 6, 1, TolkienItems.CRAM.get(), 6);
        offer(context, WEST_1_MIRUVOR, 10, 1, TolkienItems.MIRUVOR.get(), 2);
        offer(context, WEST_2_MITHRIL_INGOT, 24, 2, TolkienItems.INGOT_MITHRIL.get(), 1);
        offer(context, WEST_2_ELVEN_BOW, 20, 2, TolkienItems.ELVEN_BOW.get(), 1);
        offer(context, WEST_2_MITHRIL_PICKAXE, 24, 2, TolkienItems.PICKAXE_MITHRIL.get(), 1);
        offer(context, WEST_3_AMMOLITE_SWORD, 48, 3, TolkienItems.SWORD_AMMOLITE.get(), 1);
        offer(context, WEST_3_MITHRIL_CHESTPLATE, 56, 4, TolkienItems.CHESTPLATE_MITHRIL.get(), 1);
        offer(context, WEST_3_STAR, 40, 4, TolkienItems.FACTION_CHARM_WEST.get(), 1);

        // East
        offer(context, EAST_1_GROG, 6, 1, TolkienItems.GROG.get(), 4);
        offer(context, EAST_1_MONSTER_FLESH, 6, 1, TolkienItems.MONSTER_FLESH.get(), 8);
        offer(context, EAST_1_UTUMNO_ARROWS, 8, 1, TolkienItems.UTUMNO_ARROW.get(), 16);
        offer(context, EAST_2_MORGUL_INGOT, 24, 2, TolkienItems.INGOT_MORGULIRON.get(), 1);
        offer(context, EAST_2_URUK_BOW, 20, 2, TolkienItems.URUK_BOW.get(), 1);
        offer(context, EAST_2_MORGUL_HELMET, 24, 2, TolkienItems.HELMET_MORGULIRON.get(), 1);
        offer(context, EAST_3_MORGUL_SWORD, 48, 3, TolkienItems.SWORD_MORGULIRON.get(), 1);
        offer(context, EAST_3_MORGUL_CHESTPLATE, 56, 4, TolkienItems.CHESTPLATE_MORGULIRON.get(), 1);
        offer(context, EAST_3_KNOT, 40, 4, TolkienItems.FACTION_CHARM_EAST.get(), 1);

        // Harad
        offer(context, HARAD_1_CRAM, 6, 1, TolkienItems.CRAM.get(), 6);
        offer(context, HARAD_1_GROG, 6, 1, TolkienItems.GROG.get(), 4);
        offer(context, HARAD_1_MUMAKIL_LEATHER, 8, 1, TolkienItems.MUMAKIL_LEATHER.get(), 4);
        offer(context, HARAD_2_MORGUL_INGOT, 24, 2, TolkienItems.INGOT_MORGULIRON.get(), 1);
        offer(context, HARAD_2_MORGUL_BOOTS, 20, 2, TolkienItems.BOOTS_MORGULIRON.get(), 1);
        offer(context, HARAD_2_AMULET, 18, 2, TolkienItems.TRINKET_AMULET.get(), 1);
        offer(context, HARAD_3_MORGUL_SWORD, 48, 3, TolkienItems.SWORD_MORGULIRON.get(), 1);
        offer(context, HARAD_3_MORGUL_CHESTPLATE, 56, 4, TolkienItems.CHESTPLATE_MORGULIRON.get(), 1);
        offer(context, HARAD_3_SUNBRASS, 40, 4, TolkienItems.FACTION_AMULET_HARAD.get(), 1);
    }

    /** The three shelves, one per rank, each naming every offer that stands on it. */
    public static void bootstrapSets(BootstrapContext<TradeSet> context) {
        TolkienVillagerTrades.bootstrapSets(context);

        set(context, WEST_SHELF_1, WEST_1_LEMBAS, WEST_1_CRAM, WEST_1_MIRUVOR);
        set(context, WEST_SHELF_2, WEST_2_MITHRIL_INGOT, WEST_2_ELVEN_BOW, WEST_2_MITHRIL_PICKAXE);
        set(context, WEST_SHELF_3, WEST_3_AMMOLITE_SWORD, WEST_3_MITHRIL_CHESTPLATE, WEST_3_STAR);

        set(context, EAST_SHELF_1, EAST_1_GROG, EAST_1_MONSTER_FLESH, EAST_1_UTUMNO_ARROWS);
        set(context, EAST_SHELF_2, EAST_2_MORGUL_INGOT, EAST_2_URUK_BOW, EAST_2_MORGUL_HELMET);
        set(context, EAST_SHELF_3, EAST_3_MORGUL_SWORD, EAST_3_MORGUL_CHESTPLATE, EAST_3_KNOT);

        set(context, HARAD_SHELF_1, HARAD_1_CRAM, HARAD_1_GROG, HARAD_1_MUMAKIL_LEATHER);
        set(context, HARAD_SHELF_2, HARAD_2_MORGUL_INGOT, HARAD_2_MORGUL_BOOTS, HARAD_2_AMULET);
        set(context, HARAD_SHELF_3, HARAD_3_MORGUL_SWORD, HARAD_3_MORGUL_CHESTPLATE, HARAD_3_SUNBRASS);
    }

    /** The shelf an envoy opens for one tier of one alliance, or null if the tier is not a shelf. */
    public static ResourceKey<TradeSet> shelf(Alliance alliance, int tier) {
        return switch (alliance) {
            case WEST -> switch (tier) {
                case 1 -> WEST_SHELF_1;
                case 2 -> WEST_SHELF_2;
                case 3 -> WEST_SHELF_3;
                default -> null;
            };
            case EAST -> switch (tier) {
                case 1 -> EAST_SHELF_1;
                case 2 -> EAST_SHELF_2;
                case 3 -> EAST_SHELF_3;
                default -> null;
            };
            case HARAD -> switch (tier) {
                case 1 -> HARAD_SHELF_1;
                case 2 -> HARAD_SHELF_2;
                case 3 -> HARAD_SHELF_3;
                default -> null;
            };
        };
    }

    /**
     * One offer: the faction coin it asks for, the token it asks for on top, and what it hands over.
     * Both costs are named, because a coin alone would leave the token with nothing to buy.
     */
    private static Holder.Reference<VillagerTrade> offer(BootstrapContext<VillagerTrade> context,
                                                         ResourceKey<VillagerTrade> key, int coins, int tokens,
                                                         ItemLike gives, int count) {
        return context.register(key, new VillagerTrade(
                new TradeCost(TolkienItems.ITEM_FACTIONCOIN.get(), coins),
                Optional.of(new TradeCost(TolkienItems.ITEM_FACTIONTOKEN.get(), tokens)),
                new ItemStackTemplate(gives.asItem(), count),
                MAX_USES, 10, 0.0F, Optional.empty(), List.of()));
    }

    /** A shelf lists its offers whole: a counter shows its stock rather than drawing two at random. */
    @SafeVarargs
    private static Holder.Reference<TradeSet> set(BootstrapContext<TradeSet> context, ResourceKey<TradeSet> key,
                                                  ResourceKey<VillagerTrade>... offers) {
        HolderGetter<VillagerTrade> lookup = context.lookup(Registries.VILLAGER_TRADE);
        List<Holder<VillagerTrade>> holders = new ArrayList<>(offers.length);
        for (ResourceKey<VillagerTrade> offer : offers) {
            holders.add(lookup.getOrThrow(offer));
        }
        return context.register(key, new TradeSet(
                HolderSet.direct(holders),
                ConstantValue.exactly(offers.length),
                false,
                Optional.of(key.identifier().withPrefix("trade_set/"))));
    }

    private static ResourceKey<VillagerTrade> key(String path) {
        return ResourceKey.create(Registries.VILLAGER_TRADE, Identifier.fromNamespaceAndPath(MODID, "faction_vendor/" + path));
    }

    private static ResourceKey<TradeSet> shelfKey(String path) {
        return ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(MODID, "faction_vendor/" + path));
    }

    private TolkienFactionVendorTrades() {
    }
}
