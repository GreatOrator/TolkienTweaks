package com.greatorator.tolkientweaks.event.client;

import com.greatorator.tolkientweaks.TolkienContent;
import com.greatorator.tolkientweaks.block.models.ChameleonBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void onModelBakeEvent(ModelBakeEvent event) {
        for (BlockState blockState : TolkienContent.CHAMELEON_BLOCK.get().getStateDefinition().getPossibleStates()) {
            ModelResourceLocation variantMRL = BlockModelShapes.stateToModelLocation(blockState);
            IBakedModel existingModel = event.getModelRegistry().get(variantMRL);
            if (existingModel == null) {
                LOGGER.warn("Did not find the expected vanilla baked model(s) for blockCamouflage in registry");
            } else if (existingModel instanceof ChameleonBakedModel) {
                LOGGER.warn("Tried to replace CamouflagedBakedModel twice");
            } else {
                ChameleonBakedModel customModel = new ChameleonBakedModel(existingModel);
                event.getModelRegistry().put(variantMRL, customModel);
            }
        }
    }
}
