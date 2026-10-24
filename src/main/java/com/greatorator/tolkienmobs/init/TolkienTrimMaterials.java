package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.util.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.util.Map;

public class TolkienTrimMaterials {
    public static final ResourceKey<TrimMaterial> MITHRIL = registerKey("mithril");
    public static final ResourceKey<TrimMaterial> MORGULIRON = registerKey("morguliron");
    public static final ResourceKey<TrimMaterial> AMMOLITE = registerKey("ammolite");

    private static ResourceKey<TrimMaterial> registerKey(String name) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, TolkienMobsMain.prefix(name));
    }

    public static void bootstrap(BootstrapContext<TrimMaterial> context) {
        register(context, MITHRIL, TolkienItems.INGOT_MITHRIL.get(), Style.EMPTY.withColor(217225241));
        register(context, MORGULIRON, TolkienItems.INGOT_MORGULIRON.get(), Style.EMPTY.withColor(405147));
        register(context, AMMOLITE, TolkienItems.GEM_AMMOLITE.get(), Style.EMPTY.withColor(126176105));
    }

    /**
     * 1.21.4 dropped the item model index (the trim model is now keyed by the material's registry order) and keys
     * the armour look through the equipment asset instead, so each material just names its own palette.
     */
    private static void register(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> trimKey, Item trimItem, Style color) {
        TrimMaterial material = TrimMaterial.create(trimKey.identifier().getPath(), trimItem,
                Component.translatable(Util.makeDescriptionId("trim_material", trimKey.identifier())).withStyle(color), Map.of());

        context.register(trimKey, material);
    }
}
