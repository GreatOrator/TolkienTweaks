package com.greatorator.tolkienmobs.item.custom;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.item.TolkienItem;
import com.greatorator.tolkienmobs.util.HarvestUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HobbitRingItem extends TolkienItem {
    private int timer = 60;

    public HobbitRingItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            setActive(itemstack, !getActive(itemstack));
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (!getActive(stack)) return;
        if(!(entity instanceof Player)) return;

        pickupDrops((LivingEntity) entity);

        if(timer <= 0) {
            timer = TolkienMobsConfig.growthTimer;
            BlockPos entityPos = new BlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
            int range = TolkienMobsConfig.growthRange;
            int limit = 0;

            List<BlockPos> blocks = new ArrayList<>();

            for(BlockPos pos : BlockPos.betweenClosed(entityPos.getX() - range, entityPos.getY() - range, entityPos.getZ() - range, entityPos.getX() + range, entityPos.getY() + range, entityPos.getZ() + range)) {
                Block block = entity.level().getBlockState(pos).getBlock();

                if(block instanceof CropBlock || block instanceof StemBlock || block instanceof KelpBlock || block instanceof SeagrassBlock || block instanceof FlowerBlock) {
                    blocks.add(new BlockPos(pos));
                }
            }

            if(blocks.size() >= 1) {
                Random random = new Random();

                while(blocks.size() >= 1 && limit < 3) {
                    BlockPos pos = blocks.remove(random.nextInt(blocks.size()));
                    BlockState state = entity.level().getBlockState(pos);

                    HarvestUtility.growNearbyRandomly(false, entity.level(), pos, (Player) entity);
                }
            }
        } else {
            timer--;
        }
    }

    private static void pickupDrops(LivingEntity livingEntity) {
        final int RANGE = TolkienMobsConfig.pickupRange;
        BlockPos pos = new BlockPos(livingEntity.getBlockX(), livingEntity.getBlockY(), livingEntity.getBlockZ());
        List<ItemEntity> entities = livingEntity.level().getEntitiesOfClass(ItemEntity.class, new AABB(pos.getX() + RANGE, pos.getY() + RANGE, pos.getZ() + RANGE, pos.getX() - RANGE, pos.getY() - RANGE, pos.getZ() - RANGE));
        for(ItemEntity item : entities) {
            if(item.isAlive() && !item.hasPickUpDelay()) {
                item.playerTouch((Player)livingEntity);
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return getActive(itemStack);
    }

    public static boolean getActive(ItemStack stack) {
        return stack.getOrDefault(TolkienDataComponents.HOBBIT_RING_ACTIVE, false);
    }

    public static boolean setActive(ItemStack stack, boolean active) {
        if (!active)
            stack.remove(TolkienDataComponents.HOBBIT_RING_ACTIVE);
        else
            stack.set(TolkienDataComponents.HOBBIT_RING_ACTIVE, active);
        return active;
    }
}
