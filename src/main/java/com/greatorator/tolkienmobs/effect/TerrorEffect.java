package com.greatorator.tolkienmobs.effect;

import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import net.minecraft.client.player.ClientInput;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class TerrorEffect extends TolkienEffect{
    int fearDuration = 10;

    public TerrorEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        entity.addEffect((new MobEffectInstance(MobEffects.BLINDNESS, 40, amplifier, true, false, false, null)));
        entity.addEffect((new MobEffectInstance(MobEffects.WEAKNESS, 40, amplifier, true, false, false, null)));
        entity.setDeltaMovement(Vec3.ZERO);

        RandomSource random = entity.getRandom();

        serverLevel.sendParticles(
                ParticleTypes.DRIPPING_WATER,
                entity.position().x,
                entity.position().y + (random.nextFloat() * entity.getEyeHeight()),
                entity.position().z,
                3,
                entity.getBbWidth() * 0.5f,
                entity.getBbHeight() * 0.5f,
                entity.getBbWidth() * 0.5f,
                0.8d);
        return true;
    }

    @SubscribeEvent
    public static void onInputUpdate(MovementInputUpdateEvent event) {
        if (event.getEntity().hasEffect(TolkienMobEffects.PARALYSING_FEAR)) {
            ClientInput input = event.getInput();
            // The key states are one immutable record now, and the impulses are what actually moves the player.
            input.keyPresses = Input.EMPTY;
            input.forwardImpulse = 0;
            input.leftImpulse = 0;
        }
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % fearDuration == 0;
    }
}
