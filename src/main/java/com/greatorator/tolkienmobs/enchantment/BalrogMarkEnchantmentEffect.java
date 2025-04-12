package com.greatorator.tolkienmobs.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public record BalrogMarkEnchantmentEffect(int ground) implements EnchantmentEntityEffect {
    public static final MapCodec<BalrogMarkEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("ground").forGetter(BalrogMarkEnchantmentEffect::ground))
                    .apply(instance, BalrogMarkEnchantmentEffect::new));

    private static final Map<ServerLevel, Map<BlockPos, Long>> trackedBlocks = new HashMap<>();
    private static final Map<Entity, Boolean> playerOnGroundMap = new HashMap<>();
    private static final int DURATION_TICKS = 100;
    private static final int GROUND_DEPTH = 1;

    @Override
    public void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
        Vec3 playerPosition = entity.position();
        Vec3 movementVector = entity.getDeltaMovement().scale(2);

        BlockPos groundPos = new BlockPos(
                (int) Math.floor(playerPosition.x + movementVector.x),
                (int) Math.floor(playerPosition.y - GROUND_DEPTH),
                (int) Math.floor(playerPosition.z + movementVector.z)
        );

        Vec3 lookDirection = entity.getLookAngle().normalize();

        for (int xOffset = -2; xOffset <= 2; xOffset++) {
            for (int zOffset = -2; zOffset <= 2; zOffset++) {
                for (int yOffset = 0; yOffset <= 1; yOffset++) { // Ground level and one block up
                    BlockPos checkPos = entity.blockPosition().offset(xOffset, yOffset, zOffset);

                    Vec3 blockVec = new Vec3(checkPos.getX() - entity.getX(), 0, checkPos.getZ() - entity.getZ());
                    double dotProduct = blockVec.normalize().dot(lookDirection);

                    if (dotProduct > 0.5 && entity.onGround()) {
                        playMagmaEffects(serverLevel, checkPos);
                        replaceSurfaceWithMagmaBlock(serverLevel, checkPos, DURATION_TICKS);
                    }
                }
            }
        }
        boolean isOnGround = serverLevel.getBlockState(groundPos).is(BlockTags.MINEABLE_WITH_AXE) || serverLevel.getBlockState(groundPos).is(BlockTags.MINEABLE_WITH_PICKAXE);

        boolean wasOnGround = playerOnGroundMap.getOrDefault(entity, false);

        if (isOnGround && !wasOnGround) {
            playMagmaEffects(serverLevel, groundPos);
            replaceSurfaceWithMagmaBlock(serverLevel, groundPos, DURATION_TICKS);
        }

        playerOnGroundMap.put(entity, isOnGround);

        revertExpiredBlocks(serverLevel);
    }

    private void replaceSurfaceWithMagmaBlock(ServerLevel level, BlockPos center, int durationTicks) {
        int radius = ground(); // Radius for transformation
        long expirationTime = level.getGameTime() + durationTicks;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) continue;

                BlockPos pos = center.offset(x, 0, z);
                if (level.getBlockState(pos).is(BlockTags.MINEABLE_WITH_AXE) || level.getBlockState(pos).is(BlockTags.MINEABLE_WITH_PICKAXE)) { // Includes flowing lava
                    level.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
                    trackBlock(level, pos, expirationTime);
                }
            }
        }
    }

    private void trackBlock(ServerLevel level, BlockPos pos, long expirationTime) {
        trackedBlocks.computeIfAbsent(level, k -> new HashMap<>())
                .put(pos.immutable(), expirationTime);
    }

    private void revertExpiredBlocks(ServerLevel level) {
        Map<BlockPos, Long> levelBlocks = trackedBlocks.getOrDefault(level, new HashMap<>());
        long currentTime = level.getGameTime();

        levelBlocks.entrySet().removeIf(entry -> {
            if (currentTime >= entry.getValue()) {
                BlockPos pos = entry.getKey();
                if (level.getBlockState(pos).is(Blocks.MAGMA_BLOCK)) {
                    level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
                    playRevertEffects(level, pos);
                }
                return true;
            }
            return false;
        });
    }

    private void playMagmaEffects(ServerLevel level, BlockPos pos) {
        if (level.getBlockState(pos).is(BlockTags.MINEABLE_WITH_AXE) || level.getBlockState(pos).is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.7f, 0.8f);

            level.sendParticles(ParticleTypes.LAVA,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    20, 0.5, 0.5, 0.5, 0.2);
        }
    }

    private void playRevertEffects(ServerLevel level, BlockPos pos) {
        if (level.getBlockState(pos).is(Blocks.MAGMA_BLOCK)) {
            level.playSound(null, pos, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.5f, 0.9f);
            level.sendParticles(ParticleTypes.SMOKE,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    5, 0.2, 0.2, 0.2, 0.01);
        }
    }

    public static boolean isTemporaryMagmaBlock(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            Map<BlockPos, Long> levelBlocks = trackedBlocks.getOrDefault(serverLevel, new HashMap<>());
            return levelBlocks.containsKey(pos.immutable());
        }
        return false;
    }

    public static void removeAllTemporaryBlocks(ServerLevel level) {
        if (!trackedBlocks.containsKey(level)) return;

        Map<BlockPos, Long> levelBlocks = trackedBlocks.get(level);
        for (BlockPos pos : levelBlocks.keySet()) {
            if (level.getBlockState(pos).is(Blocks.MAGMA_BLOCK)) {
                level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState()); // Restore lava
            }
        }
        trackedBlocks.remove(level);
    }
    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
