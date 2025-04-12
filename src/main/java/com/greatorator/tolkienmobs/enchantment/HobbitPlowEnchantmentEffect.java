package com.greatorator.tolkienmobs.enchantment;

import com.greatorator.tolkienmobs.init.TolkienTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public record HobbitPlowEnchantmentEffect(int level) implements EnchantmentEntityEffect {
    public static final MapCodec<HobbitPlowEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("level").forGetter(HobbitPlowEnchantmentEffect::level))
                    .apply(instance, HobbitPlowEnchantmentEffect::new));

    @Override
    public void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
        ItemStack holding = enchantedItemInUse.itemStack();
        Player pPlayer = (Player) entity;
        if (holding.isEmpty() || serverLevel.isClientSide()) return;

        BlockPos blockPos = BlockPos.containing(vec3);
        Block targetBlock = serverLevel.getBlockState(blockPos).getBlock();
        if (targetBlock == Blocks.GRASS_BLOCK || targetBlock == Blocks.DIRT || targetBlock == Blocks.PODZOL) {

            serverLevel.setBlockAndUpdate(blockPos, Blocks.WATER.defaultBlockState());
            if (!pPlayer.isCreative()) {
                pPlayer.getItemInHand(InteractionHand.MAIN_HAND).hurtAndBreak((enchantmentLevel * 2) + 1, pPlayer, EquipmentSlot.MAINHAND);
            }

            for (int x = -enchantmentLevel; x <= enchantmentLevel; x++) {
                for (int z = -enchantmentLevel; z <= enchantmentLevel; z++) {
                    BlockPos targetPos = new BlockPos(blockPos.getX() + x, blockPos.getY(), blockPos.getZ() + z);
                    if (serverLevel.isEmptyBlock(targetPos.above()) && serverLevel.getBlockState(targetPos).is(TolkienTags.Blocks.TILLABLES)) {
                        serverLevel.setBlockAndUpdate(targetPos, Blocks.FARMLAND.defaultBlockState());
                        serverLevel.playSound(pPlayer, blockPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
