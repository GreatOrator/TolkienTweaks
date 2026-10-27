package com.greatorator.tolkienmobs.entity;

import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import net.minecraft.util.Util;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

public class TolkienWanderingEntity extends WanderingTrader implements GeoEntity {
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(TolkienWanderingEntity.class, EntityDataSerializers.INT);

    /** Vanilla's own length for a wandering trader's visit: two in-game days, or forty minutes. */
    private static final int VISIT_TICKS = 48000;

    public TolkienWanderingEntity(EntityType<? extends TolkienWanderingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.50D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    /**
     * VARIANT
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
    }

    private int getTypeVariant() {
        return this.entityData.get(VARIANT);
    }

    public TolkienVariant getTolkienVariant() {
        return TolkienVariant.byId(this.getTypeVariant() & 255);
    }

    protected void setVariant(TolkienVariant variant) {
        this.entityData.set(VARIANT, variant.getId() & 255);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(VARIANT, input.getIntOr("Variant", 0));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
        TolkienVariant variant = Util.getRandom(TolkienVariant.values(), this.random);
        this.setVariant(variant);
        // A trader the world put here - out of a country's spawn list, or as that country's chunk was
        // built - is a visit and not a resident. Vanilla gives its own wandering trader two in-game
        // days and takes it away when they are up, and that is the only thing that keeps a country
        // from filling with merchants, since these never leave on their own account. A trader a
        // player spawns, places or summons keeps vanilla's behaviour instead, which is to stay.
        if (spawnType == EntitySpawnReason.NATURAL || spawnType == EntitySpawnReason.CHUNK_GENERATION) {
            this.setDespawnDelay(VISIT_TICKS);
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    /**
     * A trader that is left behind does not stay behind. Vanilla's wandering trader never leaves on
     * distance, which suits the one its own spawner sends after a player now and then, but these are
     * handed to a country's spawn list: without this, the merchants of a country collect around
     * whoever stands in it and hold the creature cap the country's animals need.
     */
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return true;
    }


    /**
     * Animations
     *
     * GeckoLib 5 builds the controllers without the animatable and hands their handlers an
     * AnimationTest, so the two clips are built once here instead of once per tick.
     */
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<TolkienWanderingEntity>("Idle", 1, test ->
                !test.isMoving() && !this.isAggressive() ? test.setAndContinue(IDLE) : PlayState.STOP));
        controllers.add(new AnimationController<TolkienWanderingEntity>("Walk", 1, test ->
                test.isMoving() ? test.setAndContinue(WALK) : PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
