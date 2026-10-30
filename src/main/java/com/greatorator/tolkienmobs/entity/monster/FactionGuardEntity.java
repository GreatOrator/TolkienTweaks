package com.greatorator.tolkienmobs.entity.monster;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import com.greatorator.tolkienmobs.entity.TolkienMonsterEntity;
import com.greatorator.tolkienmobs.faction.Alliance;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * The soldier a country sends after a player it will not have.
 * <p>
 * One entity carries every alliance's guard: the alliance decides the skin set, and a variant picks one of
 * that set. Each alliance paints its own folder - {@code faction_west} with eight liveries,
 * {@code faction_east} with six and {@code faction_harad} with six - so a soldier's face says which side he
 * is on before he draws.
 * <p>
 * Two details differ from the rest of the mod's hostiles. A guard is a soldier, not a night creature, so
 * the sun goals are left out and one will happily stand in daylight. And a guard is placed by a country
 * rather than by a spawn list, so it arrives at full strength with a sword in its hand.
 */
public class FactionGuardEntity extends TolkienMonsterEntity implements GeoEntity {
    private static final EntityDataAccessor<Integer> ALLIANCE =
            SynchedEntityData.defineId(FactionGuardEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(FactionGuardEntity.class, EntityDataSerializers.INT);

    private static final int WEST_SKINS = 8;
    private static final int EAST_SKINS = 6;
    private static final int HARAD_SKINS = 6;

    public FactionGuardEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 6.0D);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.arm();
    }

    /**
     * Puts the alliance's blade in the guard's hand.
     * <p>
     * 26.1's {@code Mob.finalizeSpawn} no longer calls {@code populateDefaultEquipmentSlots}, so a mob that
     * is placed rather than naturally spawned - which is every guard, since a faction musters them - would
     * arrive empty-handed, and so would one summoned with a command. Arming on the first tick covers every
     * way in at once; a guard who already has a blade keeps it.
     */
    private void arm() {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapon()));
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide() && this.getMainHandItem().isEmpty()) {
            this.arm();
        }
        super.tick();
    }

    /** A guard carries the blade its own alliance arms it with: ammolite in the West, morgul iron east and south. */
    private net.minecraft.world.item.Item weapon() {
        return switch (alliance()) {
            case WEST -> com.greatorator.tolkienmobs.init.TolkienItems.SWORD_AMMOLITE.get();
            case EAST, HARAD -> com.greatorator.tolkienmobs.init.TolkienItems.SWORD_MORGULIRON.get();
        };
    }

    // ---- alliance and skin ----

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ALLIANCE, Alliance.WEST.ordinal());
        builder.define(VARIANT, 0);
    }

    public Alliance alliance() {
        return Alliance.values()[Math.floorMod(this.entityData.get(ALLIANCE), Alliance.values().length)];
    }

    /** Set by whatever musters the guard, which also rolls a skin from that alliance's own set. */
    public void setAlliance(Alliance alliance) {
        this.entityData.set(ALLIANCE, alliance.ordinal());
        this.setVariant(this.random.nextInt(skins(alliance)));
    }

    public int variant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Math.floorMod(variant, skins(this.alliance())));
    }

    /** How many skins an alliance's guards are drawn from. */
    public static int skins(Alliance alliance) {
        return switch (alliance) {
            case WEST -> WEST_SKINS;
            case EAST -> EAST_SKINS;
            case HARAD -> HARAD_SKINS;
        };
    }

    /** The folder an alliance's guard skins live in: one folder per alliance, named for it. */
    public static Identifier skinFolder(Alliance alliance) {
        return Identifier.fromNamespaceAndPath("tolkienmobs", "textures/entity/faction_" + alliance.id() + "/faction");
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Alliance", this.entityData.get(ALLIANCE));
        output.putInt("Variant", this.variant());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(ALLIANCE, input.getIntOr("Alliance", Alliance.WEST.ordinal()));
        this.setVariant(input.getIntOr("Variant", 0));
        this.reassessWeaponGoal();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        EntitySpawnReason spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    // ---- animations ----

    private static final RawAnimation WALK = RawAnimation.begin().thenPlay("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation ATTACK = RawAnimation.begin().then("attack", LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<FactionGuardEntity>("Walk", 1, test ->
                test.isMoving() ? test.setAndContinue(WALK) : PlayState.STOP));
        controllers.add(new AnimationController<FactionGuardEntity>("Idle", 1, test ->
                test.isMoving() ? PlayState.STOP : test.setAndContinue(IDLE)));
        controllers.add(new AnimationController<FactionGuardEntity>("Attack", 1, test -> {
            if (test.controller().isAnimatingBones()) return PlayState.CONTINUE;
            return this.swinging ? test.setAndContinue(ATTACK) : PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
