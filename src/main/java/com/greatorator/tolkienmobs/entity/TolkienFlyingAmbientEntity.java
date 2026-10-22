package com.greatorator.tolkienmobs.entity;

import com.greatorator.tolkienmobs.entity.ai.control.FreeFlightMoveControl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
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
        if (!level().isClientSide) {
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
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        Vec3 origin = getFlightOrigin();
        tag.putDouble("FlightOriginX", origin.x);
        tag.putDouble("FlightOriginY", origin.y);
        tag.putDouble("FlightOriginZ", origin.z);
        tag.putBoolean("BirdFlying", getFlying());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("FlightOriginX") && tag.contains("FlightOriginY") && tag.contains("FlightOriginZ")) {
            flightOrigin = new Vec3(tag.getDouble("FlightOriginX"), tag.getDouble("FlightOriginY"), tag.getDouble("FlightOriginZ"));
        }
        setFlying(tag.getBoolean("BirdFlying"));
    }
}
