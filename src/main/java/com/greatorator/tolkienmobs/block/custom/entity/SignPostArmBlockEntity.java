package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.faction.Places;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * What an arm of a sign post is pointing at. A single board has one destination; the double arm has two, one per
 * board, and the second is simply never used by the single. Each holds the name of a place this world has planted
 * and the straight-line distance from the post to it, worked out here on the server rather than trusted from
 * whoever set it.
 */
public class SignPostArmBlockEntity extends TolkienBlockEntity {
    /** Nothing chosen yet, and so nothing drawn. */
    public static final int NO_DISTANCE = -1;
    /** How many boards one arm can carry: the double arm is the most. */
    public static final int BOARDS = 2;

    private final String[] destinations = {"", ""};
    private final int[] distances = {NO_DISTANCE, NO_DISTANCE};

    public SignPostArmBlockEntity(BlockPos pos, BlockState state) {
        super(TolkienBlocks.SIGN_POST_ARM_BLOCK_ENTITY.get(), pos, state);
    }

    /** The place this arm points at, empty for none. */
    public String getDestination() {
        return this.getDestination(0);
    }

    /** How many blocks away that place is from the post, or {@link #NO_DISTANCE} for none. */
    public int getDistance() {
        return this.getDistance(0);
    }

    /** The place the board at {@code board} points at, 0 being the lower or only board. */
    public String getDestination(int board) {
        return this.destinations[board(board)];
    }

    /** How far away that board's place is, or {@link #NO_DISTANCE} for none. */
    public int getDistance(int board) {
        return this.distances[board(board)];
    }

    /**
     * Moves one board on through the places this world has actually planted, one step at a time, and measures the
     * new one from where the post stands. A board pointing at a place since removed falls back to the start of the
     * list rather than breaking.
     */
    public void stepDestination(int board, int step) {
        int at = board(board);
        List<String> places = this.destinations();
        int here = places.indexOf(this.destinations[at]);
        if (here < 0) here = 0;
        this.destinations[at] = places.get(Math.floorMod(here + step, places.size()));
        this.measure(at);
        this.markDirtyClient();
    }

    /** Steps the only board a single arm has. */
    public void stepDestination(int step) {
        this.stepDestination(0, step);
    }

    /** Sets a board directly, for a sign placed by a command or a structure rather than a hand. */
    public void setDestination(int board, @Nullable String place) {
        int at = board(board);
        this.destinations[at] = place == null ? "" : place;
        this.measure(at);
        this.markDirtyClient();
    }

    /** Sets the only board a single arm has. */
    public void setDestination(@Nullable String place) {
        this.setDestination(0, place);
    }

    private void measure(int at) {
        this.distances[at] = NO_DISTANCE;
        if (this.destinations[at].isEmpty() || this.level == null) return;

        Places.Place place = Places.place(this.level, this.destinations[at]);
        if (place == null) return;
        this.distances[at] = (int) Math.round(Math.sqrt(place.centre().distSqr(this.worldPosition)));
    }

    private List<String> destinations() {
        List<String> places = new ArrayList<>();
        places.add("");
        if (this.level == null) return places;
        // The towns a world has actually stood, and nothing else: a board is read by somebody walking a road, and the
        // catalogue also carries barrows, fords and a scout's fire, which a board has no room to say are not towns.
        for (Places.Place town : Places.towns(this.level)) {
            places.add(town.id());
        }
        return places;
    }

    private static int board(int board) {
        return Math.max(0, Math.min(BOARDS - 1, board));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("destination", this.destinations[0]);
        output.putInt("distance", this.distances[0]);
        output.putString("destination2", this.destinations[1]);
        output.putInt("distance2", this.distances[1]);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.destinations[0] = input.getStringOr("destination", "");
        this.distances[0] = input.getIntOr("distance", NO_DISTANCE);
        this.destinations[1] = input.getStringOr("destination2", "");
        this.distances[1] = input.getIntOr("distance2", NO_DISTANCE);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
