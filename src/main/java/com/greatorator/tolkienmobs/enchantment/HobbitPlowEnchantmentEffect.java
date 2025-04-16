package com.greatorator.tolkienmobs.enchantment;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienEnchantments;
import com.greatorator.tolkienmobs.init.TolkienTags;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
public record HobbitPlowEnchantmentEffect(int level) implements EnchantmentEntityEffect {
    public static final MapCodec<HobbitPlowEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("level").forGetter(HobbitPlowEnchantmentEffect::level))
                    .apply(instance, HobbitPlowEnchantmentEffect::new));

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlow(PlayerInteractEvent.RightClickBlock event) {
        ItemStack holding = event.getItemStack();
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        Level level = event.getLevel();

        if (!holding.is(ItemTags.HOES) || event.getLevel().isClientSide()) return;

        hobbitPlow(player, holding, pos, level);
    }

    private static void hobbitPlow(Player player, ItemStack holding, BlockPos pos, Level level) {
        int enchantmentLevel = 4;

        Block targetBlock = level.getBlockState(pos).getBlock();
        if (targetBlock == Blocks.GRASS_BLOCK || targetBlock == Blocks.DIRT || targetBlock == Blocks.PODZOL && holding.isEnchanted()) {
            level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());

            if (!player.isCreative()) {
                player.getItemInHand(InteractionHand.MAIN_HAND).hurtAndBreak((enchantmentLevel * 2) + 1, player, EquipmentSlot.MAINHAND);
            }

            for (int x = -enchantmentLevel; x <= enchantmentLevel; x++) {
                for (int z = -enchantmentLevel; z <= enchantmentLevel; z++) {
                    BlockPos targetPos = new BlockPos(pos.getX() + x, pos.getY(), pos.getZ() + z);
                    if (level.isEmptyBlock(targetPos.above()) && level.getBlockState(targetPos).is(TolkienTags.Blocks.TILLABLES)) {
                        level.setBlockAndUpdate(targetPos, Blocks.FARMLAND.defaultBlockState());
                        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
