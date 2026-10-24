package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.datagen.loot.functions.TrinketRandomlyFunction;
import com.greatorator.tolkienmobs.handler.interfaces.RegistryHolder;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienLootFunctions {
    // 26.1 keeps a loot function type as its bare codec, so the registry holds codecs rather than a wrapper class.
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID);

    public static final LootFunctionId<TrinketRandomlyFunction> TRINKET_RANDOMLY = lootFunction("random_trinket", TrinketRandomlyFunction.CODEC);

    private static <T extends LootItemFunction> LootFunctionId<T> lootFunction(String id, MapCodec<T> codec) {
        return new LootFunctionId<>(LOOT_FUNCTIONS.register(id, () -> codec));
    }

    public record LootFunctionId<T extends LootItemFunction>(
            DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<T>> holder)
            implements RegistryHolder<MapCodec<? extends LootItemFunction>, MapCodec<T>> {}
}