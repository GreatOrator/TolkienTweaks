package com.greatorator.tolkienmobs.util.block;

import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.greatorator.tolkienmobs.integration.CuriosBackpackHelper;
import com.greatorator.tolkienmobs.item.custom.BackpackItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * The backpack's magnet: while a backpack carries a magnet upgrade and its magnet is switched on, the
 * drops around the owner are gathered into the backpack first and into the player's inventory second.
 * The range is the best installed tier - 16, 32 or 64 blocks - and it is a plain radius, so stone and
 * walls do not stop it.
 */
public final class BackpackMagnet {
    /** The furthest a tier reaches, and so the box every scan has to look through. */
    private static final int MAX_RANGE = 64;
    /** How often the scan runs, in ticks: four times a second reads as a pull without a scan a tick. */
    private static final int SCAN_INTERVAL = 5;

    private BackpackMagnet() {
    }

    /**
     * One scan for one player. Called from the server's player tick; does nothing on the ticks between
     * scans, and builds the backpack from its stack only once there is something to gather.
     */
    public static void tick(ServerPlayer player) {
        if (!player.isAlive() || player.isSpectator() || player.tickCount % SCAN_INTERVAL != 0) {
            return;
        }

        AABB box = player.getBoundingBox().inflate(MAX_RANGE);
        List<ItemEntity> drops = player.level().getEntitiesOfClass(ItemEntity.class, box);
        if (drops.isEmpty()) {
            return;
        }

        // A backpack whose screen is open is written by that screen's own block entity, so it has to be
        // the one filled here as well: a second copy of the stack written back would undo the other.
        boolean persistAfterwards = false;
        BackpackBlockEntity backpack;
        if (player.containerMenu instanceof BackpackBlockContainer open) {
            backpack = open.tileEntity;
        } else {
            backpack = findMagnetBackpack(player);
            if (backpack != null) {
                persistAfterwards = true;
            } else {
                // Nothing worn or carried: a backpack set down beside the player still gathers, which is
                // otherwise the one case where the magnet looked broken.
                backpack = findPlacedBackpack(player);
                if (backpack == null) {
                    return;
                }
            }
        }

        if (!backpack.getBackpackSettings().magnet) {
            return;
        }
        int range = backpack.getBackpackUpgrades().magnetRange();
        if (range <= 0) {
            return;
        }

        double rangeSquared = (double) range * range;
        boolean gathered = false;
        for (ItemEntity drop : drops) {
            // A drop still counting down is left where it is, so thrown and dropped items stay thrown.
            if (drop.hasPickUpDelay() || drop.distanceToSqr(player) > rangeSquared) {
                continue;
            }
            gathered |= absorb(player, backpack, drop);
        }
        // The open backpack's own block entity saves itself as its slots change.
        if (gathered && persistAfterwards) {
            backpack.saveToStack();
        }
    }

    /**
     * The nearest placed backpack whose own reach covers the player, or null. Only loaded chunks are
     * looked through, so this never pulls a chunk in, and only the block entity map of each chunk is
     * touched rather than its blocks.
     */
    @Nullable
    private static BackpackBlockEntity findPlacedBackpack(ServerPlayer player) {
        ServerLevel level = player.level();
        BlockPos origin = player.blockPosition();
        int chunkRadius = MAX_RANGE >> 4;
        ChunkPos center = new ChunkPos(origin.getX() >> 4, origin.getZ() >> 4);
        BackpackBlockEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(center.x() + dx, center.z() + dz);
                if (chunk == null) {
                    continue;
                }
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof BackpackBlockEntity candidate)) {
                        continue;
                    }
                    int reach = candidate.getBackpackUpgrades().magnetRange();
                    double distance = candidate.getBlockPos().distSqr(origin);
                    if (reach > 0 && distance <= (double) reach * reach && distance < nearestDistance) {
                        nearest = candidate;
                        nearestDistance = distance;
                    }
                }
            }
        }
        return nearest;
    }

    /**
     * The bag with a magnet fitted: the first one the player wears or carries. Looking past a bag without
     * one matters, because a second bag left in the Curios slot would otherwise be chosen over the one in
     * hand and the scan would stop at its zero reach. The switch itself is checked by the caller, so the
     * same bag is the one the toggle button and the key work on.
     */
    @Nullable
    public static BackpackBlockEntity findMagnetBackpack(ServerPlayer player) {
        for (ItemStack candidate : findBackpacks(player)) {
            if (candidate.isEmpty()) {
                continue;
            }
            BackpackBlockEntity bag = BackpackBlockEntity.createHeld(player, candidate, null);
            if (bag.getBackpackUpgrades().magnetRange() > 0) {
                return bag;
            }
        }
        return null;
    }

    /**
     * Every backpack the player wears or carries: the Curios slot first, then the bag in either hand, then
     * the rest of the inventory. The hands are read directly as well as through the inventory, because a bag
     * in the offhand is the one case a plain inventory walk can miss.
     */
    private static List<ItemStack> findBackpacks(Player player) {
        List<ItemStack> bags = new ArrayList<>(4);
        if (ModList.get().isLoaded("curios")) {
            CuriosBackpackHelper.findBackpack(player).ifPresent(bag -> addBag(bags, bag));
        }
        addBag(bags, player.getMainHandItem());
        addBag(bags, player.getOffhandItem());
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            addBag(bags, player.getInventory().getItem(i));
        }
        return bags;
    }

    private static void addBag(List<ItemStack> bags, ItemStack stack) {
        if (stack.getItem() instanceof BackpackItem && bags.stream().noneMatch(bag -> bag == stack)) {
            bags.add(stack);
        }
    }

    /** Takes one drop: the backpack's storage first, then the player's inventory, then it stays on the ground. */
    private static boolean absorb(ServerPlayer player, BackpackBlockEntity backpack, ItemEntity drop) {
        ItemStack stack = drop.getItem();
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        int before = stack.getCount();

        // A backpack's storage is the first N slots of a fixed-size handler - the rest are there so the
        // menu can grow and shrink in place - and an item put in them would have no slot to show it in.
        ItemStack leftOver = ItemUtil.insertItemReturnRemaining(
                RangedResourceHandler.of(backpack.itemHandler, 0, backpack.getStorageSize()), stack, false, null);
        if (!leftOver.isEmpty()) {
            // Inventory#add takes what it can and leaves the rest in the stack it was handed.
            player.getInventory().add(leftOver);
        }

        int taken = before - leftOver.getCount();
        if (taken <= 0) {
            return false;
        }

        drop.setItem(leftOver);
        player.take(drop, taken);
        player.awardStat(Stats.ITEM_PICKED_UP.get(item), taken);
        player.onItemPickup(drop);
        if (leftOver.isEmpty()) {
            drop.discard();
        }
        return true;
    }
}
