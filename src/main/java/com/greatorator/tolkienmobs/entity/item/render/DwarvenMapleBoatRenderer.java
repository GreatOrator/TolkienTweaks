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

public class DwarvenMapleBoatRenderer extends AbstractBoatRenderer {
    private static final ResourceLocation DWARVEN_MAPLE_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/boat/dwarven_maple.png");
    private static final ResourceLocation DWARVEN_MAPLE_CHEST_BOAT = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/dwarven_maple.png");

    private final EntityModel<BoatRenderState> model;
    private final ResourceLocation texture;

    public DwarvenMapleBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context);
        this.model = new BoatModel(context.bakeLayer(chest ? TolkienModelLayers.DWARVEN_MAPLE_CHEST_BOAT : TolkienModelLayers.DWARVEN_MAPLE_BOAT));
        this.texture = chest ? DWARVEN_MAPLE_CHEST_BOAT : DWARVEN_MAPLE_BOAT;
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
