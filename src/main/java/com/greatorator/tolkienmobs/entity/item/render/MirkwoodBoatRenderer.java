package com.greatorator.tolkienmobs.entity.item.render;

import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MirkwoodBoatRenderer extends AbstractBoatRenderer {
    private static final Identifier MIRKWOOD_BOAT = Identifier.fromNamespaceAndPath(MODID, "textures/entity/boat/mallorn.png");
    private static final Identifier MIRKWOOD_CHEST_BOAT = Identifier.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/mallorn.png");

    private final EntityModel<BoatRenderState> model;

    /**
     * 26.1 has the base boat renderer do the drawing: it takes the texture up front and submits the
     * model itself, so a boat renderer only supplies the model and the texture.
     */
    public MirkwoodBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context, chest ? MIRKWOOD_CHEST_BOAT : MIRKWOOD_BOAT);
        this.model = new BoatModel(context.bakeLayer(chest ? TolkienModelLayers.MIRKWOOD_CHEST_BOAT : TolkienModelLayers.MIRKWOOD_BOAT));
    }

    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }
}
