package com.greatorator.tolkienmobs.effect;

import com.greatorator.tolkienmobs.init.TolkienEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

import java.util.Random;

public class DrownEffect extends TolkienEffect {
    private final Random random = new Random();

    public DrownEffect(MobEffectCategory category, int color) {
        super(category, color);

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onHeal(LivingHealEvent event) {
        var effect = event.getEntity().getEffect(TolkienEffects.ELEMENTAL_DROWNING);
        if (effect == null)
            return;
        var chance = (effect.getAmplifier() + 1) / 15F;
        if (this.random.nextFloat() <= chance) {
            event.setAmount(event.getAmount() / 4F);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        var mod = 20 >> amplifier;
        return mod > 0 && duration % mod == 0 && this.random.nextBoolean();
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(entity.damageSources().drown(), 1F);
        return true;
    }
}
