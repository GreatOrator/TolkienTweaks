package com.greatorator.tolkienmobs.entity.projectiles;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SimpleTrapEntity extends BaseTrapEntity{
    private static final EntityDataAccessor<Integer> TRAP_TYPE = SynchedEntityData.defineId(SimpleTrapEntity.class,
            EntityDataSerializers.INT);


    public SimpleTrapEntity(EntityType<? extends SimpleTrapEntity> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TRAP_TYPE, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_70037_1_) {
        this.setTrapType(p_70037_1_.getInt("TrapType"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_213281_1_) {
        p_213281_1_.putInt("TrapType", this.getTrapType());
    }

    public int getTrapType() {
        return Mth.clamp(this.entityData.get(TRAP_TYPE), 0, 1);
    }

    public void setTrapType(int attached) {
        this.entityData.set(TRAP_TYPE, attached);
    }

    @Override
    public boolean canTrapEntity(LivingEntity entity) {
        if (this.getTrapType() == 0) {
            return super.canTrapEntity(entity) && (entity instanceof Spider);
        } else if (this.getTrapType() == 1) {
            return super.canTrapEntity(entity);
        } else {
            return super.canTrapEntity(entity);
        }
    }

    /**
     * Animations
     */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 1, (event) -> {
            if (this.getTrapType() == 0) {
                if (this.spawnAnimationTick > 0) {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("web_trap_spawn"));
                }
            } else {

            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public int getSpawnAnimationLength() {
        return this.getTrapType() == 1 ? 8 : 7;
    }

    @Override
    public int getDecayAnimationLength() {
        return 25;
    }
}
