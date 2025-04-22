package com.greatorator.tolkienmobs.entity.item;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;

import javax.annotation.Nullable;
import java.util.Optional;

public class MorgulCrystalEntity extends Entity{
    private static final EntityDataAccessor<Optional<BlockPos>> DATA_BEAM_TARGET;
    private static final EntityDataAccessor<Boolean> DATA_SHOW_BOTTOM;
    public int time;

    public MorgulCrystalEntity(EntityType<? extends MorgulCrystalEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
        this.time = this.random.nextInt(100000);
    }

    public MorgulCrystalEntity(Level level, double x, double y, double z) {
        this(TolkienEntities.MORGUL_CRYSTAL.get(), level);
        this.setPos(x, y, z);
    }

    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BEAM_TARGET, Optional.empty());
        builder.define(DATA_SHOW_BOTTOM, true);
    }

    public void tick() {
        ++this.time;
        this.checkInsideBlocks();
        this.handlePortal();
        if (this.level() instanceof ServerLevel) {
            BlockPos blockpos = this.blockPosition();
            if (((ServerLevel)this.level()).getDragonFight() != null && this.level().getBlockState(blockpos).isAir()) {
                this.level().setBlockAndUpdate(blockpos, BaseFireBlock.getState(this.level(), blockpos));
            }
        }

    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.getBeamTarget() != null) {
            compound.put("beam_target", NbtUtils.writeBlockPos(this.getBeamTarget()));
        }

        compound.putBoolean("ShowBottom", this.showsBottom());
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        NbtUtils.readBlockPos(compound, "beam_target").ifPresent(this::setBeamTarget);
        if (compound.contains("ShowBottom", 1)) {
            this.setShowBottom(compound.getBoolean("ShowBottom"));
        }

    }

    public boolean isPickable() {
        return true;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.getEntity() instanceof EnderDragon) {
            return false;
        } else {
            if (!this.isRemoved() && !this.level().isClientSide) {
                this.remove(Entity.RemovalReason.KILLED);
                if (!source.is(DamageTypeTags.IS_EXPLOSION)) {
                    DamageSource damagesource = source.getEntity() != null ? this.damageSources().explosion(this, source.getEntity()) : null;
                    this.level().explode(this, damagesource, (ExplosionDamageCalculator)null, this.getX(), this.getY(), this.getZ(), 6.0F, false, Level.ExplosionInteraction.BLOCK);
                }

                this.onDestroyedBy(source);
            }

            return true;
        }
    }

    public void kill() {
        this.onDestroyedBy(this.damageSources().generic());
        super.kill();
    }

    private void onDestroyedBy(DamageSource source) {
        if (this.level() instanceof ServerLevel) {
//            EndDragonFight enddragonfight = ((ServerLevel)this.level()).getDragonFight();
//            if (enddragonfight != null) {
//                enddragonfight.onCrystalDestroyed(this, source);
//            }
        }

    }

    public void setBeamTarget(@Nullable BlockPos beamTarget) {
        this.getEntityData().set(DATA_BEAM_TARGET, Optional.ofNullable(beamTarget));
    }

    @Nullable
    public BlockPos getBeamTarget() {
        return (BlockPos)((Optional)this.getEntityData().get(DATA_BEAM_TARGET)).orElse((Object)null);
    }

    public void setShowBottom(boolean showBottom) {
        this.getEntityData().set(DATA_SHOW_BOTTOM, showBottom);
    }

    public boolean showsBottom() {
        return (Boolean)this.getEntityData().get(DATA_SHOW_BOTTOM);
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return super.shouldRenderAtSqrDistance(distance) || this.getBeamTarget() != null;
    }

    public ItemStack getPickResult() {
        return new ItemStack(Items.END_CRYSTAL);
    }

    static {
        DATA_BEAM_TARGET = SynchedEntityData.defineId(MorgulCrystalEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
        DATA_SHOW_BOTTOM = SynchedEntityData.defineId(MorgulCrystalEntity.class, EntityDataSerializers.BOOLEAN);
    }
}
