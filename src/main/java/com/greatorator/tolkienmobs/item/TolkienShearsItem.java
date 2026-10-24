package com.greatorator.tolkienmobs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.IShearable;

import java.util.Random;
import java.util.function.Consumer;

public class TolkienShearsItem extends ShearsItem {
    public boolean hasEffectOverride = false;
    private boolean hasLore = false;

    /**
     * Shears take no material, so their repair tag and tool rules are attached here rather than
     * in the item's static properties. Both need a registration lookup on the item and block
     * registries, and that is only writable while item registration runs; building them in a
     * static initializer would fail in datagen, where the built-in registries are frozen before
     * mods are constructed.
     */
    public TolkienShearsItem(Properties properties, TagKey<Item> repairTag) {
        super(properties.repairable(repairTag).component(DataComponents.TOOL, createToolProperties()));
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (level.isClientSide())
            return false;

        var block = state.getBlock();

        if (block instanceof IShearable) {
            var tile = level.getBlockEntity(pos);
            var params = new LootParams.Builder((ServerLevel) level)
                    .withParameter(LootContextParams.ORIGIN, new Vec3(pos.getX(), pos.getY(), pos.getZ()))
                    .withParameter(LootContextParams.TOOL, new ItemStack(Items.SHEARS))
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, tile);

            var drops = state.getDrops(params);
            var rand = new Random();

            for (var drop : drops) {
                float f = 0.7F;
                double d = rand.nextFloat() * f + (1D - f) * 0.5;
                double d1 = rand.nextFloat() * f + (1D - f) * 0.5;
                double d2 = rand.nextFloat() * f + (1D - f) * 0.5;

                var item = new ItemEntity(level, pos.getX() + d, pos.getY() + d1, pos.getZ() + d2, drop);

                item.setPickUpDelay(10);

                level.addFreshEntity(item);
            }

            stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);

            if (entity instanceof Player player) {
                player.awardStat(Stats.BLOCK_MINED.get(block), 1);
            }

            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);

            return true;
        }

        return false;
    }

    public TolkienShearsItem setEffectOverride() {
        this.hasEffectOverride = true;
        return this;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasEffectOverride || super.isFoil(stack);
    }

    public TolkienShearsItem setHasLore() {
        this.hasLore = true;
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (hasLore) {
            tooltipComponents.accept(Component.translatable(getDescriptionId() + ".lore").withStyle(ChatFormatting.GOLD));
            super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
        }
    }
}
