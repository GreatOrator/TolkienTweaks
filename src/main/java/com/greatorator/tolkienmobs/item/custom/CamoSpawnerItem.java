package com.greatorator.tolkienmobs.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.component.TypedEntityData;

import java.util.function.Consumer;

/**
 * 26.1 moved a block's tooltip onto its item, so the camo spawner's description lives here. It is the
 * same line vanilla's spawner shows: the entity it spawns when the item carries spawn data, and the
 * two "place near a spawner" hints when it does not.
 */
public class CamoSpawnerItem extends BlockItem {
    public CamoSpawnerItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
        TypedEntityData<BlockEntityType<?>> spawnData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        Spawner.appendHoverText(spawnData, tooltipComponents, "SpawnData");
    }
}
