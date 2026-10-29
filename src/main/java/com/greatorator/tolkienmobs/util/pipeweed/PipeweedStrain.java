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
 * them apart by what was planted, and each burns to its own shape and its own short, small comfort.
 */
public enum PipeweedStrain {
    /** The common Shire leaf, the one every hobbit grows along the garden wall. */
    LONGBOTTOM(TolkienItems.PIPEWEED_ITEM, PipeSmoke.Shape.RING, MobEffects.ABSORPTION, 200, 0),
    /** The oldest and sweetest, said to have come from the Southfarthing. */
    OLD_TOBY(TolkienItems.PIPEWEED_OLDTOBY_ITEM, PipeSmoke.Shape.SHIP, MobEffects.REGENERATION, 200, 0),
    /** A thin, sharp leaf from the southern marches of the Shire. */
    SOUTHLINCH(TolkienItems.PIPEWEED_SOUTHLINCH_ITEM, PipeSmoke.Shape.DRAGON, MobEffects.SATURATION, 120, 0);

    private final DeferredItem<Item> leaf;
    private final PipeSmoke.Shape shape;
    private final Holder<MobEffect> effect;
    private final int duration;
    private final int amplifier;

    PipeweedStrain(DeferredItem<Item> leaf, PipeSmoke.Shape shape, Holder<MobEffect> effect, int duration, int amplifier) {
        this.leaf = leaf;
        this.shape = shape;
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
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
