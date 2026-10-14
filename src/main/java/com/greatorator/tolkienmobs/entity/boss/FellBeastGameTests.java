package com.greatorator.tolkienmobs.entity.boss;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.entity.DragonEntity;
import com.greatorator.tolkienmobs.entity.ai.goal.dragon.DefendHomeGoal;
import com.greatorator.tolkienmobs.entity.ai.goal.dragon.FlyerWanderGoal;
import com.greatorator.tolkienmobs.entity.ai.goal.dragon.MoveToHomeGoal;
import com.greatorator.tolkienmobs.entity.projectiles.FellBeastFireballEntity;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.ArrayList;
import java.util.List;

@GameTestHolder("tolkienmobs_fellbeast")
@PrefixGameTestTemplate(false)
public class FellBeastGameTests {
    @GameTest(template = "fellbeast_arena")
    public static void spawnHomeRidersAndPersistence(GameTestHelper helper) {
        var beast = beast(helper, new BlockPos(8, 10, 8));
        BlockPos home = beast.blockPosition();
        beast.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(home), MobSpawnType.SPAWN_EGG, null);
        helper.assertTrue(home.equals(beast.getHomePos()), "Finalize spawn must capture the positioned spawn, even with its rider");
        helper.assertTrue(beast.getFirstPassenger() instanceof WitchKingEntity && beast.getControllingPassenger() == null && !beast.canBeControlledByRider(),
                "Witch King passenger must leave fellbeast AI in control");
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        beast.ejectPassengers();
        player.startRiding(beast);
        helper.assertTrue(beast.canBeControlledByRider() && home.equals(beast.getHomePos()), "Player control must retain the spawn home");
        player.stopRiding();
        beast.setLeashedTo(player, false);
        beast.dropLeash(false, false);
        helper.assertTrue(home.equals(beast.getHomePos()), "Dropping a leash must retain home");
        beast.moveTo(beast.getX() + 9, beast.getY(), beast.getZ() - 7);
        var tag = new CompoundTag();
        beast.save(tag);
        var restored = (FellBeastEntity) EntityType.loadEntityRecursive(tag, helper.getLevel(), entity -> entity);
        helper.assertTrue(restored != null && home.equals(restored.getHomePos()), "Reload must retain original home after movement");
        restored.setNoAi(true);
        restored.tick();
        helper.assertTrue(home.equals(restored.getHomePos()), "First loaded tick must not move an existing home");
        tag.remove("HomePos");
        var legacy = (FellBeastEntity) EntityType.loadEntityRecursive(tag, helper.getLevel(), entity -> entity);
        BlockPos legacyPosition = legacy.blockPosition();
        legacy.setNoAi(true);
        legacy.tick();
        helper.assertTrue(legacyPosition.equals(legacy.getHomePos()), "Legacy saves without home must adopt their loaded position");
        beast.setHomePos(BlockPos.ZERO);
        var zero = new CompoundTag();
        beast.addAdditionalSaveData(zero);
        restored.readAdditionalSaveData(zero);
        helper.assertTrue(restored.hasRestriction() && BlockPos.ZERO.equals(restored.getHomePos()), "World origin is a valid saved home");
        var mutable = home.mutable();
        beast.setHomePos(mutable);
        mutable.move(1, 2, 3);
        helper.assertTrue(home.equals(beast.getHomePos()), "Home must not track later changes to a mutable position");
        helper.succeed();
    }

    @GameTest(template = "fellbeast_arena")
    public static void radiusWanderingAndReturnCoordinates(GameTestHelper helper) {
        var beast = beast(helper, new BlockPos(8, 30, 8));
        beast.setHomePos(beast.blockPosition());
        BlockPos home = beast.getHomePos();
        int radius = TolkienMobsConfig.homeRadius;
        helper.assertTrue(beast.getRestrictRadius() == radius && beast.isWithinRestriction(home.offset(radius, 0, 0))
                && !beast.isWithinRestriction(home.offset(radius + 1, 0, 0)), "Radius API must be linear, with one squared distance comparison");
        beast.setNavigator(DragonEntity.NavigationType.FLYING);
        var wander = new FlyerWanderGoal(beast, 1);
        int candidates = 0;
        for (int i = 0; i < 100; i++) {
            Vec3 candidate = wander.getPosition();
            if (candidate == null) continue;
            candidates++;
            helper.assertTrue(beast.isWithinRestriction(BlockPos.containing(candidate)), "Wander target escaped the home sphere");
        }
        helper.assertTrue(candidates > 0, "Wandering must still produce valid positions");
        beast.setPos(home.getX() + radius + 8.5, home.getY(), home.getZ() + 0.5);
        helper.getLevel().getChunkAt(beast.blockPosition());
        var returning = new MoveToHomeGoal(beast);
        helper.assertTrue(returning.canUse(), "Out-of-range beast must return");
        Vec3 before = beast.position();
        returning.start();
        returning.tick();
        helper.assertTrue(before.equals(beast.position()), "Return must walk/fly before using a timeout teleport");
        BlockPos target = beast.getNavigation().getTargetPos();
        helper.assertTrue(target != null && Math.abs(target.getZ() - home.getZ()) <= 1 && target.getX() < beast.getX(),
                "Return waypoint must lead home using Z, not the Y coordinate");
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.startRiding(beast);
        helper.assertTrue(!returning.canUse() && !returning.canContinueToUse() && !wander.canUse(), "Player must not fight autonomous movement");
        player.stopRiding();
        returning.stop();
        returning.start();
        for (int i = 0; i < 599; i++) returning.tick();
        helper.assertTrue(before.equals(beast.position()), "A stalled return must wait the full timeout before teleporting");
        returning.tick();
        helper.assertTrue(beast.position().equals(Vec3.atBottomCenterOf(home)), "A stalled return must safely recover to its saved home");
        returning.stop();
        helper.succeed();
    }

    @GameTest(template = "fellbeast_arena", timeoutTicks = 750)
    public static void physicallyReturnsHome(GameTestHelper helper) {
        checkPhysicalReturn(helper, false);
    }

    @GameTest(template = "fellbeast_arena", timeoutTicks = 750)
    public static void physicallyReturnsHomeWithWitchKing(GameTestHelper helper) {
        checkPhysicalReturn(helper, true);
    }

    private static void checkPhysicalReturn(GameTestHelper helper, boolean withRider) {
        ServerLevel level = helper.getLevel();
        // Test both isolated navigation and the full AI with its usual NPC passenger.
        var beast = withRider ? new TestBeast(level) : new ReturningBeast(level);
        BlockPos home = helper.absolutePos(new BlockPos(8, withRider ? 160 : 80, 8));
        beast.moveTo(Vec3.atBottomCenterOf(home));
        if (withRider) beast.finalizeSpawn(level, level.getCurrentDifficultyAt(home), MobSpawnType.SPAWN_EGG, null);
        else beast.setHomePos(home);
        beast.moveTo(home.getX() + TolkienMobsConfig.homeRadius + 8.5, home.getY(), home.getZ() + 0.5);
        beast.setNavigator(DragonEntity.NavigationType.FLYING);
        Vec3 initialPosition = beast.position();
        double initialDistance = beast.distanceToSqr(Vec3.atBottomCenterOf(home));
        List<ChunkPos> newlyForced = new ArrayList<>();
        for (ChunkPos chunk : List.of(new ChunkPos(home), new ChunkPos(beast.blockPosition()))) {
            if (!level.getForcedChunks().contains(chunk.toLong())) {
                level.setChunkForced(chunk.x, chunk.z, true);
                newlyForced.add(chunk);
            }
        }
        level.addFreshEntityWithPassengers(beast);
        long[] arrivedAt = {-1};
        helper.onEachTick(() -> {
            if (arrivedAt[0] < 0 && beast.distanceToSqr(Vec3.atBottomCenterOf(home)) <= 9) arrivedAt[0] = beast.tickCount;
        });
        helper.runAtTickTime(120, () -> {
            helper.assertTrue(!beast.hasGenericMeleeGoal(), "Humanoid melee must not override animated attacks or home return");
            helper.assertTrue(beast.distanceToSqr(Vec3.atBottomCenterOf(home)) < initialDistance - 25,
                    "Flight must make real progress toward home before teleport timeout: " + initialPosition + " -> " + beast.position() + ", ticks=" + beast.tickCount + ", speed=" + beast.getSpeed() + ", input=" + beast.zza + ", nav=" + beast.getNavigationType() + ", done=" + beast.getNavigation().isDone() + ", yaw=" + beast.getYRot() + ", target=" + beast.getNavigation().getTargetPos());
        });
        helper.runAtTickTime(680, () -> {
            helper.assertTrue(arrivedAt[0] >= 0 && arrivedAt[0] < 600,
                    "Beast must physically reach home before the teleport timeout; arrival=" + arrivedAt[0] + ", position=" + beast.position());
            beast.getPassengers().forEach(Entity::discard);
            beast.discard();
            for (ChunkPos chunk : newlyForced) level.setChunkForced(chunk.x, chunk.z, false);
            helper.succeed();
        });
    }

    @GameTest(template = "fellbeast_arena")
    public static void targetingMeleeAndCancelledQueues(GameTestHelper helper) {
        var beast = beast(helper, new BlockPos(10, 3, 10));
        beast.setHomePos(beast.blockPosition());
        beast.setYRot(0);
        beast.setYHeadRot(0);
        var target = EntityType.COW.create(helper.getLevel());
        target.moveTo(beast.getX(), beast.getY(), beast.getZ() + 3);
        helper.getLevel().addFreshEntity(target);
        helper.assertTrue(!beast.isAlliedTo(target) && beast.canAttack(target), "Unrelated living entities must not all be allies");
        var rider = TolkienEntities.ENTITY_TTM_WITCHKING.get().create(helper.getLevel());
        rider.startRiding(beast);
        helper.assertTrue(beast.isAlliedTo(rider) && beast.canAttack(target), "NPC rider must be protected without disabling attacks");
        beast.setTarget(target);
        var attack = beast.new DragonAttackGoal(beast);
        helper.assertTrue(attack.canUse(), "Valid nearby target should allow attacks");
        attack.start();
        for (int i = 0; i < 13; i++) attack.tick();
        helper.assertTrue(target.getHealth() < target.getMaxHealth(), "Queued melee attack must deal damage");
        beast.setTarget(null);
        attack.stop();
        attack.tick();
        helper.assertTrue(!beast.getBreathingFire() && !beast.isAggressive(), "Target loss must safely clear queued attacks and breath");
        rider.stopRiding();
        var enemy = EntityType.ZOMBIE.create(helper.getLevel());
        enemy.moveTo(beast.getX() + 5, beast.getY(), beast.getZ());
        helper.getLevel().addFreshEntity(enemy);
        var defense = new DefendHomeGoal(beast);
        boolean found = false;
        for (int i = 0; i < 100 && !found; i++) found = defense.canUse();
        helper.assertTrue(found, "Home defense should find a nearby hostile mob");
        defense.start();
        helper.assertTrue(beast.getTarget() != null, "Home defense must assign its selected target");
        enemy.moveTo(beast.getRestrictCenter().getX() + TolkienMobsConfig.homeRadius + 10, beast.getY(), beast.getZ());
        helper.assertTrue(!beast.canAttack(enemy), "Targets outside home territory must not freeze wandering or start pursuit");
        defense.stop();
        target.discard();
        enemy.discard();
        helper.succeed();
    }

    @GameTest(template = "fellbeast_arena")
    public static void aimedBreathAndProjectilePersistence(GameTestHelper helper) {
        var beast = beast(helper, new BlockPos(8, 10, 8));
        beast.setHomePos(beast.blockPosition());
        beast.setNoAi(true);
        beast.setYRot(0);
        beast.setYHeadRot(0);
        var target = EntityType.COW.create(helper.getLevel());
        target.moveTo(beast.getX(), beast.getY(), beast.getZ() + 15);
        helper.getLevel().addFreshEntity(target);
        beast.setTarget(target);
        beast.setBreathingFire(true);
        beast.breathTimer.set(1);
        for (int i = 0; i < 11; i++) beast.aiStep();
        var balls = helper.getLevel().getEntitiesOfClass(FellBeastFireballEntity.class, beast.getBoundingBox().inflate(30), ball -> ball.getOwner() == beast);
        helper.assertTrue(balls.size() == 2, "Charged breath must be paced, not emit every tick: " + balls.size());
        for (var ball : balls) {
            Vec3 expected = target.getEyePosition().subtract(ball.position()).normalize();
            helper.assertTrue(ball.getDeltaMovement().length() > 1 && ball.getDeltaMovement().normalize().dot(expected) > 0.98,
                    "Fireball must be launched from the mouth toward its target");
            var saved = new CompoundTag();
            ball.saveWithoutId(saved);
            var loaded = TolkienEntities.AMMO_FELLBEAST_FIREBALL.get().create(helper.getLevel());
            loaded.load(saved);
            helper.assertTrue(loaded.getPickupItemStackOrigin().is(TolkienItems.FELLBEAST_FIREBALL.get()), "Projectile item must survive loading");
        }
        balls.forEach(Entity::discard);
        beast.setTarget(null);
        beast.aiStep();
        helper.assertTrue(!beast.getBreathingFire(), "Losing the target must stop breathing even with a rider");
        target.discard();
        helper.succeed();
    }

    @GameTest(template = "fellbeast_arena")
    public static void yawAltitudeAttributesAndSafeTeleport(GameTestHelper helper) {
        var beast = beast(helper, new BlockPos(8, 10, 8));
        beast.setYRot(80);
        helper.assertTrue(beast.getYRot() == 80, "Turn setter must change actual yaw");
        helper.assertTrue(beast.getScaledAttributes().length == 3, "Scaled attributes must contain actual attributes rather than holders");
        beast.setPos(beast.getX(), -30, beast.getZ());
        helper.assertTrue(beast.getAltitude() > 1, "Altitude scan must work below Y zero");
        BlockPos home = helper.absolutePos(new BlockPos(16, 5, 16));
        helper.getLevel().getChunkAt(home);
        helper.getLevel().setBlockAndUpdate(home.above(), Blocks.STONE.defaultBlockState());
        Vec3 before = beast.position();
        helper.assertTrue(!beast.trySafeTeleport(home) && before.equals(beast.position()), "Blocked home must not cause suffocation teleport");
        helper.getLevel().setBlockAndUpdate(home.above(), Blocks.AIR.defaultBlockState());
        helper.assertTrue(beast.trySafeTeleport(home) && beast.position().equals(Vec3.atBottomCenterOf(home)), "Clear home must use correct centered collision check");
        helper.succeed();
    }

    private static FellBeastEntity beast(GameTestHelper helper, BlockPos relative) {
        var beast = TolkienEntities.ENTITY_FELL_BEAST.get().create(helper.getLevel());
        BlockPos pos = helper.absolutePos(relative);
        beast.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        return beast;
    }

    private static class TestBeast extends FellBeastEntity {
        TestBeast(ServerLevel level) { super(TolkienEntities.ENTITY_FELL_BEAST.get(), level); }
        boolean hasGenericMeleeGoal() {
            return goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.getGoal() instanceof net.minecraft.world.entity.ai.goal.MeleeAttackGoal);
        }
    }

    private static class ReturningBeast extends TestBeast {
        ReturningBeast(ServerLevel level) { super(level); }
        @Override
        protected void registerGoals() { goalSelector.addGoal(4, new MoveToHomeGoal(this)); }
    }
}
