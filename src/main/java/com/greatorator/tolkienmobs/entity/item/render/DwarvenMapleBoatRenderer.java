package com.greatorator.tolkienmobs.entity.item.render;

import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class DwarvenMapleBoatRenderer extends AbstractBoatRenderer {
    private static final Identifier DWARVEN_MAPLE_BOAT = Identifier.fromNamespaceAndPath(MODID, "textures/entity/boat/mallorn.png");
    private static final Identifier DWARVEN_MAPLE_CHEST_BOAT = Identifier.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/mallorn.png");

    private final EntityModel<BoatRenderState> model;

    /**
     * 26.1 has the base boat renderer do the drawing: it takes the texture up front and submits the
     * model itself, so a boat renderer only supplies the model and the texture.
     */
    public DwarvenMapleBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context, chest ? DWARVEN_MAPLE_CHEST_BOAT : DWARVEN_MAPLE_BOAT);
        this.model = new BoatModel(context.bakeLayer(chest ? TolkienModelLayers.DWARVEN_MAPLE_CHEST_BOAT : TolkienModelLayers.DWARVEN_MAPLE_BOAT));
    }

    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }
}
