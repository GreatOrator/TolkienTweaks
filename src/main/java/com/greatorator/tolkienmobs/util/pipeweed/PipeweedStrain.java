package com.greatorator.tolkienmobs.util.pipeweed;

import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * The three leaves a Shire pipe can be packed with. Each one is a crop of its own, so a farm tells
 * them apart by what was planted, and each burns to its own shape, its own smoke and its own short,
 * small comfort.
 *
 * <p>The smoke is drawn from the crop art itself: the flower each strain opens at its last stage is
 * the colour its plume comes out, lifted a third of the way to that flower's pale centre so the
 * smoke reads as smoke rather than as a puff of paint. A pipe therefore blows the same pink, salmon
 * or violet the plant flowers in, and the same pair of colours its leaf icon carries.
 */
public enum PipeweedStrain {
    /** The common Shire leaf, the one every hobbit grows along the garden wall. */
    LONGBOTTOM(TolkienItems.PIPEWEED_ITEM, PipeSmoke.Shape.RING, MobEffects.ABSORPTION, 200, 0,
            0xd488a4, 0xf0bcd0),
    /** The oldest and sweetest, said to have come from the Southfarthing. */
    OLD_TOBY(TolkienItems.PIPEWEED_OLDTOBY_ITEM, PipeSmoke.Shape.SHIP, MobEffects.REGENERATION, 200, 0,
            0xdb9c74, 0xf4caa3),
    /** A thin, sharp leaf from the southern marches of the Shire. */
    SOUTHLINCH(TolkienItems.PIPEWEED_SOUTHLINCH_ITEM, PipeSmoke.Shape.DRAGON, MobEffects.SATURATION, 120, 0,
            0xc68fb6, 0xe8bcd9);

    /** How far a plume is lifted from the flower's own colour toward its pale centre. */
    private static final float PALE = 0.35F;

    private final DeferredItem<Item> leaf;
    private final PipeSmoke.Shape shape;
    private final Holder<MobEffect> effect;
    private final int duration;
    private final int amplifier;
    private final int smoke;

    PipeweedStrain(DeferredItem<Item> leaf, PipeSmoke.Shape shape, Holder<MobEffect> effect, int duration, int amplifier,
                   int bloom, int bloomHeart) {
        this.leaf = leaf;
        this.shape = shape;
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.smoke = pale(bloom, bloomHeart);
    }

    /** A strain's blossom colour lifted toward its pale centre, as a packed RGB int. */
    private static int pale(int bloom, int heart) {
        int red = mix((bloom >> 16) & 0xFF, (heart >> 16) & 0xFF);
        int green = mix((bloom >> 8) & 0xFF, (heart >> 8) & 0xFF);
        int blue = mix(bloom & 0xFF, heart & 0xFF);
        return red << 16 | green << 8 | blue;
    }

    private static int mix(int from, int to) {
        return Math.round(from + (to - from) * PALE);
    }

    public Item leafItem() {
        return leaf.get();
    }

    public PipeSmoke.Shape shape() {
        return shape;
    }

    public Holder<MobEffect> effect() {
        return effect;
    }

    /** The colour this strain's smoke is drawn in, as packed RGB. */
    public int smoke() {
        return smoke;
    }

    public int duration() {
        return duration;
    }

    public int amplifier() {
        return amplifier;
    }

    /** The strain the smoker is carrying, or null when they have none of the three. */
    public static PipeweedStrain find(Player player) {
        for (PipeweedStrain strain : values()) {
            if (!stackOf(player, strain).isEmpty()) {
                return strain;
            }
        }
        return null;
    }

    /** Takes one leaf of the strain off the player; the pipe has already checked there is one. */
    public static void consume(Player player, PipeweedStrain strain) {
        ItemStack stack = stackOf(player, strain);
        if (!stack.isEmpty()) {
            stack.consume(1, player);
        }
    }

    private static ItemStack stackOf(Player player, PipeweedStrain strain) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(strain.leafItem())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
