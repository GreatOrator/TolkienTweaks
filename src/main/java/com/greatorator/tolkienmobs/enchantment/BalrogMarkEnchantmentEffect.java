package com.greatorator.tolkienmobs.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Optionull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.phys.Vec3;

public record BalrogMarkEnchantmentEffect(int level) implements EnchantmentEntityEffect {
    public static final MapCodec<BalrogMarkEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("level").forGetter(BalrogMarkEnchantmentEffect::level))
                    .apply(instance, BalrogMarkEnchantmentEffect::new));

    @Override
    public void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
        if (entity instanceof LivingEntity target) {
            SculkSpreader spreader = SculkSpreader.createLevelSpreader();
            Entity attacker = Optionull.map(target.getLastDamageSource(), DamageSource::getEntity);

            if (!(attacker instanceof ServerPlayer serverPlayer)) return;

            BlockPos pos = new BlockPos((int) vec3.x, (int) vec3.y, (int) vec3.z);
            for (int i = 0; i < 3 * enchantmentLevel; i++) {
                spreader.addCursors(pos, target.getExperienceReward(serverLevel, attacker));
            }
            for (int i = 0; i < 8 * enchantmentLevel; i++) {
                spreader.updateCursors(serverLevel, pos, serverLevel.getRandom(), true);
            }
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
