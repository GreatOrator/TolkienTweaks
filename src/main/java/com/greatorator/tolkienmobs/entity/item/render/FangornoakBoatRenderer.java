package com.greatorator.tolkienmobs.entity.item.render;

import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class FangornoakBoatRenderer extends AbstractBoatRenderer {
    private static final Identifier FANGORNOAK_BOAT = Identifier.fromNamespaceAndPath(MODID, "textures/entity/boat/mallorn.png");
    private static final Identifier FANGORNOAK_CHEST_BOAT = Identifier.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/mallorn.png");

    private final EntityModel<BoatRenderState> model;

    /**
     * 26.1 has the base boat renderer do the drawing: it takes the texture up front and submits the
     * model itself, so a boat renderer only supplies the model and the texture.
     */
    public FangornoakBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context, chest ? FANGORNOAK_CHEST_BOAT : FANGORNOAK_BOAT);
        this.model = new BoatModel(context.bakeLayer(chest ? TolkienModelLayers.FANGORNOAK_CHEST_BOAT : TolkienModelLayers.FANGORNOAK_BOAT));
    }

    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }
}
