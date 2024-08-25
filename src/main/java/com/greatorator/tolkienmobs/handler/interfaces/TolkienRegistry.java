package com.greatorator.tolkienmobs.handler.interfaces;

import com.greatorator.tolkienmobs.init.TolkienWoodTypes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public interface TolkienRegistry {
    static final ResourceKey<Registry<TolkienWoodTypes>> WOOD_TYPE_REGISTRY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "b_wood_type"));
}