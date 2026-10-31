package com.greatorator.tolkienmobs.entity.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

/**
 * The small life of a body frozen at a hall table.
 * <p>
 * A hall's warden and envoy stand at their workbenches with no brain, which is what keeps them there -
 * and is also what made them read as furniture: a frozen body does not even breathe. None of that can be
 * handed back to a goal, because a goal would also walk them away from the bench, so the life is drawn
 * here instead. Each body turns slowly about the place it was set, and its head comes up from the work
 * and settles back to it, on two waves of different lengths so the cycle does not repeat where the eye can
 * follow it.
 * <p>
 * What makes a body staff is having no brain and carrying one of the hall's own tags, so a hall placed
 * before this walks nothing new and needs no tag: it is staff already, and the pose simply starts.
 */
public final class TableIdle {
    /** The tag a hall's warden carries, and the one its envoy carries. */
    private static final String WARDEN = "tolkienmobs:faction_envoy";
    private static final String ENVOY = "tolkienmobs:faction_vendor";
    /** The longest turn away from where the body was set, and how far the head is tipped at rest. */
    private static final float TURN = 9.0F;
    private static final float LIFT = 12.0F;
    private static final float LIFT_SWING = 10.0F;

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
            float turn = this.placedYaw + Mth.sin(ticks * 0.021F) * TURN;
            float lift = LIFT + Mth.sin(ticks * 0.013F + 1.7F) * LIFT_SWING;
            body.setYRot(turn);
            body.setYHeadRot(turn);
            body.setXRot(lift);
        }
    }
}
