package com.greatorator.tolkienmobs.effect;

import com.greatorator.tolkienmobs.faction.Courage;
import com.greatorator.tolkienmobs.faction.CourageLedger;
import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import net.minecraft.client.player.ClientInput;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

/**
 * Crippling terror: what a Nazgul's scream does to a player who has not yet learned to stand up to it.
 * <p>
 * Its teeth are taken away one at a time by {@link Courage}, so this effect asks the player's rung what
 * is left of it on every tick rather than applying a fixed set of things. A faint-hearted player is
 * held to the spot, blinded and weakened; a trembling one keeps their legs at the price of a heavy
 * slowness; a steady one keeps their sight; a stout-hearted one keeps their strength; and from fearless
 * up the whole effect fails to land. Every one of those changes is made here, in one place, because
 * this is the place that knows both the player and the effect.
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class TerrorEffect extends TolkienEffect{
    int fearDuration = 10;

    public TerrorEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        Courage courage = Courage.of(CourageLedger.courage(entity));

        // Nothing left of it at all: the scream still sounds, and no longer finds anything to hold.
        if (courage.refusesTerror()) return true;

        // Standing in it is what teaches a player to stand it, so the deed is earned by carrying the
        // terror rather than by avoiding it - and only a player has a ladder to climb.
        if (entity instanceof ServerPlayer player) {
            CourageLedger.fearWithstood(player);
        }

        if (courage.blinded()) {
            entity.addEffect((new MobEffectInstance(MobEffects.BLINDNESS, 40, amplifier, true, false, false, null)));
        }
        if (courage.weakened()) {
            entity.addEffect((new MobEffectInstance(MobEffects.WEAKNESS, 40, amplifier, true, false, false, null)));
        }
        if (courage.pinned()) {
            entity.setDeltaMovement(Vec3.ZERO);
        } else if (courage.slowness() >= 0) {
            entity.addEffect((new MobEffectInstance(MobEffects.SLOWNESS, 40, courage.slowness(), true, false, false, null)));
        }

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
        if (!event.getEntity().hasEffect(TolkienMobEffects.PARALYSING_FEAR)) return;
        // The key states are one immutable record now, so they are cleared as a whole. 26.1 turns
        // them into the movement vector inside the input's own tick, before this event, and keeps
        // that vector private; the effect's own server-side tick is what pins the entity.
        // A player who has earned the first rung keeps their keys: the effect slows them on the
        // server instead of stealing the input on the client.
        if (!CourageLedger.clientStage().pinned()) return;
        ClientInput input = event.getInput();
        input.keyPresses = Input.EMPTY;
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % fearDuration == 0;
    }
}
