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
 * What an arm of a sign post is pointing at. The arm holds the name of a place this world has
 * planted, and the straight-line distance from the post to it, worked out here on the server rather
 * than trusted from whoever set it.
 */
public class SignPostArmBlockEntity extends TolkienBlockEntity {
    /** Nothing chosen yet, and so nothing drawn. */
    public static final int NO_DISTANCE = -1;

    private String destination = "";
    private int distance = NO_DISTANCE;

    public SignPostArmBlockEntity(BlockPos pos, BlockState state) {
        super(TolkienBlocks.SIGN_POST_ARM_BLOCK_ENTITY.get(), pos, state);
    }

    /** The place this arm points at, empty for none. */
    public String getDestination() {
        return this.destination;
    }

    /** How many blocks away that place is from the post, or {@link #NO_DISTANCE} for none. */
    public int getDistance() {
        return this.distance;
    }

    /**
     * Moves the arm on through the places this world has actually planted, one step at a time, and
     * measures the new one from where the post stands. An arm that was pointing at a place since
     * removed falls back to the start of the list rather than breaking.
     */
    public void stepDestination(int step) {
        List<String> places = this.destinations();
        int at = places.indexOf(this.destination);
        if (at < 0) at = 0;
        this.destination = places.get(Math.floorMod(at + step, places.size()));
        this.measure();
        this.markDirtyClient();
    }

    /** Sets the arm directly, for a sign placed by a command or a structure rather than a hand. */
    public void setDestination(@Nullable String place) {
        this.destination = place == null ? "" : place;
        this.measure();
        this.markDirtyClient();
    }

    private void measure() {
        this.distance = NO_DISTANCE;
        if (this.destination.isEmpty() || this.level == null) return;

        Places.Place place = Places.place(this.level, this.destination);
        if (place == null) return;
        this.distance = (int) Math.round(Math.sqrt(place.centre().distSqr(this.worldPosition)));
    }

    private List<String> destinations() {
        List<String> places = new ArrayList<>();
        places.add("");
        for (Places.Name name : Places.catalogue()) {
            if (places.contains(name.id())) continue;
            if (this.level == null || Places.place(this.level, name.id()) == null) continue;
            places.add(name.id());
        }
        return places;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("destination", this.destination);
        output.putInt("distance", this.distance);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.destination = input.getStringOr("destination", "");
        this.distance = input.getIntOr("distance", NO_DISTANCE);
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
