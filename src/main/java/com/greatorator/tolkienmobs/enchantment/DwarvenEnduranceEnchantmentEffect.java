package com.greatorator.tolkienmobs.enchantment;

import com.greatorator.tolkienmobs.init.TolkienTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

public record DwarvenEnduranceEnchantmentEffect(int hungerRange) implements EnchantmentEntityEffect {
    public static final MapCodec<DwarvenEnduranceEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("hunger_range").forGetter(DwarvenEnduranceEnchantmentEffect::hungerRange))
                    .apply(instance, DwarvenEnduranceEnchantmentEffect::new));

    @Override
    public void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
        if (entity instanceof Player player && player.canEat(false) && (player.getFoodData().getFoodLevel() < hungerRange())) {
            ItemStack food = ItemStack.EMPTY;
            if (player.getOffhandItem().is(Tags.Items.FOODS)) {
                if (needsFood(player, player.getOffhandItem().get(DataComponents.FOOD)) && isFoodAllowed(player, player.getOffhandItem())) {
                    food = player.getOffhandItem();
                }
            } else {
                food = getMostNeededFood(player);
            }
            if (!food.isEmpty()) {
                ItemStack finished = food.finishUsingItem(player.level(), player);
                if (!ItemStack.matches(food, finished) && !player.addItem(finished)) {
                    finished.shrink(1);
                }
            }
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }

    private static ItemStack getMostNeededFood(Player player) {
        ItemStack food = ItemStack.EMPTY;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Tags.Items.FOODS)) {
                FoodProperties component = stack.get(DataComponents.FOOD);
                if (needsFood(player, component)) {
                    if (food.isEmpty() || food.get(DataComponents.FOOD).nutrition() < component.nutrition()) {
                        if (isFoodAllowed(player, stack)) {
                            food = stack;
                        }
                    }
                }
            }
        }
        return food;
    }

    private static boolean needsFood(Player player, FoodProperties component) {
        return component != null && (player.getFoodData().getFoodLevel() < 6 || player.getFoodData().getFoodLevel() <= 20);
    }

    private static boolean isFoodAllowed(Player player, ItemStack stack) {
        return !stack.is(TolkienTags.Items.CANNOT_AUTO_CONSUME);
    }
}
