package com.greatorator.tolkienmobs.faction;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * How much of the dark a player can stand, read off the courage they have earned.
 * <p>
 * The terror of the Nazgul, of the Witch-king and of the Watcher stones is not a difficulty setting:
 * it is the point of them, and a player who has done nothing to deserve standing up to it should be
 * pinned to the spot by it. What changes is the player. Every rung of this ladder takes exactly one
 * thing away from the terror, in the order the fear itself gives way: first the legs, then the eyes,
 * then the strength, and at last the whole of it - so courage is felt as a series of specific mercies
 * rather than as a number that was quietly multiplied somewhere.
 * <p>
 * Above {@link #FEARLESS} the crippling terror does not land at all, and above {@link #UNBOWED} the
 * dread aura of the Watcher and the Spider does not either. What is left of a Watcher's regard at that
 * point is its wither: a gaze that no longer frightens still hurts.
 * <p>
 * The thresholds are counted in deeds, which is why this ladder is long. A rung is not meant to arrive
 * in an afternoon.
 */
public enum Courage {
    /** Nothing earned: the terror pins, blinds and weakens, exactly as it was written. */
    FAINTHEARTED("fainthearted", 0, 0x8A8A8A, ChatFormatting.GRAY),
    /** The first mercy: the legs answer again, at the price of a heavy, noticeable slowness. */
    TREMBLING("trembling", 6, 0xB08A5A, ChatFormatting.YELLOW),
    /** The eyes: the terror still slows and weakens, but the world no longer goes dark. */
    STEADY("steady", 18, 0x62B45B, ChatFormatting.GREEN),
    /** The strength: only a weakness is left of it, so a sword still swings, just not as hard. */
    STOUTHEARTED("stouthearted", 40, 0x4FB8C9, ChatFormatting.AQUA),
    /** The terror itself: a Nazgul's scream no longer takes hold of anything. */
    FEARLESS("fearless", 75, 0xD9A441, ChatFormatting.GOLD),
    /** The aura as well: a Watcher's dread is refused, and only its wither still lands. */
    UNBOWED("unbowed", 130, 0xEADFA8, ChatFormatting.GOLD);

    /** The most courage a player can hold; a score, bounded only to keep it out of integer trouble. */
    public static final int CEILING = 100_000;

    private final String id;
    private final int threshold;
    private final int colour;
    private final ChatFormatting chat;

    Courage(String id, int threshold, int colour, ChatFormatting chat) {
        this.id = id;
        this.threshold = threshold;
        this.colour = colour;
        this.chat = chat;
    }

    public String id() {
        return id;
    }

    /** The deeds this rung begins at. */
    public int threshold() {
        return threshold;
    }

    /** The colour the ledger draws this rung in, as 0xRRGGBB. */
    public int colour() {
        return colour;
    }

    public ChatFormatting chat() {
        return chat;
    }

    public Component displayName() {
        return Component.translatable("courage.tolkienmobs." + id);
    }

    /** Whether the terror still holds a player still. Only the faint-hearted are pinned. */
    public boolean pinned() {
        return this == FAINTHEARTED;
    }

    /** The slowness that stands in for the pin, as a potion amplifier, or -1 where there is none. */
    public int slowness() {
        return switch (this) {
            case TREMBLING, STEADY -> 0;
            default -> -1;
        };
    }

    /** Whether the terror still blinds. From {@link #STEADY} up the world stays lit. */
    public boolean blinded() {
        return this == FAINTHEARTED || this == TREMBLING;
    }

    /** Whether the terror still weakens. It is the last of its teeth to go. */
    public boolean weakened() {
        return !refusesTerror();
    }

    /** Whether the crippling terror fails to land at all. */
    public boolean refusesTerror() {
        return this == FEARLESS || this == UNBOWED;
    }

    /** Whether the dread aura fails to land at all, leaving only what it does to the body. */
    public boolean refusesDread() {
        return this == UNBOWED;
    }

    /** Whether a player at this rung is no longer held still by the terror, which the HUD calls out. */
    public boolean shrugsOffParalysis() {
        return !pinned();
    }

    /** The rung a courage total falls in. */
    public static Courage of(int deeds) {
        Courage best = FAINTHEARTED;
        for (Courage stage : values()) {
            if (deeds >= stage.threshold) best = stage;
        }
        return best;
    }

    /** The next rung up, or null at the top of the ladder. */
    public Courage next() {
        Courage[] all = values();
        int next = ordinal() + 1;
        return next < all.length ? all[next] : null;
    }

    /** How far through this rung a total is, 0..1, for the ledger's bar. */
    public float progress(int deeds) {
        Courage next = next();
        if (next == null) return 1.0F;
        int span = next.threshold - threshold;
        return Math.max(0.0F, Math.min(1.0F, (deeds - threshold) / (float) span));
    }
}
