package com.greatorator.tolkienmobs.faction;

/**
 * One build's road, in the numbers the pass needs.
 * <p>
 * A road is not a generic thing the way a drape is. The drape can read any cut's own outline and know what to do; a
 * road has to be told where it starts, which banks a bridge spans, how wide the span is and where the deck sits -
 * numbers a surveyor walks and writes down, not numbers the build carries. They live in {@link Roads}, one instance
 * per build, so that a second city brings its own road with one line rather than a second copy of the road's code.
 *
 * @param id          the build it belongs to, for a log line rather than for a lookup
 * @param cityX0      the build's own footprint, which the road leaves from and may not cross
 * @param cityZ0      the footprint's near corner, north and west
 * @param pathX       where the road starts: the last block of the build's path, at the city end
 * @param pathZ       the same, along the other axis
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
public record Route(String id, int cityX0, int cityX1, int cityZ0, int cityZ1,
                    int pathX, int pathZ, int eastBankX, int westBankX, int bridgeZ, int half,
                    int bridgeBase, int endBase, int deckY, int runIn, int exitReach,
                    String endPiece, String middlePiece) {
}
