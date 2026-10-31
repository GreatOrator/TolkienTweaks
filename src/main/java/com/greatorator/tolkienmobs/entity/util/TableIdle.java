package com.greatorator.tolkienmobs.entity.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * The small life of a body frozen at a hall table.
 * <p>
 * A hall's warden and envoy stand at their workbenches with no brain, which is what keeps them there -
 * and is also what made them read as furniture: a frozen body does not even breathe. None of that can be
 * handed back to a goal, because a goal would also walk them away from the bench, so the life is drawn
 * here instead. Each body turns slowly about the place it was set, and its head comes up from the work
 * and settles back to it, on two waves of different lengths so the cycle does not repeat where the eye can
 * follow it. A player who comes up to the counter is noticed: the body turns to them and the head comes up
 * level, holds that while they are there, and turns back to the work once they go.
 * <p>
 * What makes a body staff is having no brain and carrying one of the hall's own tags, so a hall placed
 * before this walks nothing new and needs no tag: it is staff already, and the pose simply starts.
 */
public final class TableIdle {
    /** The tag a hall's warden carries, and the one its envoy carries. */
    private static final String WARDEN = "tolkienmobs:faction_envoy";
    private static final String ENVOY = "tolkienmobs:faction_vendor";
    /** The longest turn away from where the body was set, and how far the head is tipped at work. */
    private static final float TURN = 9.0F;
    private static final float LIFT = 12.0F;
    private static final float LIFT_SWING = 10.0F;
    /** How close a player must come to be noticed, and how far round the bench they can stand. */
    private static final double NOTICE = 4.5D;
    private static final float NOTICE_ARC = 100.0F;
    /** How fast the body turns, in degrees a tick, and how level the head is when someone is served. */
    private static final float TURN_RATE = 4.0F;
    private static final float LOOK_UP = 2.0F;

    private TableIdle() {
    }

    /** One body's idle. Only the place it was set is remembered, and only so it can be swayed about. */
    public static final class Pose {
        private float placedYaw = Float.NaN;

        public void tick(Mob body) {
            if (body.level().isClientSide() || !body.isNoAi()) return;
            if (!body.entityTags().contains(WARDEN) && !body.entityTags().contains(ENVOY)) return;
            if (Float.isNaN(this.placedYaw)) this.placedYaw = body.getYRot();
            int ticks = body.tickCount;

            float aim = this.placedYaw + Mth.sin(ticks * 0.021F) * TURN;
            float lift = LIFT + Mth.sin(ticks * 0.013F + 1.7F) * LIFT_SWING;
            Player customer = this.customer(body);
            if (customer != null) {
                aim = yawToward(body, customer);
                lift = LOOK_UP;
            }
            // Always steered rather than set, so the way back from a customer is a turn and not a snap.
            float turn = approach(body.getYRot(), aim, TURN_RATE);
            body.setYRot(turn);
            body.setYHeadRot(turn);
            body.setXRot(approach(body.getXRot(), lift, TURN_RATE));
        }

        /**
         * The player standing at this body's counter, if one has come up to it. A body behind a counter
         * has a wall at its back, so a player out there is only within reach of the body's arc if it is
         * not counted, hence the test that they are round the front of it.
         */
        @Nullable
        private Player customer(Mob body) {
            Player player = body.level().getNearestPlayer(body, NOTICE);
            if (player == null || !player.isAlive() || player.isSpectator()) return null;
            float bearing = yawToward(body, player);
            return Math.abs(Mth.wrapDegrees(bearing - this.placedYaw)) <= NOTICE_ARC ? player : null;
        }
    }

    /** The yaw that looks from the body at the target, in the game's own convention. */
    private static float yawToward(Entity body, Entity target) {
        double dx = target.getX() - body.getX();
        double dz = target.getZ() - body.getZ();
        return (float) (Math.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
    }

    /** A step toward an angle, the short way round, so a turn never unwinds through a full circle. */
    private static float approach(float current, float target, float step) {
        float delta = Mth.wrapDegrees(target - current);
        if (Math.abs(delta) <= step) return target;
        return current + Math.signum(delta) * step;
    }
}
