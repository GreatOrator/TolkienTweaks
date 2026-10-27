package com.greatorator.tolkienmobs.init;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, MODID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, MODID);

        // POI
    // The three trades that name a building of their own work at the mod's own block: the grocer at
    // a fireplace, the coin trader (the banker) at a piggy bank, and the trinket smith at a trinket
    // table. The state sets are read from the blocks themselves, so a property added to one of those
    // blocks later is a job site as soon as it is placed.
    public static final DeferredHolder<PoiType, PoiType> COIN_TRADER_POI = POI_TYPES.register("coin_trader_poi",
            () -> new PoiType(
                    ImmutableSet.copyOf(TolkienBlocks.PIGGYBANK.get().getStateDefinition().getPossibleStates()),
                    1, 1));
    public static final DeferredHolder<PoiType, PoiType> GROCERY_STORE_POI = POI_TYPES.register("grocery_store_poi",
            () -> new PoiType(
                    ImmutableSet.copyOf(TolkienBlocks.FIREPLACE.get().getStateDefinition().getPossibleStates()),
                    1, 1));
    public static final DeferredHolder<PoiType, PoiType> PET_MERCHANT_POI = POI_TYPES.register("pet_merchant_poi",
            () -> new PoiType(
                    ImmutableSet.copyOf(Blocks.HAY_BLOCK.getStateDefinition().getPossibleStates()),
                    1, 1));
    public static final DeferredHolder<PoiType, PoiType> JUNK_TRADER_POI = POI_TYPES.register("junk_trader_poi",
            () -> new PoiType(
                    ImmutableSet.copyOf(Blocks.ANVIL.getStateDefinition().getPossibleStates()),
                    1, 1));
    public static final DeferredHolder<PoiType, PoiType> TRINKET_SMITH_POI = POI_TYPES.register("trinket_smith_poi",
            () -> new PoiType(
                    ImmutableSet.copyOf(TolkienBlocks.TRINKET_TABLE.get().getStateDefinition().getPossibleStates()),
                    1, 1));
    public static final DeferredHolder<PoiType, PoiType> TRINKET_TAILOR_POI = POI_TYPES.register("trinket_tailor_poi",
            () -> new PoiType(
                    ImmutableSet.copyOf(Blocks.AMETHYST_BLOCK.getStateDefinition().getPossibleStates()),
                    1, 1));

        // Profession
    public static final DeferredHolder<VillagerProfession, VillagerProfession> COIN_TRADER_PROFESSION = VILLAGER_PROFESSIONS.register("coin_trader",
            () -> profession("coin_trader", COIN_TRADER_POI, TolkienSounds.VILLAGER_COIN_TRADER.get(),
                    tradeSets(TolkienVillagerTrades.COIN_TRADER_LEVEL_1, TolkienVillagerTrades.COIN_TRADER_LEVEL_2,
                            TolkienVillagerTrades.COIN_TRADER_LEVEL_3)));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> GROCERY_STORE_PROFESSION = VILLAGER_PROFESSIONS.register("grocery_store",
            () -> profession("grocery_store", GROCERY_STORE_POI, SoundEvents.VILLAGER_WORK_BUTCHER,
                    tradeSets(TolkienVillagerTrades.GROCERY_STORE_LEVEL_1, TolkienVillagerTrades.GROCERY_STORE_LEVEL_2,
                            TolkienVillagerTrades.GROCERY_STORE_LEVEL_3, TolkienVillagerTrades.GROCERY_STORE_LEVEL_4)));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> PET_MERCHANT_PROFESSION = VILLAGER_PROFESSIONS.register("pet_merchant",
            () -> profession("pet_merchant", PET_MERCHANT_POI, SoundEvents.VILLAGER_WORK_LIBRARIAN,
                    tradeSets(TolkienVillagerTrades.PET_MERCHANT_LEVEL_1, TolkienVillagerTrades.PET_MERCHANT_LEVEL_2)));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> JUNK_TRADER_PROFESSION = VILLAGER_PROFESSIONS.register("junk_trader",
            () -> profession("junk_trader", JUNK_TRADER_POI, SoundEvents.WANDERING_TRADER_TRADE,
                    tradeSets(TolkienVillagerTrades.JUNK_TRADER_LEVEL_1, TolkienVillagerTrades.JUNK_TRADER_LEVEL_2,
                            TolkienVillagerTrades.JUNK_TRADER_LEVEL_3)));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> TRINKET_SMITH_PROFESSION = VILLAGER_PROFESSIONS.register("trinket_smith",
            () -> profession("trinket_smith", TRINKET_SMITH_POI, SoundEvents.VILLAGER_WORK_ARMORER,
                    tradeSets(TolkienVillagerTrades.TRINKET_SMITH_LEVEL_1, TolkienVillagerTrades.TRINKET_SMITH_LEVEL_2,
                            TolkienVillagerTrades.TRINKET_SMITH_LEVEL_3, TolkienVillagerTrades.TRINKET_SMITH_LEVEL_4)));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> TRINKET_TAILOR_PROFESSION = VILLAGER_PROFESSIONS.register("trinket_tailor",
            () -> profession("trinket_tailor", TRINKET_TAILOR_POI, SoundEvents.VILLAGER_WORK_LEATHERWORKER,
                    tradeSets(TolkienVillagerTrades.TRINKET_TAILOR_LEVEL_1, TolkienVillagerTrades.TRINKET_TAILOR_LEVEL_2,
                            TolkienVillagerTrades.TRINKET_TAILOR_LEVEL_3, TolkienVillagerTrades.TRINKET_TAILOR_LEVEL_4)));

    /**
     * 26.1 made a profession a record whose offers are named trade sets rather than a trade event, so
     * it is built here with its job site on both sides of the "held"/"acquirable" test and one entry
     * per level it has offers for. The display name keeps the key the lang files already carry.
     */
    private static VillagerProfession profession(String name, Supplier<PoiType> jobSite, SoundEvent workSound,
                                                 Int2ObjectMap<ResourceKey<TradeSet>> tradeSets) {
        return new VillagerProfession(
                Component.translatable("entity.minecraft.villager." + MODID + "." + name),
                holder -> holder.value() == jobSite.get(),
                holder -> holder.value() == jobSite.get(),
                ImmutableSet.of(),
                ImmutableSet.of(),
                workSound,
                tradeSets);
    }

    /** The level-to-trade-set table a profession reads: the first set is level one. */
    @SafeVarargs
    private static Int2ObjectMap<ResourceKey<TradeSet>> tradeSets(ResourceKey<TradeSet>... levels) {
        Int2ObjectMap<ResourceKey<TradeSet>> byLevel = new Int2ObjectOpenHashMap<>();
        for (int level = 0; level < levels.length; level++) {
            byLevel.put(level + 1, levels[level]);
        }
        return byLevel;
    }

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
