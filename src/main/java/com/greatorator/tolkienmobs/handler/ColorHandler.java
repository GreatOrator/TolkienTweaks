package com.greatorator.tolkienmobs.handler;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.item.custom.PipeItem;
import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import com.greatorator.tolkienmobs.util.pipeweed.PipeweedStrain;
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
 * Tints the gem layer of a trinket with the color of the potion it holds, and the leaf in a pipe's
 * bowl with the color of the strain that leaf came from. Both colors live in tint sources, which
 * an item model names in its {@code tints} list, rather than in an item color handler: the trinkets
 * list a plain white first layer and the trinket source for the gem, and the pipe points its bowl
 * faces at tint index 0 and lists the pipe-weed source there.
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ColorHandler {

    @SubscribeEvent
    public static void itemColourEvent(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(TolkienMobsMain.resLoc("trinket"), TrinketTintSource.MAP_CODEC);
        event.register(TolkienMobsMain.resLoc("pipe_leaf"), PipeLeafTintSource.MAP_CODEC);
    }

    /**
     * The leaf a pipe's bowl holds. Loading a pipe puts a leaf in the bowl, so the color is read
     * straight off the stack - the same leaf the draw will burn, whatever the smoker is carrying by
     * then, and the same for whoever is looking at it. An empty bowl takes a dull ash brown.
     */
    public record PipeLeafTintSource() implements ItemTintSource {
        public static final MapCodec<PipeLeafTintSource> MAP_CODEC = MapCodec.unit(new PipeLeafTintSource());

        /** An empty bowl, as packed RGB: the color of leaf burnt out of it. */
        private static final int ASH = 0x6F5A47;

        /**
         * How much darker a bowl of leaf is than the flower it came from. The leaf sits down in the
         * bowl, away from the light, and the pipe's own wood is a bright honey gold: at full flower
         * strength Old Toby's salmon was close enough to that gold to read as bare wood.
         */
        private static final float SHADE = 0.78F;

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
            PipeweedStrain bowl = PipeItem.bowlOf(stack);
            return bowl == null ? ARGB.opaque(ASH) : ARGB.opaque(shaded(bowl.bloom()));
        }

        private static int shaded(int rgb) {
            int red = Math.round(((rgb >> 16) & 0xFF) * SHADE);
            int green = Math.round(((rgb >> 8) & 0xFF) * SHADE);
            int blue = Math.round((rgb & 0xFF) * SHADE);
            return red << 16 | green << 8 | blue;
        }

        @Override
        public MapCodec<PipeLeafTintSource> type() {
            return MAP_CODEC;
        }
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
