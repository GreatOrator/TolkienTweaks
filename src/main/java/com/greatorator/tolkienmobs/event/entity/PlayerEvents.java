package com.greatorator.tolkienmobs.event.entity;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import com.greatorator.tolkienmobs.init.TolkienPotions;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
public class PlayerEvents {
    @SubscribeEvent
    public static void onPlayerUpdate(PlayerTickEvent.Pre event){
        Player player = event.getEntity();

        if(!(player instanceof ServerPlayer) || !player.isAlive()) return;

        if(player.isInWater()) {

            if(player.level().getBiome(player.blockPosition()).is(TolkienBiomes.MIRKWOOD)|| player.level().getBiome(player.blockPosition()).is(TolkienBiomes.MIRKWOOD_SPOOKY)) {
                player.addEffect(new MobEffectInstance(TolkienMobEffects.SLEEPNESIA, 1200, 8));
            }
        }
    }
}
