package com.greatorator.tolkienmobs.entity;

import com.greatorator.tolkienmobs.entity.ai.control.FreeFlightMoveControl;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/** Shared flight state and a persistent home for the four birds. */
public class TolkienFlyingAmbientEntity extends TolkienAmbientEntity {
    public static final double FLIGHT_RADIUS = 64.0;
    public static final double FLIGHT_HEIGHT = 100.0;
    private Vec3 flightOrigin;

    public TolkienFlyingAmbientEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new FreeFlightMoveControl(this);
    }

    public Vec3 getFlightOrigin() {
        // Constructors run before spawn positioning. Existing saves acquire a home on first tick.
        if (flightOrigin == null) flightOrigin = position();
        return flightOrigin;
    }

    public Vec3 restrictFlightTarget(Vec3 target) {
        Vec3 origin = getFlightOrigin();
        double dx = target.x - origin.x;
        double dz = target.z - origin.z;
        double distance = Math.sqrt(dx * dx + dz * dz);
        double scale = distance > FLIGHT_RADIUS ? FLIGHT_RADIUS / distance : 1.0;
        return new Vec3(origin.x + dx * scale,
                Math.min(target.y, Math.min(origin.y + FLIGHT_HEIGHT, level().getMaxY() - getBbHeight())),
                origin.z + dz * scale);
    }

    @Override
    public void aiStep() {
        if (!level().isClientSide()) {
            getFlightOrigin();
            if (isOrderedToSit()) {
                getNavigation().stop();
                setFlying(false);
            }
        }
        super.aiStep();
    }

    @Override
    public void setFlying(boolean flying) {
        super.setFlying(flying);
        setNoGravity(flying);
        if (!flying) setGliding(false);
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        if (type == MoverType.SELF && getFlying()) {
            Vec3 destination = position().add(movement);
            Vec3 limited = restrictFlightTarget(destination);
            // Do not teleport birds brought outside their home by a player or another mod.
            if (position().distanceToSqr(restrictFlightTarget(position())) < 1.0E-6) {
                movement = limited.subtract(position());
            } else {
                Vec3 homeward = restrictFlightTarget(position()).subtract(position());
                movement = homeward.normalize().scale(Math.min(movement.length(), homeward.length()));
            }
        }
        super.move(type, movement);
    }

    @Override
    public boolean shouldTryTeleportToOwner() {
        return !getFlying() && super.shouldTryTeleportToOwner();
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        Vec3 origin = getFlightOrigin();
        output.putDouble("FlightOriginX", origin.x);
        output.putDouble("FlightOriginY", origin.y);
        output.putDouble("FlightOriginZ", origin.z);
        output.putBoolean("BirdFlying", getFlying());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        if (input.read("FlightOriginX", Codec.DOUBLE).isPresent() && input.read("FlightOriginY", Codec.DOUBLE).isPresent() && input.read("FlightOriginZ", Codec.DOUBLE).isPresent()) {
            flightOrigin = new Vec3(input.getDoubleOr("FlightOriginX", 0.0), input.getDoubleOr("FlightOriginY", 0.0), input.getDoubleOr("FlightOriginZ", 0.0));
        }
        setFlying(input.getBooleanOr("BirdFlying", false));
    }
}
