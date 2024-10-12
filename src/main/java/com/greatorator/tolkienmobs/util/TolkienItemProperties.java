package com.greatorator.tolkienmobs.util;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.containers.CoinPouchContainer;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.item.custom.CoinPouchItem;
import com.greatorator.tolkienmobs.item.custom.KeyRingItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class TolkienItemProperties {
    public static void addCustomItemProperties() {
        makeCustomBow(TolkienItems.ELVEN_BOW.get());
        makeCustomBow(TolkienItems.URUK_BOW.get());
        makeCustomPouch(TolkienItems.COIN_POUCH.get());
        makeCustomRing(TolkienItems.KEY_RING.get());
    }

    private static void makeCustomBow(Item item) {
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pull"), (stack, level, entity, duration) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getUseItem() != stack ? 0.0F : (float)(stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
            }
        });
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pulling"), (stack, level, entity, duration) ->
                entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F
        );
    }

    private static void makeCustomPouch(Item item) {
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("fullness"), (stack, level, entity, fullness) ->
            CoinPouchItem.getCoinCount(stack)
        );
    }

    private static void makeCustomRing(Item item) {
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("fullness"), (stack, level, entity, fullness) ->
                KeyRingItem.getKeyCount(stack)
        );
    }
}