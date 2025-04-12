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
    public void apply(ServerLevel level, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
        Player pPlayer = (Player) entity;

        pPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, range(), false, false, false));

        if (!level.getBlockState(BlockPos.containing(vec3)).is(BlockTags.MINEABLE_WITH_PICKAXE)) return;
        if (entity.isShiftKeyDown()) return;
        int max = (i * TolkienMobsConfig.BLOCKS_PER_LEVEL.get()) + 1;
        if (!(entity instanceof Player player)) return;
        chainMine(
                level,
                player,
                BlockPos.containing(vec3),
                max,
                enchantedItemInUse.itemStack(),
                enchantedItemInUse.onBreak()
        );
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }

    private static void chainMine(ServerLevel level, Player player, BlockPos sourceBlock, int max, ItemStack tool, Consumer<Item> onBreak) {
        BlockPos.breadthFirstTraversal(
                sourceBlock,
                Integer.MAX_VALUE,
                max,
                GeneralUtility::acceptDirections,
                blockPos -> {
                    BlockState blockState = level.getBlockState(blockPos);
                    if (blockState.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                        BlockEntity blockEntity = level.getBlockEntity(blockPos);
                        level.removeBlock(blockPos, false);
                        if (!player.isCreative()) {
                            blockState.getBlock().playerDestroy(level, player, blockPos, blockState, blockEntity, tool);
                        }
                        if (!sourceBlock.equals(blockPos)) {
                            tool.hurtAndBreak(1, level, player, onBreak);
                        }
                        return true;
                    }
                    return sourceBlock.equals(blockPos);
                }
        );
    }
}
