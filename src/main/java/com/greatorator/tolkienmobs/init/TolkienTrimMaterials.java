package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.util.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public class TolkienTrimMaterials {
    public static final ResourceKey<TrimMaterial> MITHRIL = registerKey("mithril");
    public static final ResourceKey<TrimMaterial> MORGULIRON = registerKey("morguliron");
    public static final ResourceKey<TrimMaterial> AMMOLITE = registerKey("ammolite");

    private static ResourceKey<TrimMaterial> registerKey(String name) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, TolkienMobsMain.prefix(name));
    }

    public static void bootstrap(BootstrapContext<TrimMaterial> context) {
        register(context, MITHRIL, Style.EMPTY.withColor(217225241));
        register(context, MORGULIRON, Style.EMPTY.withColor(405147));
        register(context, AMMOLITE, Style.EMPTY.withColor(126176105));
    }

    /**
     * 26.1 builds a trim material from a palette name and a description; the material item the old factory took only
     * ever reached an unused parameter. The palette name stays the registry path - "mithril" and its neighbours -
     * which is what resolves to the mod's own {@code textures/trims/color_palettes/<name>.png}.
     */
    private static void register(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> trimKey, Style color) {
        TrimMaterial material = new TrimMaterial(
                MaterialAssetGroup.create(trimKey.identifier().getPath()),
                Component.translatable(Util.makeDescriptionId("trim_material", trimKey.identifier())).withStyle(color));

        context.register(trimKey, material);
    }
}
