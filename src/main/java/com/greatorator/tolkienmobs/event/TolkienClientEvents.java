package com.greatorator.tolkienmobs.event;

import com.greatorator.tolkienmobs.init.TolkienItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

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
