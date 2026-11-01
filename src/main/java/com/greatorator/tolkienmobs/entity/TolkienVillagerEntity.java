package com.greatorator.tolkienmobs.entity;

import com.greatorator.tolkienmobs.entity.util.TableIdle;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import net.minecraft.util.Util;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;

public class TolkienVillagerEntity extends Villager implements GeoEntity {
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(TolkienVillagerEntity.class, EntityDataSerializers.INT);

    public TolkienVillagerEntity(EntityType<? extends TolkienVillagerEntity> entityType, Level level) {
        super(entityType, level);
    }

    /** The hall idle: the yaw this body was placed at, captured the first time it is asked. */
    private final TableIdle.Pose tablePose = new TableIdle.Pose();

    @Override
    public void tick() {
        super.tick();
        this.tablePose.tick(this);
        // A body of the mod's folk that has been given a home keeps to it, whatever kind of folk it is.
        // The valley and the camp both place their people with a home area rather than on a leash, and a
        // villager's stroll is written for a village: without this the walk carries them out of the gate
        // and off across the country.
        if (!this.level().isClientSide() && this.hasHome()) {
            this.keepInsideHome();
        }
    }

    /**
     * Keeps a body that has been given a home area inside it. A villager stroll is written for a village
     * and not for a room: with no meeting point it walks toward the nearest one, which from a hall means
     * out of the door and off across the country. Rather than take the stroll away, the target it asks for
     * is pulled back inside the home circle, so the walk keeps its direction and the body keeps its room;
     * one that has already stepped outside is sent back to the middle.
     */
    protected void keepInsideHome() {
        Vec3 middle = Vec3.atBottomCenterOf(this.getHomePosition());
        var wanted = this.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
        if (wanted.isPresent()) {
            Vec3 asked = wanted.get().getTarget().currentPosition();
            if (!this.isWithinHome(asked)) {
                this.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                        new WalkTarget(this.pulledInside(middle, asked), 0.5F, 1));
            }
        }
        if (!this.isWithinHome()) {
            this.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(middle, 0.6F, 1));
        }
    }

    /** A point on the way to the target, stopped short of the home circle edge. */
    protected Vec3 pulledInside(Vec3 middle, Vec3 target) {
        double dx = target.x - middle.x;
        double dz = target.z - middle.z;
        double distance = Math.sqrt(dx * dx + dz * dz);
        double limit = Math.max(1.0D, this.getHomeRadius() - 1.0D);
        if (distance <= limit || distance < 1.0E-4D) {
            return target;
        }
        double scale = limit / distance;
        return new Vec3(middle.x + dx * scale, target.y, middle.z + dz * scale);
    }

    /**
     * What one of the mod's folk is called. 26.1 asks getName for a type name and a villager answers
     * with its profession's name, so one that holds no trade introduces itself as a villager - which
     * named every dwarf, hobbit, elf, man, southron and orc of the mod a villager until it happened to
     * take a job, and left the ones standing in a village that never found their work block without a
     * name of their own. Only that case is answered here: a trade still names itself the way vanilla
     * names it, which is also the title of the screen it trades in, and a dwarf without one is a dwarf.
     */
    @Override
    protected Component getTypeName() {
        if (this.getVillagerData().profession().is(VillagerProfession.NONE)) {
            return this.getType().getDescription();
        }
        return super.getTypeName();
    }

    @Nonnull
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
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
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    /**
     * Animations
     *
     * GeckoLib 5 builds the controllers without the animatable and hands their handlers an
     * AnimationTest, so the three clips are built once here instead of once per tick.
     */
    private static final RawAnimation SITTING = RawAnimation.begin().thenPlay("sitting");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<TolkienVillagerEntity>("Idle", 1, test -> {
            if (this.isPassenger()) {
                return test.setAndContinue(SITTING);
            }
            if (!test.isMoving() && !this.isAggressive()) {
                return test.setAndContinue(IDLE);
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<TolkienVillagerEntity>("Walk", 1, test ->
                test.isMoving() ? test.setAndContinue(WALK) : PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
