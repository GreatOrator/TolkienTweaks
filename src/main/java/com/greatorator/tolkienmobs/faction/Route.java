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
     * <p>
     * The banks are points rather than an x and a z, because a span crosses the water it finds and water does not
     * always run the way the ford's does: north of the crossing the Bruinen turns east across the road, so the same
     * two pieces are stood a quarter turn and the span lies along z. A span is on one axis - both banks share a
     * coordinate - and the coordinate they share is the line both approaches are forced onto.
     *
     * @param bankX       the bank the road arrives on: the near side, where the approach comes in
     * @param bankZ       the same, along the other axis
     * @param farX        the bank the road leaves by, across the water
     * @param farZ        the same, along the other axis
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
                    int pathX, int pathZ, int bankX, int bankZ, int farX, int farZ, int half,
                    int bridgeBase, int endBase, int deckY, int runIn, int exitReach,
                    String endPiece, String middlePiece) implements Route {
        public Crossing {
            if (bankX != farX && bankZ != farZ) throw new IllegalArgumentException("a span is on one axis: " + id);
            if (bankX == farX && bankZ == farZ) throw new IllegalArgumentException("a span needs two banks: " + id);
        }

        /** Whether the span lies along x, which is how the ford's does: the banks differ in x and share a z. */
        public boolean alongX() {
            return bankZ == farZ;
        }

        /** The near bank's coordinate along the span's own axis, and the far bank's. */
        public int bank() {
            return alongX() ? bankX : bankZ;
        }

        public int far() {
            return alongX() ? farX : farZ;
        }

        /** The coordinate both banks share: the line the two approaches run in on. */
        public int line() {
            return alongX() ? bankZ : bankX;
        }

        /** One block along the span towards the far bank, which is the direction its pieces are stood in. */
        public int step() {
            return Integer.signum(far() - bank());
        }

        /** A station of the span, from a coordinate along it and an offset across it. */
        public int x(int along, int across) {
            return alongX() ? along : line() + across;
        }

        public int z(int along, int across) {
            return alongX() ? line() + across : along;
        }
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
