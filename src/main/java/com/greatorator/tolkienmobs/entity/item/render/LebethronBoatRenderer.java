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

public class LebethronBoatRenderer extends AbstractBoatRenderer {
    private static final Identifier LEBETHRON_BOAT = Identifier.fromNamespaceAndPath(MODID, "textures/entity/boat/lebethron.png");
    private static final Identifier LEBETHRON_CHEST_BOAT = Identifier.fromNamespaceAndPath(MODID, "textures/entity/chest_boat/lebethron.png");

    private final EntityModel<BoatRenderState> model;
    private final Identifier texture;

    public LebethronBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context);
        this.model = new BoatModel(context.bakeLayer(chest ? TolkienModelLayers.LEBETHRON_CHEST_BOAT : TolkienModelLayers.LEBETHRON_BOAT));
        this.texture = chest ? LEBETHRON_CHEST_BOAT : LEBETHRON_BOAT;
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
