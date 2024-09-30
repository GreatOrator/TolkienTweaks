package com.greatorator.tolkienmobs.init;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, MODID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, MODID);

        // POI
    public static final DeferredHolder<PoiType, PoiType> COIN_TRADER_POI = POI_TYPES.register("coin_trader_poi",
            () -> new PoiType(ImmutableSet.copyOf(TolkienBlocks.PIGGYBANK.get().getStateDefinition().getPossibleStates()),
                    1, 1));
    public static final DeferredHolder<PoiType, PoiType> GROCERY_STORE_POI = POI_TYPES.register("grocery_store_poi",
            () -> new PoiType(ImmutableSet.copyOf(TolkienBlocks.FIREPLACE.get().getStateDefinition().getPossibleStates()),
                    1, 1));
    public static final DeferredHolder<PoiType, PoiType> PET_MERCHANT_POI = POI_TYPES.register("pet_merchant_poi",
            () -> new PoiType(ImmutableSet.copyOf(Blocks.HAY_BLOCK.getStateDefinition().getPossibleStates()),
                    1, 1));
    public static final DeferredHolder<PoiType, PoiType> JUNK_TRADER_POI = POI_TYPES.register("junk_trader_poi",
            () -> new PoiType(ImmutableSet.copyOf(TolkienBlocks.BARREL_MALLORN.get().getStateDefinition().getPossibleStates()),
                    1, 1));
    public static final DeferredHolder<PoiType, PoiType> TRINKET_SMITH_POI = POI_TYPES.register("trinket_smith_poi",
            () -> new PoiType(ImmutableSet.copyOf(TolkienBlocks.TRINKET_TABLE.get().getStateDefinition().getPossibleStates()),
                    1, 1));
    public static final DeferredHolder<PoiType, PoiType> TRINKET_TAILOR_POI = POI_TYPES.register("trinket_tailor_poi",
            () -> new PoiType(ImmutableSet.copyOf(TolkienBlocks.BACKPACK.get().getStateDefinition().getPossibleStates()),
                    1, 1));

        // Profession
    public static final DeferredHolder<VillagerProfession, VillagerProfession> COIN_TRADER_PROFESSION = VILLAGER_PROFESSIONS.register("coin_trader",
                () -> new VillagerProfession("coin_trader", holder -> holder.value() == COIN_TRADER_POI.value(),
                        holder -> holder.value() == COIN_TRADER_POI.value(), ImmutableSet.of(), ImmutableSet.of(),
                        TolkienSounds.VILLAGER_COIN_TRADER.get()));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> GROCERY_STORE_PROFESSION = VILLAGER_PROFESSIONS.register("grocery_store",
            () -> new VillagerProfession("grocery_store", holder -> holder.value() == GROCERY_STORE_POI.value(),
                    holder -> holder.value() == GROCERY_STORE_POI.value(), ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_BUTCHER));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> PET_MERCHANT_PROFESSION = VILLAGER_PROFESSIONS.register("pet_merchant",
            () -> new VillagerProfession("pet_merchant", holder -> holder.value() == PET_MERCHANT_POI.value(),
                    holder -> holder.value() == PET_MERCHANT_POI.value(), ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_LIBRARIAN));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> JUNK_TRADER_PROFESSION = VILLAGER_PROFESSIONS.register("junk_trader",
            () -> new VillagerProfession("junk_trader", holder -> holder.value() == JUNK_TRADER_POI.value(),
                    holder -> holder.value() == JUNK_TRADER_POI.value(), ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.WANDERING_TRADER_TRADE));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> TRINKET_SMITH_PROFESSION = VILLAGER_PROFESSIONS.register("trinket_smith",
            () -> new VillagerProfession("trinket_smith", holder -> holder.value() == TRINKET_SMITH_POI.value(),
                    holder -> holder.value() == TRINKET_SMITH_POI.value(), ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_ARMORER));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> TRINKET_TAILOR_PROFESSION = VILLAGER_PROFESSIONS.register("trinket_tailor",
            () -> new VillagerProfession("trinket_tailor", holder -> holder.value() == TRINKET_TAILOR_POI.value(),
                    holder -> holder.value() == TRINKET_TAILOR_POI.value(), ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_LEATHERWORKER));

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
