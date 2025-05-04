package com.greatorator.tolkienmobs.event;

import com.greatorator.tolkienmobs.network.component.TrinketComponent;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;

import java.util.List;
import java.util.stream.Collectors;

public class TradesHandler {

    public static class setTrinketForSaleWithPotion {
        private final ItemStack toItem;
        private final Item fromItem;
        private final RandomSource random = RandomSource.create();

        public setTrinketForSaleWithPotion(Item fromItem) {
            this.toItem = new ItemStack(fromItem);
            this.fromItem = fromItem;
        }

        public ItemStack getOffer() {
            List<Holder<Potion>> list = (List) BuiltInRegistries.POTION.holders().filter((p_340770_) -> {
                return !((Potion) p_340770_.value()).getEffects().isEmpty();
            }).collect(Collectors.toList());
            Holder<Potion> holder = (Holder) Util.getRandom(list, random);
            ItemStack itemstack = new ItemStack(this.toItem.getItem());
            itemstack.set(TolkienDataComponents.POTION_CONTENTS, new TrinketComponent(holder));
            return itemstack;
        }
    }
}
