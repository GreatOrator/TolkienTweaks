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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import javax.annotation.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * Tints the gem layer of a trinket with the colour of the potion it holds, and the packed leaf in a
 * pipe's bowl with the colour of the strain the smoker is carrying. Both colours live in tint
 * sources, which an item model names in its {@code tints} list, rather than in an item colour
 * handler: the trinkets list a plain white first layer and the trinket source for the gem, and the
 * pipe points its bowl faces at tint index 0 and lists the pipe-weed source there.
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ColorHandler {

    @SubscribeEvent
    public static void itemColourEvent(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(TolkienMobsMain.resLoc("trinket"), TrinketTintSource.MAP_CODEC);
        event.register(TolkienMobsMain.resLoc("pipe_leaf"), PipeLeafTintSource.MAP_CODEC);
    }

    /**
     * The leaf a pipe's bowl is packed with. A pipe carries no strain of its own: it smokes whichever
     * leaf the smoker is holding, so the bowl is coloured by asking the same question the pipe asks
     * when it is lit, {@link PipeweedStrain#find}, which means the bowl shows the leaf the next draw
     * would spend. A bowl that has already been smoked out shows ash instead, whatever is in the pack,
     * until the pipe is drawn on again, and so does one whose smoker carries no leaf at all.
     */
    public record PipeLeafTintSource() implements ItemTintSource {
        public static final MapCodec<PipeLeafTintSource> MAP_CODEC = MapCodec.unit(new PipeLeafTintSource());

        /** An empty bowl, as packed RGB: the colour of leaf burnt out of it. */
        private static final int ASH = 0x6F5A47;

        /**
         * How much darker a bowl of leaf is than the flower it came from. The leaf sits down in the
         * bowl, away from the light, and the pipe's own wood is a bright honey gold: at full flower
         * strength Old Toby's salmon was close enough to that gold to read as bare wood.
         */
        private static final float SHADE = 0.78F;

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
            if (PipeItem.isSpent(stack)) {
                return ARGB.opaque(ASH);
            }
            if (entity instanceof Player player) {
                PipeweedStrain strain = PipeweedStrain.find(player);
                if (strain != null) {
                    return ARGB.opaque(shaded(strain.bloom()));
                }
            }
            return ARGB.opaque(ASH);
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
