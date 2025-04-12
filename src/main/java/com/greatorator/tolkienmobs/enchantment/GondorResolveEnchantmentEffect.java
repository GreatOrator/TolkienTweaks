package com.greatorator.tolkienmobs.enchantment;

import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record GondorResolveEnchantmentEffect(int resistance) implements EnchantmentEntityEffect {
    public static final MapCodec<GondorResolveEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("resistance").forGetter(GondorResolveEnchantmentEffect::resistance))
                    .apply(instance, GondorResolveEnchantmentEffect::new));

    @Override
    public void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
        Player player = (Player) entity;

        player.addEffect(new MobEffectInstance(TolkienMobEffects.ENT_STANCE, 20, resistance(), false, false, false));
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
