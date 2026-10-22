package com.greatorator.tolkienmobs.util;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.item.custom.CoinPouchItem;
import com.greatorator.tolkienmobs.item.custom.KeyRingItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

import javax.annotation.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * Item model properties the mod's item models ask for by name. They used to be registered as
 * predicates on the item; the game now reads them from the item model definition, where
 * {@code minecraft:range_dispatch} and {@code minecraft:condition} name them by id.
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class TolkienItemProperties {
    @SubscribeEvent
    public static void registerRangeProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(TolkienMobsMain.resLoc("pull"), Pull.MAP_CODEC);
        event.register(TolkienMobsMain.resLoc("fullness"), Fullness.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerConditionalProperties(RegisterConditionalItemModelPropertyEvent event) {
        event.register(TolkienMobsMain.resLoc("pulling"), Pulling.MAP_CODEC);
    }

    /**
     * How far a bow has been drawn, from 0 to 1 over the twenty ticks of a full draw. Zero when the
     * stack is not the one being used, which is what the bow's un-drawn frame wants.
     */
    public record Pull() implements RangeSelectItemModelProperty {
        public static final MapCodec<Pull> MAP_CODEC = MapCodec.unit(new Pull());

        @Override
        public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            if (entity == null || entity.getUseItem() != stack) {
                return 0.0F;
            }
            return (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
        }

        @Override
        public MapCodec<Pull> type() {
            return MAP_CODEC;
        }
    }

    /** True while a bow is being drawn; the bow swaps to its drawn frames only then. */
    public record Pulling() implements ConditionalItemModelProperty {
        public static final MapCodec<Pulling> MAP_CODEC = MapCodec.unit(new Pulling());

        @Override
        public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed,
                           ItemDisplayContext context) {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack;
        }

        @Override
        public MapCodec<Pulling> type() {
            return MAP_CODEC;
        }
    }

    /** How full a coin pouch or key ring is, as a fraction of the slots it holds. */
    public record Fullness() implements RangeSelectItemModelProperty {
        public static final MapCodec<Fullness> MAP_CODEC = MapCodec.unit(new Fullness());

        @Override
        public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            if (stack.getItem() instanceof CoinPouchItem) {
                return CoinPouchItem.getCoinCount(stack);
            }
            if (stack.getItem() instanceof KeyRingItem) {
                return KeyRingItem.getKeyCount(stack);
            }
            return 0.0F;
        }

        @Override
        public MapCodec<Fullness> type() {
            return MAP_CODEC;
        }
    }
}
