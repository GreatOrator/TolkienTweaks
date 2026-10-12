package com.greatorator.tolkienmobs.gametest;

import com.greatorator.tolkienmobs.enchantment.DwarvenMinerEnchantmentEffect;
import com.greatorator.tolkienmobs.init.TolkienEnchantments;
import com.mojang.authlib.GameProfile;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;
import java.util.function.Consumer;

@GameTestHolder("tolkienmobs_mining")
@PrefixGameTestTemplate(false)
public class DwarvenMinerGameTests {
    @GameTest(template = "mining_arena")
    public static void allLevelsAndFaces(GameTestHelper helper) {
        BlockPos source = helper.absolutePos(new BlockPos(12, 5, 12));
        var player = player(helper);
        helper.assertTrue(helper.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(TolkienEnchantments.DWARVEN_MINING_KEY).value().getMaxLevel() == 4, "Maximum enchantment level must be four");
        for (int level = 1; level <= 5; level++) for (Direction face : Direction.values()) {
            int radius = Math.min(level, 4); // Legacy level-five stacks still stop at 9×9.
            for (BlockPos pos : BlockPos.betweenClosed(source.offset(-5, -2, -5), source.offset(5, 8, 5))) {
                helper.getLevel().setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
            }
            var tool = equip(helper, player, new ItemStack(Items.DIAMOND_PICKAXE), level);
            aim(player, source, face);
            hit(helper, player, source);
            int removed = 0;
            for (BlockPos pos : BlockPos.betweenClosed(source.offset(-5, -2, -5), source.offset(5, 8, 5))) {
                int dx = pos.getX() - source.getX(), dy = pos.getY() - source.getY(), dz = pos.getZ() - source.getZ();
                boolean expected = switch (face.getAxis()) {
                    case Y -> dy == 0 && Math.abs(dx) <= radius && Math.abs(dz) <= radius;
                    case Z -> dz == 0 && Math.abs(dx) <= radius && dy >= -1 && dy <= 2 * radius - 1;
                    case X -> dx == 0 && Math.abs(dz) <= radius && dy >= -1 && dy <= 2 * radius - 1;
                };
                boolean air = helper.getLevel().getBlockState(pos).isAir();
                helper.assertTrue(air == expected, "Wrong mining mask at level " + level + ", face " + face + ", offset " + dx + "," + dy + "," + dz);
                if (air) removed++;
            }
            int area = (2 * radius + 1) * (2 * radius + 1);
            helper.assertTrue(removed == area && tool.getDamageValue() == area, "Size and durability must equal " + area);
            clearDrops(helper, source);
        }
        helper.succeed();
    }

    @GameTest(template = "mining_arena")
    public static void actualHitFaceAndWallHeight(GameTestHelper helper) {
        BlockPos source = helper.absolutePos(new BlockPos(12, 5, 12));
        helper.getLevel().setBlockAndUpdate(source, Blocks.STONE.defaultBlockState());
        var player = player(helper);
        for (Direction face : Direction.values()) {
            aim(player, source, face);
            helper.assertTrue(DwarvenMinerEnchantmentEffect.getMiningFace(player, source) == face, "Ray trace must choose " + face);
        }
        // Feet above the block must still select a wall when the ray hits its side.
        player.setPos(source.getX() + 0.5, source.getY() + 0.1, source.getZ() - 3.0);
        player.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(source));
        helper.assertTrue(DwarvenMinerEnchantmentEffect.getMiningFace(player, source) == Direction.NORTH,
                "Player feet height must not turn wall mining into floor mining");
        helper.succeed();
    }

    @GameTest(template = "mining_arena")
    public static void sneakAndBrokenTool(GameTestHelper helper) {
        BlockPos source = helper.absolutePos(new BlockPos(12, 5, 12));
        var player = player(helper);
        var tool = equip(helper, player, new ItemStack(Items.DIAMOND_PICKAXE), 2);
        helper.getLevel().setBlockAndUpdate(source, Blocks.STONE.defaultBlockState());
        aim(player, source, Direction.NORTH);
        player.setShiftKeyDown(true);
        hit(helper, player, source);
        helper.assertTrue(!helper.getLevel().isEmptyBlock(source) && tool.getDamageValue() == 0, "Sneak must disable area mining");
        player.setShiftKeyDown(false);
        var effect = new DwarvenMinerEnchantmentEffect(3);
        effect.apply(helper.getLevel(), 2, new net.minecraft.world.item.enchantment.EnchantedItemInUse(tool, EquipmentSlot.MAINHAND, player),
                helper.spawn(net.minecraft.world.entity.EntityType.PIG, new BlockPos(3, 3, 3)), Vec3.atCenterOf(source));
        helper.assertTrue(!helper.getLevel().isEmptyBlock(source), "Non-player hits must safely do nothing");
        for (BlockPos pos : DwarvenMinerEnchantmentEffect.getMiningArea(source, Direction.NORTH, 2)) {
            helper.getLevel().setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
        }
        tool.setDamageValue(tool.getMaxDamage() - 1);
        hit(helper, player, source);
        int remaining = 0;
        for (BlockPos pos : DwarvenMinerEnchantmentEffect.getMiningArea(source, Direction.NORTH, 2)) {
            if (!helper.getLevel().isEmptyBlock(pos)) remaining++;
        }
        helper.assertTrue(tool.isEmpty() && remaining == 24, "Stop as soon as the pickaxe breaks");
        helper.succeed();
    }

    @GameTest(template = "mining_arena")
    public static void dropsToolTierAndBreakCancellation(GameTestHelper helper) {
        BlockPos source = helper.absolutePos(new BlockPos(12, 5, 12));
        var player = player(helper);
        var tool = equip(helper, player, new ItemStack(Items.STONE_PICKAXE), 1);
        for (BlockPos pos : DwarvenMinerEnchantmentEffect.getMiningArea(source, Direction.NORTH, 1)) {
            helper.getLevel().setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
        }
        BlockPos protectedBlock = source.offset(1, 0, 0);
        BlockPos ore = source.offset(-1, 0, 0);
        BlockPos bedrock = source.above();
        helper.getLevel().setBlockAndUpdate(ore, Blocks.DIAMOND_ORE.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(bedrock, Blocks.BEDROCK.defaultBlockState());
        aim(player, source, Direction.NORTH);
        Consumer<BlockEvent.BreakEvent> cancel = event -> {
            if (event.getPlayer() == player && (event.getPos().equals(source) || event.getPos().equals(protectedBlock))) event.setCanceled(true);
        };
        NeoForge.EVENT_BUS.addListener(cancel);
        try {
            hit(helper, player, source);
            helper.assertTrue(tool.getDamageValue() == 0 && !helper.getLevel().isEmptyBlock(source.below()), "Canceled source must leave the entire area alone");
        } finally {
            NeoForge.EVENT_BUS.unregister(cancel);
        }
        Consumer<BlockEvent.BreakEvent> cancelNeighbor = event -> {
            if (event.getPlayer() == player && event.getPos().equals(protectedBlock)) event.setCanceled(true);
        };
        NeoForge.EVENT_BUS.addListener(cancelNeighbor);
        try {
            hit(helper, player, source);
        } finally {
            NeoForge.EVENT_BUS.unregister(cancelNeighbor);
        }
        helper.assertTrue(!helper.getLevel().isEmptyBlock(ore) && !helper.getLevel().isEmptyBlock(bedrock)
                && !helper.getLevel().isEmptyBlock(protectedBlock), "Respect tool tier, bedrock and neighbor break cancellation");
        int drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(source).inflate(10)).stream()
                .filter(item -> item.getItem().is(Items.COBBLESTONE)).mapToInt(item -> item.getItem().getCount()).sum();
        helper.assertTrue(drops == 6 && tool.getDamageValue() == 6, "Exactly six valid blocks should drop and consume durability");
        helper.succeed();
    }

    private static ServerPlayer player(GameTestHelper helper) {
        var player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "miner-test"));
        player.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
        return player;
    }

    private static ItemStack equip(GameTestHelper helper, ServerPlayer player, ItemStack tool, int level) {
        tool.enchant(helper.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(TolkienEnchantments.DWARVEN_MINING_KEY), level);
        player.setItemSlot(EquipmentSlot.MAINHAND, tool);
        return tool;
    }

    private static void aim(ServerPlayer player, BlockPos source, Direction face) {
        Vec3 eyes = Vec3.atCenterOf(source).add(Vec3.atLowerCornerOf(face.getNormal()).scale(4));
        player.setPos(eyes.x, eyes.y - player.getEyeHeight(), eyes.z);
        player.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(source));
    }

    private static void hit(GameTestHelper helper, ServerPlayer player, BlockPos source) {
        EnchantmentHelper.onHitBlock(helper.getLevel(), player.getMainHandItem(), player, player, EquipmentSlot.MAINHAND,
                Vec3.atCenterOf(source), helper.getLevel().getBlockState(source), item -> player.onEquippedItemBroken(item, EquipmentSlot.MAINHAND));
    }

    private static void clearDrops(GameTestHelper helper, BlockPos source) {
        helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(source).inflate(10)).forEach(item -> item.discard());
    }
}
