package com.greatorator.tolkienmobs.handler.interfaces;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nullable;
import java.util.List;

public interface TolkienSpawner {
    void setEntityId(EntityType<?> var1, RandomSource var2);

    static void appendHoverText(ItemStack stack, List<Component> tooltipLines, String spawnDataKey) {
        Component component = getSpawnEntityDisplayName(stack, spawnDataKey);
        if (component != null) {
            tooltipLines.add(component);
        } else {
            tooltipLines.add(CommonComponents.EMPTY);
            tooltipLines.add(Component.translatable("block.minecraft.spawner.desc1").withStyle(ChatFormatting.GRAY));
            tooltipLines.add(CommonComponents.space().append(Component.translatable("block.minecraft.spawner.desc2").withStyle(ChatFormatting.BLUE)));
        }

    }

    @Nullable
    static Component getSpawnEntityDisplayName(ItemStack stack, String spawnDataKey) {
        CompoundTag compoundtag = ((CustomData)stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY)).getUnsafe();
        ResourceLocation resourcelocation = getEntityKey(compoundtag, spawnDataKey);
        return resourcelocation != null ? (Component) BuiltInRegistries.ENTITY_TYPE.getOptional(resourcelocation).map((p_312609_) -> {
            return Component.translatable(p_312609_.getDescriptionId()).withStyle(ChatFormatting.GRAY);
        }).orElse((MutableComponent) null) : null;
    }

    @Nullable
    private static ResourceLocation getEntityKey(CompoundTag tag, String spawnDataKey) {
        if (tag.contains(spawnDataKey, 10)) {
            String s = tag.getCompound(spawnDataKey).getCompound("entity").getString("id");
            return ResourceLocation.tryParse(s);
        } else {
            return null;
        }
    }

    void serverTick(ServerLevel level, BlockPos pos);
}
