package com.greatorator.tolkienmobs.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * The seat a bench hands a player who right-clicks it.
 * <p>
 * It is an entity rather than a block state because a rider needs something to ride, and it is
 * deliberately the least an entity can be: no model, nothing synched, nothing saved, and no way to be
 * hurt. It is spawned by the block at the height of the bench's own seat, ridden, and killed the
 * moment its rider gets off - so a bench never leaves a seat standing in the world, and a seat is
 * never found anywhere the bench is not.
 * <p>
 * Nothing is written to disk: a seat that is not being sat on does not exist, so a save never has to be
 * repaired after one is left behind by a crash.
 */
public class SeatEntity extends Entity {
    public SeatEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
    }

    /** A seat is furniture: nothing can break it, so nothing can knock its rider off it. */
    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!level().isClientSide()) {
            this.kill((ServerLevel) level());
        }
    }
}
