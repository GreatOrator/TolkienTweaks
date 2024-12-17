package com.greatorator.tolkienmobs.effect;

import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class SleepEffect extends TolkienEffect {
    static ResourceLocation sleepTight = ResourceLocation.fromNamespaceAndPath(MODID, "sleeping");
    static boolean shouldSleep = true;

    public SleepEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {
        AttributeInstance nightTime = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);

        if (livingEntity instanceof Player && livingEntity.hasEffect(TolkienMobEffects.SLEEPNESIA) && nightTime !=null) {
            nightTime.addPermanentModifier(new AttributeModifier(sleepTight, amplifier + 1, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        AttributeInstance nightTime = pLivingEntity.getAttribute(Attributes.MOVEMENT_SPEED);

        if (shouldSleep){
            ((Player) pLivingEntity).setForcedPose(Pose.SLEEPING);
            pLivingEntity.addEffect((new MobEffectInstance(MobEffects.BLINDNESS, 40, pAmplifier, true, false, false, null)));
            pLivingEntity.addEffect((new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, pAmplifier+100, true, false, false, null)));
        }else {
            ((Player) pLivingEntity).setForcedPose(null);
            nightTime.removeModifier(sleepTight);
        }
        return shouldSleep;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        if (duration <= 5){
            shouldSleep = false;
        }
        return true;
    }
}
