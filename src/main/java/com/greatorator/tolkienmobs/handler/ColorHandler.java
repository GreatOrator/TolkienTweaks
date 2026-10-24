package com.greatorator.tolkienmobs.handler;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import javax.annotation.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * Tints the gem layer of a trinket with the colour of the potion it holds. The colour moved out of
 * the item colour handler into a tint source, which an item model names in its {@code tints} list:
 * the trinkets list a plain white first layer and this source for the gem.
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ColorHandler {

    @SubscribeEvent
    public static void itemColourEvent(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(TolkienMobsMain.resLoc("trinket"), TrinketTintSource.MAP_CODEC);
    }

    public record TrinketTintSource() implements ItemTintSource {
        public static final MapCodec<TrinketTintSource> MAP_CODEC = MapCodec.unit(new TrinketTintSource());

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
            return ARGB.opaque(TrinketItem.getPotionColor(stack));
        }

        @Override
        public MapCodec<TrinketTintSource> type() {
            return MAP_CODEC;
        }
    }
}
