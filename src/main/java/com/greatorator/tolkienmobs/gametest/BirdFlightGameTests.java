package com.greatorator.tolkienmobs.gametest;

import com.greatorator.tolkienmobs.entity.TolkienFlyingAmbientEntity;
import com.greatorator.tolkienmobs.entity.ai.goal.LandBirdGoal;
import com.greatorator.tolkienmobs.entity.ai.goal.RandomGlideGoal;
import com.greatorator.tolkienmobs.entity.ai.goal.RestrictedFlyingGoal;
import com.greatorator.tolkienmobs.entity.ai.goal.StartFlyingBirdGoal;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("tolkienmobs_flight")
@PrefixGameTestTemplate(false)
public class BirdFlightGameTests {
    @GameTest(template = "flight_arena", timeoutTicks = 700)
    public static void crebainFlies(GameTestHelper helper) { flightCycle(helper, TolkienEntities.ENTITY_TTM_CREBAIN.get()); }

    @GameTest(template = "flight_arena", timeoutTicks = 700)
    public static void thrushFlies(GameTestHelper helper) { flightCycle(helper, TolkienEntities.ENTITY_TTM_THRUSH.get()); }

    @GameTest(template = "flight_arena", timeoutTicks = 700)
    public static void eagleFlies(GameTestHelper helper) { flightCycle(helper, TolkienEntities.ENTITY_TTM_GREAT_EAGLE.get()); }

    @GameTest(template = "flight_arena", timeoutTicks = 700)
    public static void gwahirFlies(GameTestHelper helper) { flightCycle(helper, TolkienEntities.ENTITY_TTM_GWAHIR.get()); }

    private static void flightCycle(GameTestHelper helper, EntityType<? extends TolkienFlyingAmbientEntity> type) {
        for (int x = 0; x < 24; x++) for (int z = 0; z < 24; z++) {
            helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
        }
        var bird = helper.spawn(type, new BlockPos(5, 2, 12));
        bird.setPersistenceRequired();
        bird.goalSelector.removeAllGoals(goal -> true);
        bird.targetSelector.removeAllGoals(goal -> true);
        bird.goalSelector.addGoal(0, new StartFlyingBirdGoal(bird, 1));
        bird.goalSelector.addGoal(1, new RandomGlideGoal(bird));
        Vec3 origin = bird.getFlightOrigin();
        helper.onEachTick(() -> bird.setNoActionTime(0));
        helper.runAtTickTime(65, () -> {
            helper.assertTrue(bird.getY() > origin.y + 2 && !bird.onGround(), type + " must physically take off");
            bird.goalSelector.getAvailableGoals().stream().map(wrapped -> wrapped.getGoal())
                    .filter(goal -> goal instanceof StartFlyingBirdGoal).toList().forEach(bird.goalSelector::removeGoal);
            // A deterministic destination exercises real navigation alongside the glide goal.
            var wander = new RestrictedFlyingGoal(bird, 1.0, 64) {
                @Override
                protected Vec3 getPosition() { return origin.add(5, 8, 0); }
            };
            wander.setInterval(1);
            bird.goalSelector.addGoal(2, wander);
        });
        helper.runAtTickTime(400, () -> {
            helper.assertTrue(bird.distanceToSqr(origin.add(5, 8, 0)) < 9,
                    type + " must navigate while the glide animation goal is active: " + bird.position() + " origin=" + origin + " flying=" + bird.getFlying() + " navDone=" + bird.getNavigation().isDone());
            bird.goalSelector.getAvailableGoals().stream().map(wrapped -> wrapped.getGoal())
                    .filter(goal -> goal instanceof RestrictedFlyingGoal).toList().forEach(bird.goalSelector::removeGoal);
            bird.goalSelector.addGoal(0, new LandBirdGoal(bird, 1));
        });
        helper.runAtTickTime(680, () -> {
            helper.assertTrue(bird.onGround() && !bird.getFlying() && !bird.isNoGravity(), type + " must land and restore gravity");
            bird.discard();
            helper.succeed();
        });
    }

    @GameTest(template = "flight_arena", timeoutTicks = 650)
    public static void normalGoalsTakeOffAndRespectSitting(GameTestHelper helper) {
        for (int x = 0; x < 24; x++) for (int z = 0; z < 24; z++) {
            helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
        }
        var birds = java.util.List.of(
                helper.spawn(TolkienEntities.ENTITY_TTM_CREBAIN.get(), new BlockPos(4, 2, 4)),
                helper.spawn(TolkienEntities.ENTITY_TTM_THRUSH.get(), new BlockPos(4, 2, 18)),
                helper.spawn(TolkienEntities.ENTITY_TTM_GREAT_EAGLE.get(), new BlockPos(16, 2, 4)),
                helper.spawn(TolkienEntities.ENTITY_TTM_GWAHIR.get(), new BlockPos(16, 2, 18)));
        double[] peaks = new double[4];
        for (var bird : birds) bird.getRandom().setSeed(1234);
        helper.onEachTick(() -> {
            for (int i = 0; i < birds.size(); i++) {
                var bird = birds.get(i);
                bird.setNoActionTime(0);
                peaks[i] = Math.max(peaks[i], bird.getY() - bird.getFlightOrigin().y);
            }
        });
        helper.runAtTickTime(600, () -> {
            for (int i = 0; i < birds.size(); i++) {
                helper.assertTrue(peaks[i] > 2, birds.get(i).getType() + " must take off with its normal goals");
            }
            birds.get(1).setOrderedToSit(true);
        });
        helper.runAtTickTime(620, () -> {
            var thrush = birds.get(1);
            helper.assertTrue(!thrush.getFlying() && !new StartFlyingBirdGoal(thrush, 1).canUse(),
                    "Sitting must prevent takeoff");
            birds.forEach(bird -> bird.discard());
            helper.succeed();
        });
    }

    @GameTest(template = "flight_arena")
    public static void flightBoundsAndSave(GameTestHelper helper) {
        for (var type : java.util.List.of(TolkienEntities.ENTITY_TTM_CREBAIN.get(), TolkienEntities.ENTITY_TTM_THRUSH.get(),
                TolkienEntities.ENTITY_TTM_GREAT_EAGLE.get(), TolkienEntities.ENTITY_TTM_GWAHIR.get())) {
            var bird = (TolkienFlyingAmbientEntity) helper.spawn(type, new BlockPos(4, 4, 4));
            Vec3 origin = bird.getFlightOrigin();
            Vec3 target = bird.restrictFlightTarget(origin.add(200, 200, 200));
            helper.assertTrue(Math.abs(target.subtract(origin).horizontalDistance() - 64) < 1.0E-6, "64-block horizontal radius");
            helper.assertTrue(target.y == origin.y + 100, "100-block ceiling from spawn");
            bird.setFlying(true);
            bird.getMoveControl().setWantedPosition(origin.x + 200, origin.y + 200, origin.z, 1);
            helper.assertTrue(bird.getMoveControl().getWantedX() == origin.x + 64, "Controller must bound all goals");
            bird.setPos(origin.add(63, 99, 0));
            bird.move(MoverType.SELF, new Vec3(4, 4, 0));
            helper.assertTrue(bird.getX() <= origin.x + 64 && bird.getY() <= origin.y + 100, "Inertia must not cross flight limits");
            CompoundTag saved = new CompoundTag();
            bird.addAdditionalSaveData(saved);
            var restored = (TolkienFlyingAmbientEntity) type.create(helper.getLevel());
            restored.setPos(origin.add(30, 30, 0));
            restored.readAdditionalSaveData(saved);
            helper.assertTrue(restored.getFlightOrigin().equals(origin), "Reload must preserve original home");
            helper.assertTrue(restored.getFlying() && restored.isNoGravity(), "Reload must preserve flight");
            bird.discard();
        }
        helper.succeed();
    }
}
