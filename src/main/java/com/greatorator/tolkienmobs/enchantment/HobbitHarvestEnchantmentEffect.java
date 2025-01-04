package com.greatorator.tolkienmobs.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public record HobbitHarvestEnchantmentEffect(int level) implements EnchantmentEntityEffect {
    private static final List<BlockPos> ITERATING_OFFSET = new ArrayList<>();
    static {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                ITERATING_OFFSET.add(new BlockPos(dx, 0, dz));
            }
        }
    }
    public static final MapCodec<HobbitHarvestEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.optionalFieldOf("level", 3).forGetter(HobbitHarvestEnchantmentEffect::level))
                    .apply(instance, HobbitHarvestEnchantmentEffect::new));

    @Override
    public void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
        ItemStack tool = enchantedItemInUse.itemStack();
        BlockPos pos = BlockPos.containing(vec3);
        if (!(entity instanceof Player player)) return;
        BlockState targetBlockState = serverLevel.getBlockState(pos);
        boolean hasValidCropBlock = false;
        if (targetBlockState.getBlock() instanceof CropBlock cropBlock) {
            if (cropBlock.isMaxAge(targetBlockState)) {
                hasValidCropBlock = true;
                BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
                cropBlock.playerWillDestroy(serverLevel, pos, targetBlockState, player);
                cropBlock.playerDestroy(serverLevel, player, pos, targetBlockState, blockEntity, tool);
                serverLevel.setBlockAndUpdate(pos, cropBlock.getStateForAge(0));
                tool.hurtAndBreak(1, serverLevel, player, enchantedItemInUse.onBreak());
            }
        } else return;
        int max = enchantmentLevel * 2 + 1;
        max *= max;
        if (hasValidCropBlock) max -= 1;
        Deque<BlockPos> iteratingPos = new ArrayDeque<>();
        iteratingPos.push(pos);
        List<BlockPos> vis = new ArrayList<>();
        while (!iteratingPos.isEmpty() && max >= 0) {
            BlockPos currentIterating = iteratingPos.pop();
            if (vis.contains(currentIterating)) continue;
            vis.add(currentIterating);
            max--;
            for (BlockPos offset : ITERATING_OFFSET) {
                BlockPos currentPos = currentIterating.offset(offset);
                iteratingPos.add(currentPos);
            }
            BlockState state = serverLevel.getBlockState(currentIterating);
            if (!(state.getBlock() instanceof CropBlock block)) continue;
            if (!block.isMaxAge(state)) continue;
            BlockState newState = block.getStateForAge(0);
            cropBlock.playerWillDestroy(serverLevel, currentIterating, state, player);
            cropBlock.playerDestroy(serverLevel, player, currentIterating, state, serverLevel.getBlockEntity(currentIterating), tool);
            serverLevel.setBlockAndUpdate(currentIterating, newState);
            tool.hurtAndBreak(1, serverLevel, player, enchantedItemInUse.onBreak());
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
