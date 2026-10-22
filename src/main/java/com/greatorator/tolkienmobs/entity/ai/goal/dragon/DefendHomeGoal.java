package com.greatorator.tolkienmobs.entity.ai.goal.dragon;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.entity.DragonEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;

/**
 * Basically another target goal that targets things within the home
 */
public class DefendHomeGoal extends TargetGoal
{
    private static final TargetingConditions.Selector FILTER =
            (entity, level) -> entity instanceof Enemy && !(entity instanceof Creeper) && !entity.getName().getString().equalsIgnoreCase("Ignore Me");
    private static final TargetingConditions CONDITIONS = TargetingConditions.forCombat().selector(FILTER);
    private final DragonEntity defender;
    public DefendHomeGoal(DragonEntity defender)
    {
        super(defender, false, false);
        this.defender = defender;
        setFlags(EnumSet.of(Flag.TARGET));
    }


    @Override
    public boolean canUse()
    {
        if (defender.getHealth() <= defender.getMaxHealth() * 0.25) return false;
        if (!defender.hasRestriction()) return false;
        return defender.getRandom().nextDouble() < 0.2 && (targetMob = findPotentialTarget()) != null;
    }

    @Override
    public void start()
    {
        super.start();
        defender.setTarget(targetMob);

        // alert others!
        for (Mob mob : defender.level.getEntitiesOfClass(Mob.class, defender.getBoundingBox().inflate(TolkienMobsConfig.homeRadius), defender::isAlliedTo))
            mob.setTarget(targetMob);
    }

    @Override
    public boolean canContinueToUse()
    {
        return targetMob != null && defender.isWithinRestriction(targetMob.blockPosition()) && super.canContinueToUse();
    }

    @Override
    protected double getFollowDistance()
    {
        return defender.getRestrictRadius();
    }

    public LivingEntity findPotentialTarget()
    {
        // Level#getNearestEntity moved onto the server getter and now takes the candidate list instead
        // of a class and a box, so the scan is done here the way NearestAttackableTargetGoal does it.
        if (!(defender.level() instanceof ServerLevel serverLevel)) return null;
        return serverLevel.getNearestEntity(
                serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(defender.getRestrictCenter()).inflate(TolkienMobsConfig.homeRadius)),
                CONDITIONS,
                defender,
                defender.getX(), defender.getEyeY(), defender.getZ());
    }
}
