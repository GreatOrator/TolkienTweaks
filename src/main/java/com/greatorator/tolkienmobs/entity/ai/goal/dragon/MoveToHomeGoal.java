package com.greatorator.tolkienmobs.entity.ai.goal.dragon;

import com.greatorator.tolkienmobs.entity.DragonEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MoveToHomeGoal extends Goal {
    private static final int RETURN_TIMEOUT = 600;
    private final DragonEntity dragon;
    private int time;
    private int repathTime;

    public MoveToHomeGoal(DragonEntity dragon) {
        this.dragon = dragon;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    private boolean canReturn() {
        return dragon.hasHome() && !dragon.canBeControlledByRider() && !dragon.isPassenger() && !dragon.isLeashed();
    }

    @Override
    public boolean canUse() { return canReturn() && !dragon.isWithinHome(); }

    @Override
    public boolean canContinueToUse() {
        return canReturn() && dragon.distanceToSqr(Vec3.atBottomCenterOf(dragon.getHomePosition())) > 4;
    }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    @Override
    public void start() {
        time = repathTime = 0;
        dragon.clearAI();
        dragon.setSleeping(false);
        if (dragon.dragonCanFly()) dragon.setNavigator(DragonEntity.NavigationType.FLYING);
    }

    @Override
    public void stop() {
        time = repathTime = 0;
        dragon.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (!canReturn()) return;
        Vec3 home = Vec3.atBottomCenterOf(dragon.getHomePosition());
        if (++time >= RETURN_TIMEOUT) {
            if (dragon.trySafeTeleport(dragon.getHomePosition())) time = 0;
            return;
        }
        if (--repathTime <= 0) {
            repathTime = 20;
            // Short waypoints keep distant homes within the navigator's search range.
            Vec3 offset = home.subtract(dragon.position());
            Vec3 waypoint = dragon.position().add(offset.normalize().scale(Math.min(16, offset.length())));
            dragon.getNavigation().moveTo(waypoint.x, waypoint.y, waypoint.z, 1.1);
        }
    }
}
