package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.datagen.loot.functions.TrinketRandomlyFunction;
import com.greatorator.tolkienmobs.handler.interfaces.RegistryHolder;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienLootFunctions {
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID);

    public static final LootFunctionId<TrinketRandomlyFunction> TRINKET_RANDOMLY = lootFunction("random_trinket", TrinketRandomlyFunction.CODEC);

    private static <T extends LootItemFunction> LootFunctionId<T> lootFunction(String id, MapCodec<T> codec) {
        return new LootFunctionId<>(LOOT_FUNCTIONS.register(id, () -> new LootItemFunctionType<>(codec)));
    }

    public record LootFunctionId<T extends LootItemFunction>(
            DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<T>> holder)
            implements RegistryHolder<LootItemFunctionType<?>, LootItemFunctionType<T>> {}
}