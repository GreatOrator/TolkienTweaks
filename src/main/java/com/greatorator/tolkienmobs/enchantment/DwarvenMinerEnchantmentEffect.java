package com.greatorator.tolkienmobs.enchantment;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record DwarvenMinerEnchantmentEffect(int range) implements EnchantmentEntityEffect {
    public static final MapCodec<DwarvenMinerEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(it ->
            it.group(
                    Codec.INT.optionalFieldOf("range", 3).forGetter(DwarvenMinerEnchantmentEffect::range)
            ).apply(it, DwarvenMinerEnchantmentEffect::new)
    );

    @Override
    public void apply(ServerLevel level, int levelEnchant, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
        Player pPlayer = (Player) entity;

        pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, range(), false, false, false));

        if (!level.getBlockState(BlockPos.containing(vec3)).is(BlockTags.MINEABLE_WITH_PICKAXE)) return;
        if (entity.isShiftKeyDown()) return;
        if (!(entity instanceof Player player)) return;

        // Calculate radius based on enchantment level (1=3x3, 2=5x5, 3=7x7, 4=9x9)
        int radius = Math.min(levelEnchant, 4);
        
        // Calculate center block based on player position
        BlockPos sourceBlock = BlockPos.containing(vec3);
        BlockPos centerBlock = calculateCenterBlock(player, sourceBlock, radius);

        chainMine(
                level,
                player,
                centerBlock,
                radius,
                enchantedItemInUse.itemStack(),
                enchantedItemInUse.onBreak()
        );
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }

    private static BlockPos calculateCenterBlock(Player player, BlockPos sourceBlock, int radius) {
        // If player is above the mining point (player Y > source Y), center is directly from mining point
        if (player.getY() > sourceBlock.getY()) {
            return sourceBlock;
        }
        
        // For all levels, center is 1 block up from the mining point
        return sourceBlock.above();
    }

    private static void chainMine(ServerLevel level, Player player, BlockPos centerBlock, int radius, ItemStack tool, Consumer<Item> onBreak) {
        // Calculate the area bounds based on radius (radius 1 = 3x3, radius 2 = 5x5, radius 3 = 7x7)
        int halfSize = radius;
        BlockPos minPos = new BlockPos(centerBlock.getX() - halfSize, centerBlock.getY(), centerBlock.getZ() - halfSize);
        BlockPos maxPos = new BlockPos(centerBlock.getX() + halfSize, centerBlock.getY(), centerBlock.getZ() + halfSize);

        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            BlockState blockState = level.getBlockState(pos);
            if (blockState.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                level.removeBlock(pos, false);
                if (!player.isCreative()) {
                    blockState.getBlock().playerDestroy(level, player, pos, blockState, blockEntity, tool);
                }
                if (!centerBlock.equals(pos)) {
                    tool.hurtAndBreak(1, level, player, onBreak);
                }
            }
        }
    }
}
