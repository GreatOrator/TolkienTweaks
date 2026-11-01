package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.ArrayList;
import java.util.List;

/**
 * The three alliances' heraldry: a field, and the patterns cut into it.
 * <p>
 * A side's colours are drawn the way a loom draws them - a base colour, then a stack of pattern layers,
 * each in a dye - which is all a banner or a shield actually is. Keeping the designs here rather than in a
 * texture means the same four lines describe everything a side puts its mark on: the shields the guards at
 * the hall doors wear, and the banners hung either side of those same hall doorways.
 * <p>
 * The layers are in the order they were applied, because that order is the design. A border cut before a
 * flower is a border the flower covers; the same two layers the other way round is a flower framed. So the
 * list is kept exactly as it was made and never sorted or condensed.
 * <p>
 * The three are told apart by more than colour, which is what makes them read at a distance: the West is
 * blue under a white skull and a crowned flower, the East is black under a white flower inside a rhombus
 * and a curly border, and Harad is grey under a light rhombus and a red-and-orange circle.
 */
public final class Heraldry {
    /** One layer of a design: the pattern, and the dye it is cut in. */
    private record Layer(String pattern, DyeColor colour) {
    }

    /** Blue field, white skull, a crowned flower, and a border twice cut. */
    private static final List<Layer> WEST = List.of(
            new Layer("triangle_bottom", DyeColor.WHITE),
            new Layer("stripe_left", DyeColor.BLUE),
            new Layer("stripe_right", DyeColor.BLUE),
            new Layer("skull", DyeColor.WHITE),
            new Layer("border", DyeColor.WHITE),
            new Layer("border", DyeColor.BLUE),
            new Layer("flower", DyeColor.WHITE),
            new Layer("flower", DyeColor.WHITE));

    /** Black field, a white flower in a rhombus over small stripes, curling round a bottom triangle. */
    private static final List<Layer> EAST = List.of(
            new Layer("flower", DyeColor.WHITE),
            new Layer("rhombus", DyeColor.WHITE),
            new Layer("small_stripes", DyeColor.BLACK),
            new Layer("triangle_bottom", DyeColor.WHITE),
            new Layer("curly_border", DyeColor.BLACK),
            new Layer("border", DyeColor.BLACK));

    /** Grey field, a light rhombus and bar, orange and yellow flowers under a red-and-orange circle. */
    private static final List<Layer> HARAD = List.of(
            new Layer("rhombus", DyeColor.LIGHT_GRAY),
            new Layer("stripe_middle", DyeColor.LIGHT_GRAY),
            new Layer("flower", DyeColor.ORANGE),
            new Layer("flower", DyeColor.YELLOW),
            new Layer("circle", DyeColor.RED),
            new Layer("circle", DyeColor.ORANGE));

    private Heraldry() {
    }

    /** The field a side's design is cut into. */
    public static DyeColor field(Alliance alliance) {
        return switch (alliance) {
            case WEST -> DyeColor.BLUE;
            case EAST -> DyeColor.BLACK;
            case HARAD -> DyeColor.GRAY;
        };
    }

    /**
     * The shield a hall's guard wears at its door: the side's own field, with the side's own design on it.
     * A shield is a banner worn on an arm, so it takes the very same layers and needs no design of its own.
     */
    public static ItemStack shield(Level level, Alliance alliance) {
        ItemStack shield = new ItemStack(Items.SHIELD);
        shield.set(DataComponents.BASE_COLOR, field(alliance));
        shield.set(DataComponents.BANNER_PATTERNS, layers(level, alliance));
        return shield;
    }

    private static List<Layer> design(Alliance alliance) {
        return switch (alliance) {
            case WEST -> WEST;
            case EAST -> EAST;
            case HARAD -> HARAD;
        };
    }

    /**
     * The design as the game holds it. Patterns live in their own registry, so a layer is a reference to a
     * pattern and a dye; a pattern this build does not have is skipped rather than written as a hole, which
     * keeps an older or newer pack's design from arriving as a blank banner.
     */
    private static BannerPatternLayers layers(Level level, Alliance alliance) {
        HolderLookup.RegistryLookup<BannerPattern> registry =
                level.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN);
        List<BannerPatternLayers.Layer> built = new ArrayList<>();
        for (Layer layer : design(alliance)) {
            ResourceKey<BannerPattern> key = ResourceKey.create(Registries.BANNER_PATTERN,
                    Identifier.withDefaultNamespace(layer.pattern()));
            registry.get(key).ifPresent(holder ->
                    built.add(new BannerPatternLayers.Layer(holder, layer.colour())));
        }
        return new BannerPatternLayers(List.copyOf(built));
    }
}
