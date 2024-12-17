package com.greatorator.tolkienmobs.handler;

import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import net.minecraft.client.color.item.ItemColors;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ColorHandler {

    @SubscribeEvent
    public static void itemColourEvent(RegisterColorHandlersEvent.Item event) {
        final ItemColors colors = event.getItemColors();

        colors.register((pStack, pTintIndex) -> {
            if (pTintIndex == 1 && pStack.getItem() instanceof TrinketItem) {
                return TrinketItem.getPotionColor(pStack);
            }
            return 0xFFFFFFFF;
        },
                TolkienItems.TRINKET_RING.get(),
                TolkienItems.TRINKET_AMULET.get(),
                TolkienItems.TRINKET_BELT.get(),
                TolkienItems.TRINKET_CLOAK.get(),
                TolkienItems.TRINKET_CHARM.get(),
                TolkienItems.TRINKET_GLOVE.get(),
                TolkienItems.TRINKET_HAT.get());
    }
}
