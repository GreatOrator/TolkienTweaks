package com.greatorator.tolkienmobs.entity.projectiles;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class TornadoEntity extends Entity implements GeoAnimatable {
    private static final EntityDataAccessor<Boolean> BLAST = SynchedEntityData.defineId(TornadoEntity.class,
            EntityDataSerializers.BOOLEAN);
        public int lifeTime;
    public static EntityDimensions blastDimensions = EntityDimensions.scalable(0.75F, 2.0F);

    public TornadoEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public TornadoEntity(Level worldIn) {
        super(TolkienEntities.AMMO_TORNADO.get(), worldIn);
    }

    @Override
    public EntityDimensions getDimensions(Pose p_213305_1_) {
        return this.isBlast() ? blastDimensions : super.getDimensions(p_213305_1_);
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }

    @Override
    public void baseTick() {
        super.baseTick();

        this.refreshDimensions();

        if (!this.isBlast()) {
            List<Entity> list = this.level.getEntities(this, this.getBoundingBox(), Entity::isAlive);
            if (!list.isEmpty()) {
                for (Entity entity : list) {
                    if(entity instanceof LivingEntity){
                        LivingEntity livingEntity = (LivingEntity)entity;
                        if (this.lifeTime == 15) {
                            livingEntity.push(0, 1.5, 0);
                        }
                    }

                }
            }

            if (this.lifeTime >= 15 && this.lifeTime < 30) {
                if (this.level.isClientSide) {
                    for (int i = 0; i < 3; i++) {
                        this.level.addParticle(TolkienParticleTypes.WIND_PARTICLE.get(), this.getRandomX(0.5D), this.getRandomY() - 2, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 1.0D, 5, (this.random.nextDouble() - 0.5D) * 1.0D);
                    }
                }
            }
        }

        this.lifeTime ++;

        int removeTime = this.isBlast() ? 14 : 36;

        if (this.lifeTime >= removeTime && !this.level.isClientSide) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(BLAST, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    public boolean isBlast() {
        return this.entityData.get(BLAST);
    }

    public void setBlast(boolean attached) {
        this.entityData.set(BLAST, attached);
    }

    /**
     * Animations
     */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 1, (event) -> {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("lift"));
                return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
