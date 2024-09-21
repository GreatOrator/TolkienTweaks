package com.greatorator.tolkienmobs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.SpecialPlantable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HarvestUtility {
    public static Iterable<BlockPos> getPositionsFromBox(AABB box) {
        return getPositionsFromBox(new BlockPos((int) box.minX, (int) box.minY, (int) box.minZ), new BlockPos((int) box.maxX, (int) box.maxY, (int) box.maxZ));
    }

    public static Iterable<BlockPos> getPositionsFromBox(BlockPos corner1, BlockPos corner2) {
        return () -> BlockPos.betweenClosedStream(corner1, corner2).iterator();
    }

    public static void growNearbyRandomly(boolean harvest, Level level, BlockPos pos, Player player) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        boolean grewWater = false;
        int chance = harvest ? 16 : 32;
        for (BlockPos currentPos : getPositionsFromBox(pos.offset(-4, -3, -4), pos.offset(4, 3, 4))) {
            currentPos = currentPos.immutable();
            BlockState state = serverLevel.getBlockState(currentPos);
            Block crop = state.getBlock();

            if (harvest) {
                harvestBlock(serverLevel, currentPos, state);
            } else if (crop instanceof CropBlock growable) {
                if (!growable.isValidBonemealTarget(serverLevel, currentPos, state)) {
                    if (!leaveBottomBlock(crop) || serverLevel.getBlockState(currentPos.below()).is(crop)) {
                        harvestBlock(serverLevel, currentPos, state);
                    }
                } else if (!isGrassLikeBlock(crop)) {
                    if (serverLevel.random.nextInt(chance) == 0) {
                        growable.performBonemeal(serverLevel, serverLevel.random, currentPos, state);
                        level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, currentPos, 0);
                    }
                }
            } else if (crop instanceof SpecialPlantable) {
                if (serverLevel.random.nextInt(chance / 4) == 0) {
                    for (int i = 0; i < (harvest ? 8 : 4); i++) {
                        state.randomTick(serverLevel, currentPos, serverLevel.random);
                    }
                }
                if (harvest) {
                    if (crop == Blocks.SUGAR_CANE || crop == Blocks.CACTUS) {
                        if (serverLevel.getBlockState(currentPos.above()).is(crop) && serverLevel.getBlockState(currentPos.above(2)).is(crop)) {
                            for (int i = crop == Blocks.SUGAR_CANE ? 1 : 0; i < 3; i++) {
                                harvestBlock(serverLevel, currentPos.above(i), state);
                            }
                        }
                    } else if (crop == Blocks.NETHER_WART) {
                        if (state.getValue(NetherWartBlock.AGE) == 3) {
                            harvestBlock(serverLevel, currentPos, state);
                        }
                    }
                }
            }

            else if (!grewWater && serverLevel.random.nextInt(512) == 0 && growWaterPlant(serverLevel, currentPos, state, null)) {
                level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, currentPos, 0);
                grewWater = true;
            }
        }
    }

    public static List<ItemStack> harvestBlock(ServerLevel pLevel, BlockPos cropPos, BlockState crop) {
        List<ItemStack> drops = new ArrayList<>();
        if (crop.getBlock() instanceof CropBlock cropBlock) {
            if (cropBlock.isMaxAge(crop)) {
                BlockState placeState = Blocks.AIR.defaultBlockState();
                BlockEntity blockEntity = pLevel.getBlockEntity(cropPos);
                drops.addAll(Block.getDrops(crop, pLevel, cropPos, blockEntity));
                for (ItemStack drop : drops) {
                    if (drop.getItem() instanceof BlockItem blockItem) {
                        placeState = blockItem.getBlock().defaultBlockState();
                        drop.shrink(1);
                        break;
                    }
                }
                pLevel.destroyBlock(cropPos, true);
                pLevel.setBlockAndUpdate(cropPos, placeState);
            }
        } else if (crop.is(Blocks.NETHER_WART) && crop.getValue(NetherWartBlock.AGE) == NetherWartBlock.MAX_AGE) {
            BlockState placeState = Blocks.AIR.defaultBlockState();
            BlockEntity blockEntity = pLevel.getBlockEntity(cropPos);
            drops.addAll(Block.getDrops(crop, pLevel, cropPos, blockEntity));
            for (ItemStack drop : drops) {
                if (drop.getItem() instanceof BlockItem blockItem) {
                    placeState = blockItem.getBlock().defaultBlockState();
                    drop.shrink(1);
                    break;
                }
            }
            pLevel.destroyBlock(cropPos, true);
            pLevel.setBlockAndUpdate(cropPos, placeState);
        } else if (crop.getBlock() instanceof SugarCaneBlock || crop.getBlock() instanceof CactusBlock || crop.is(Blocks.BAMBOO)) {
            List<BlockPos> posToCheck = new ArrayList<>();
            for (int i = 0; i < 10; i++) { //In case it grew a lot since last check
                BlockPos pos = cropPos.above(i);
                if (pLevel.getBlockState(pos).is(crop.getBlock()))
                    posToCheck.add(pos);
                else
                    break;
            }
            for (int i = posToCheck.size() - 1; i >= 0; i--) {
                BlockPos clearPos = posToCheck.get(i);
                BlockEntity blockEntity = pLevel.getBlockEntity(clearPos);
                drops.addAll(Block.getDrops(pLevel.getBlockState(clearPos), pLevel, clearPos, blockEntity));
                pLevel.destroyBlock(clearPos, true);
            }
        }

        return drops;
    }

    private static boolean leaveBottomBlock(Block crop) {
        return crop == Blocks.KELP_PLANT || crop == Blocks.BAMBOO;
    }

    private static boolean isGrassLikeBlock(Block crop) {
        return crop instanceof GrassBlock || crop instanceof NyliumBlock || crop instanceof NetherrackBlock || crop instanceof MossBlock;
    }

    public static boolean growWaterPlant(ServerLevel level, BlockPos pos, BlockState state, @Nullable Direction side) {
        boolean success = false;
        if (state.is(Blocks.WATER) && state.getFluidState().getAmount() == 8) {
            Random random = (Random) level.getRandom();
            label76:
            for (int i = 0; i < 128; ++i) {
                BlockPos blockpos = pos;
                for (int j = 0; j < i / 16; ++j) {
                    blockpos = blockpos.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2,
                            random.nextInt(3) - 1);
                    if (level.getBlockState(blockpos).isCollisionShapeFullBlock(level, blockpos)) {
                        continue label76;
                    }
                }
                BlockState newState = Blocks.SEAGRASS.defaultBlockState();
                Holder<Biome> biome = level.getBiome(blockpos);
//                if (biome.is(Biomes.WARM_OCEAN)) {
//                    if (i == 0 && side != null && side.getAxis().isHorizontal()) {
//                        newState = getRandomState(BlockTags.WALL_CORALS, random, newState);
//                        if (newState.hasProperty(BaseCoralWallFanBlock.FACING)) {
//                            newState = newState.setValue(BaseCoralWallFanBlock.FACING, side);
//                        }
//                    } else if (random.nextInt(4) == 0) {
//                        newState = getRandomState(BlockTags.UNDERWATER_BONEMEALS, random, newState);
//                    }
//                }
                if (newState.is(BlockTags.WALL_CORALS, s -> s.hasProperty(BaseCoralWallFanBlock.FACING))) {
                    for (int k = 0; !newState.canSurvive(level, blockpos) && k < 4; ++k) {
                        newState = newState.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection((RandomSource) random));
                    }
                }
                if (newState.canSurvive(level, blockpos)) {
                    BlockState stateToReplace = level.getBlockState(blockpos);
                    if (stateToReplace.is(Blocks.WATER) && stateToReplace.getFluidState().getAmount() == 8) {
                        level.setBlockAndUpdate(blockpos, newState);
                        success = true;
                    } else if (stateToReplace.is(Blocks.SEAGRASS) && random.nextInt(10) == 0) {
                        ((BonemealableBlock) Blocks.SEAGRASS).performBonemeal(level, (RandomSource) random, blockpos, stateToReplace);
                        success = true;
                    }
                }
            }
        }
        return success;
    }
}