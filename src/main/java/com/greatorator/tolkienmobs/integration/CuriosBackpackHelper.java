package com.greatorator.tolkienmobs.integration;

import com.greatorator.tolkienmobs.item.custom.BackpackItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

/** Curios lookups for the backpack. Only call when the curios mod is loaded. */
public class CuriosBackpackHelper {
    public static Optional<ItemStack> findBackpack(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(inventory -> inventory.findFirstCurio(stack -> stack.getItem() instanceof BackpackItem))
                .map(SlotResult::stack);
    }

    public static boolean isWearing(LivingEntity entity, ItemStack backpack) {
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(inventory -> inventory.findFirstCurio(stack -> stack == backpack))
                .isPresent();
    }
}
