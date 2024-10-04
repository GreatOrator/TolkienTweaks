package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;

import java.util.Map;

public class TolkienTrimMaterials {
    public static final ResourceKey<TrimMaterial> MITHRIL = registerKey("mithril");
    public static final ResourceKey<TrimMaterial> MORGULIRON = registerKey("morguliron");
    public static final ResourceKey<TrimMaterial> AMMOLITE = registerKey("ammolite");

    private static ResourceKey<TrimMaterial> registerKey(String name) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, TolkienMobsMain.prefix(name));
    }

    public static void bootstrap(BootstrapContext<TrimMaterial> context) {
        register(context, MITHRIL, TolkienItems.INGOT_MITHRIL, Style.EMPTY.withColor(217225241), 0.2F);
        register(context, MORGULIRON, TolkienItems.INGOT_MORGULIRON, Style.EMPTY.withColor(405147), 0.7F);
        register(context, AMMOLITE, TolkienItems.GEM_AMMOLITE, Style.EMPTY.withColor(126176105), 0.1F);
    }

    private static void register(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> trimKey, Holder<Item> trimItem, Style color, float itemModelIndex) {
        TrimMaterial material = new TrimMaterial(trimKey.location().getPath(), trimItem, itemModelIndex, Map.of(), Component.translatable(Util.makeDescriptionId("trim_material", trimKey.location())).withStyle(color));
        context.register(trimKey, material);
    }
}