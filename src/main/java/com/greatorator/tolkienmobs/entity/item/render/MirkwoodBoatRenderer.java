package com.greatorator.tolkienmobs.entity.item.render;

import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.ResourceLocation;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MirkwoodBoatRenderer extends AbstractBoatRenderer {
    private static final ResourceLocation MIRKWOOD_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/boat/mirkwood.png");
    private static final ResourceLocation MIRKWOOD_CHEST_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/mirkwood.png");

    private final EntityModel<BoatRenderState> model;
    private final ResourceLocation texture;

    public MirkwoodBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context);
        this.model = new BoatModel(context.bakeLayer(chest ? TolkienModelLayers.MIRKWOOD_CHEST_BOAT : TolkienModelLayers.MIRKWOOD_BOAT));
        this.texture = chest ? MIRKWOOD_CHEST_BOAT : MIRKWOOD_BOAT;
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
