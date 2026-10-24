package com.greatorator.tolkienmobs.entity.item.render;

import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.Identifier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MallornBoatRenderer extends AbstractBoatRenderer {
    private static final Identifier MALLORN_BOAT = Identifier.fromNamespaceAndPath(MODID, "textures/entity/boat/mallorn.png");
    private static final Identifier MALLORN_CHEST_BOAT = Identifier.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/mallorn.png");

    private final EntityModel<BoatRenderState> model;
    private final Identifier texture;

    public MallornBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context);
        this.model = new BoatModel(context.bakeLayer(chest ? TolkienModelLayers.MALLORN_CHEST_BOAT : TolkienModelLayers.MALLORN_BOAT));
        this.texture = chest ? MALLORN_CHEST_BOAT : MALLORN_BOAT;
    }

    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    @Override
    protected RenderType renderType() {
        return this.model.renderType(this.texture);
    }
}
