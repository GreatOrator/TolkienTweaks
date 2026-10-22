package com.greatorator.tolkienmobs.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record DwarvenMinerEnchantmentEffect(int range) implements EnchantmentEntityEffect {
    public static final MapCodec<DwarvenMinerEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(it ->
            it.group(
                    Codec.INT.optionalFieldOf("range", 3).forGetter(DwarvenMinerEnchantmentEffect::range)
            ).apply(it, DwarvenMinerEnchantmentEffect::new)
    );

    @Override
    public void apply(ServerLevel level, int levelEnchant, EnchantedItemInUse item, Entity entity, Vec3 hitPosition) {
        if (!(entity instanceof ServerPlayer player)) return;
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, range(), false, false, false));
        if (player.isShiftKeyDown() || levelEnchant < 1) return;

        BlockPos source = BlockPos.containing(hitPosition);
        ItemStack tool = item.itemStack();
        if (tool != player.getMainHandItem() || !canMine(level, player, source, tool)) return;

        // Determine the face while the source block still exists; removing it changes the ray trace.
        Direction face = getMiningFace(player, source);
        if (!player.gameMode.destroyBlock(source)) return;
        for (BlockPos pos : getMiningArea(source, face, levelEnchant)) {
            if (pos.equals(source)) continue;
            if (tool.isEmpty() || tool != player.getMainHandItem()) break;
            if (canMine(level, player, pos, tool)) {
                // Preserve normal drops, XP, tool durability and cancellable block-break events.
                player.gameMode.destroyBlock(pos);
            }
        }
    }

    private static boolean canMine(ServerLevel level, ServerPlayer player, BlockPos pos, ItemStack tool) {
        BlockState state = level.getBlockState(pos);
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                && state.getDestroySpeed(level, pos) >= 0
                && (player.isCreative() || tool.isCorrectToolForDrops(state));
    }

    public static Direction getMiningFace(Player player, BlockPos source) {
        Vec3 start = player.getEyePosition();
        BlockHitResult hit = player.level().clip(new ClipContext(start,
                start.add(player.getLookAngle().scale(player.blockInteractionRange())),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (hit.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(source)) {
            return hit.getDirection();
        }
        // A server-side hit can arrive after the player's aim has moved slightly.
        Vec3 look = player.getLookAngle();
        return Direction.getNearest(Mth.floor(look.x), Mth.floor(look.y), Mth.floor(look.z), null).getOpposite();
    }

    /** A single plane: levels 1–4 produce 3×3, 5×5, 7×7 and 9×9. */
    public static Iterable<BlockPos> getMiningArea(BlockPos source, Direction face, int enchantmentLevel) {
        int radius = Mth.clamp(enchantmentLevel, 1, 4);
        if (face.getAxis() == Direction.Axis.Y) {
            // Floors and ceilings are centered on the block being hit.
            return BlockPos.betweenClosed(source.offset(-radius, 0, -radius), source.offset(radius, 0, radius));
        }
        // Walls extend one block below the hit, with the remaining rows above it.
        int top = 2 * radius - 1;
        if (face.getAxis() == Direction.Axis.Z) {
            return BlockPos.betweenClosed(source.offset(-radius, -1, 0), source.offset(radius, top, 0));
        }
        return BlockPos.betweenClosed(source.offset(0, -1, -radius), source.offset(0, top, radius));
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
