package com.greatorator.tolkienmobs.faction;

import net.minecraft.ChatFormatting;

/**
 * Where a player stands with one alliance, read off their own point total.
 * <p>
 * {@link #HOSTILE} sits below the ladder rather than on it: a point total under zero is a country
 * that will not have you, and its guards muster. Everything from {@link #OUTSIDER} up is earned.
 */
public enum FactionRank {
    HOSTILE("hostile", -100, 0x9B2C2C, ChatFormatting.DARK_RED),
    OUTSIDER("outsider", 0, 0x8A8A8A, ChatFormatting.GRAY),
    STRANGER("stranger", 100, 0xD8D8D8, ChatFormatting.WHITE),
    GUEST("guest", 300, 0x62B45B, ChatFormatting.GREEN),
    FRIEND("friend", 700, 0x4FB8C9, ChatFormatting.AQUA),
    KINSMAN("kinsman", 1500, 0xD9A441, ChatFormatting.GOLD);

    /** One token handed to a hall is worth this many points. */
    public static final int TOKEN_VALUE = 10;
    /** One enemy soldier killed is worth this many points to each alliance that is not its own. */
    public static final int KILL_VALUE = 5;
    /** The lowest a standing can sink to, which is where a country turns on you. */
    public static final int FLOOR = -100;

    private final String id;
    private final int threshold;
    private final int colour;
    private final ChatFormatting chat;

    FactionRank(String id, int threshold, int colour, ChatFormatting chat) {
        this.id = id;
        this.threshold = threshold;
        this.colour = colour;
        this.chat = chat;
    }

    public String id() {
        return id;
    }

    /** The colour the ledger draws this rank's name and bar in, as 0xRRGGBB. */
    public int colour() {
        return colour;
    }

    /** The point total this rank begins at. */
    public int threshold() {
        return threshold;
    }

    public ChatFormatting chat() {
        return chat;
    }

    public boolean earned() {
        return this != HOSTILE;
    }

    /** The rank a total falls in. Anything under zero is hostile, however far under. */
    public static FactionRank of(int standing) {
        if (standing < 0) return HOSTILE;
        FactionRank best = OUTSIDER;
        for (FactionRank rank : values()) {
            if (rank.earned() && standing >= rank.threshold) best = rank;
        }
        return best;
    }

    /** The next rank up, or empty at the top of the ladder. */
    public FactionRank next() {
        FactionRank[] all = values();
        for (int i = ordinal() + 1; i < all.length; i++) {
            if (all[i].earned()) return all[i];
        }
        return null;
    }

    /**
     * How far through this rank a total is, 0..1, for the ledger's bar. A hostile standing fills the
     * bar down from zero instead, so a country's displeasure reads as a bar that shrinks.
     */
    public float progress(int standing) {
        if (this == HOSTILE) {
            return Math.max(0.0F, Math.min(1.0F, (standing - FLOOR) / (float) -FLOOR));
        }
        FactionRank next = next();
        if (next == null) return 1.0F;
        int span = next.threshold - threshold;
        return Math.max(0.0F, Math.min(1.0F, (standing - threshold) / (float) span));
    }
}
