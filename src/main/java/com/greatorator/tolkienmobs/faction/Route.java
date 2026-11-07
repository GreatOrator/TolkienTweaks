package com.greatorator.tolkienmobs.faction;

/**
 * One build's road, in the numbers the pass needs.
 * <p>
 * A road is not a generic thing the way a drape is. The drape can read any cut's own outline and know what to do; a
 * road has to be told where it starts, and then what it has to reach and how it gets there - numbers a surveyor
 * walks and writes down, not numbers the build carries. They live in {@link Roads}, one instance per build, so that a
 * second city brings its own road with one line rather than a second copy of the road's code.
 * <p>
 * There are two kinds, because there are two things a road can have to be told about. A road that crosses water needs
 * the crossing spelled out - which banks the span lies between, how wide it is, where the deck sits and which pieces
 * stand it up - and that is a {@link Crossing}. A road with no water on it needs none of that: the build's entry
 * point and the far end, and the grade between them, which is a {@link Riverless}. Both are found by the same search
 * and laid by the same pass; only the crossing has anything to stand up.
 */
public sealed interface Route {
    /** The build it belongs to, for a log line rather than for a lookup. */
    String id();

    /** The build's own footprint, which the road leaves from and may not cross. */
    int cityX0();

    int cityX1();

    int cityZ0();

    int cityZ1();

    /** Where the road starts: the last block of the build's path, at the city end. */
    int pathX();

    int pathZ();

    /**
     * A road that crosses water, which is the bridge.
     *
     * @param eastBankX   the bank the road arrives on, and the west one it leaves by, with the span between them
     * @param westBankX   the far bank, where the road leaves the water
     * @param bridgeZ     the line the span lies on, which both approaches are forced onto
     * @param half        how many blocks the bridge is either side of its line
     * @param bridgeBase  the base a bridge middle is laid at, and the end's own base, one higher
     * @param endBase     the abutments' base, one over the middles' so their slabs meet the planks within half a block
     * @param deckY       the walking surface at the abutments, which the grade has to arrive on
     * @param runIn       how much of the approach is forced straight onto the span's own line
     * @param exitReach   how far the road runs on past the far abutment before the country has it
     * @param endPiece    the abutment piece, and the straight span between them, in the jar's structure folder
     * @param middlePiece the span piece, which is as long as the gap between the two banks allows
     */
    record Crossing(String id, int cityX0, int cityX1, int cityZ0, int cityZ1,
                    int pathX, int pathZ, int eastBankX, int westBankX, int bridgeZ, int half,
                    int bridgeBase, int endBase, int deckY, int runIn, int exitReach,
                    String endPiece, String middlePiece) implements Route {
    }

    /**
     * A road with no water on it: two ends and the grade between them, and nothing else.
     * <p>
     * This is the ordinary road between two builds - a camp and the next camp up the valley, a road joining a town to
     * the one across the downs - and it is exactly the numbers a scout walks: where the road leaves the build, and
     * the block it ends at. The search finds the way between them the way it always did, and the grade smooths that
     * line and arrives on the ground at the far end, since there is no deck to level onto and nothing to bridge.
     *
     * @param farX the block the road ends at, out in the country
     * @param farZ the same, along the other axis
     */
    record Riverless(String id, int cityX0, int cityX1, int cityZ0, int cityZ1,
                     int pathX, int pathZ, int farX, int farZ) implements Route {
    }
}
