package com.greatorator.tolkienmobs.effect;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class NimbleEffect extends TolkienEffect {
    static ResourceLocation nimbleStep = ResourceLocation.fromNamespaceAndPath(MODID, "nimbleness");
    public static float runDuration = 10;
    static boolean canStep = true;

    public NimbleEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {
        if (source instanceof Player && livingEntity.hasEffect(TolkienEffects.ELF_NIMBLENESS) && canStep) {
            canStepUp(livingEntity, amplifier);
        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player && entity.hasEffect(TolkienEffects.ELF_NIMBLENESS) && !canStep) {
            noStepUp(entity);
        }
        return canStep;
    }

    private static void canStepUp (LivingEntity livingEntity, int amplifier){
        AttributeInstance hasStep = livingEntity.getAttribute(Attributes.STEP_HEIGHT);

        if (hasStep != null && livingEntity.hasEffect(TolkienEffects.ELF_NIMBLENESS)) {
            canStep = true;
            hasStep.addPermanentModifier(new AttributeModifier(nimbleStep, amplifier + 1, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void noStepUp (LivingEntity livingEntity){
        AttributeInstance hasStep = livingEntity.getAttribute(Attributes.STEP_HEIGHT);

        if (hasStep != null) {
            canStep = false;
            hasStep.removeModifier(nimbleStep);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        TolkienMobsMain.LOGGER.warn(String.valueOf(canStep));
        TolkienMobsMain.LOGGER.warn(String.valueOf(duration));
        if (duration <= 5){
            canStep = false;
        }
        return canStep;
    }
}
