package com.greatorator.tolkienmobs.event;

import com.google.common.base.Suppliers;
import com.greatorator.tolkienmobs.block.custom.entity.function.ChameleonBlockDynamicBakedModel;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class TolkienClientEvents {
//    private static final Supplier<Block[]> chameleonBlocks = Suppliers.memoize(() -> new Block[] {
//            TolkienBlocks.CHAMELEON_BLOCK.get(),
//            TolkienBlocks.CAMO_GLOWSTONE_BLOCK.get()
//    });
//
//    @SubscribeEvent
//    public static void onModelBakingCompleted(ModelEvent.ModifyBakingResult event) {
//        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
//
//        for (Block block : chameleonBlocks.get()) {
//            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
//                registerChameleonModel(modelRegistry, BuiltInRegistries.BLOCK.getKey(block), state.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
//            }
//        }
//    }
//    private static void registerChameleonModel(Map<ModelResourceLocation, BakedModel> modelRegistry, ResourceLocation rl, String stateString) {
//    ModelResourceLocation mrl = new ModelResourceLocation(rl, stateString);
//
//    modelRegistry.put(mrl, new ChameleonBlockDynamicBakedModel(modelRegistry.get(mrl)));
//    }

    @SubscribeEvent
    public static void onComputeFovModifierEvent(ComputeFovModifierEvent event) {
        if(event.getPlayer().isUsingItem() && event.getPlayer().getUseItem().getItem() == TolkienItems.ELVEN_BOW.get()) {
            float fovModifier = 1f;
            int ticksUsingItem = event.getPlayer().getTicksUsingItem();
            float deltaTicks = (float)ticksUsingItem / 20f;
            if(deltaTicks > 1f) {
                deltaTicks = 1f;
            } else {
                deltaTicks *= deltaTicks;
            }
            fovModifier *= 1f - deltaTicks * 0.15f;
            event.setNewFovModifier(fovModifier);
        }
        if(event.getPlayer().isUsingItem() && event.getPlayer().getUseItem().getItem() == TolkienItems.URUK_BOW.get()) {
            float fovModifier = 1f;
            int ticksUsingItem = event.getPlayer().getTicksUsingItem();
            float deltaTicks = (float)ticksUsingItem / 20f;
            if(deltaTicks > 1f) {
                deltaTicks = 1f;
            } else {
                deltaTicks *= deltaTicks;
            }
            fovModifier *= 1f - deltaTicks * 0.15f;
            event.setNewFovModifier(fovModifier);
        }
    }
}
