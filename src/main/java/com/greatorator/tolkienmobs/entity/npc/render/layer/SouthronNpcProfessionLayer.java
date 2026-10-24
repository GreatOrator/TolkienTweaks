package com.greatorator.tolkienmobs.entity.npc.render.layer;

import com.greatorator.tolkienmobs.entity.npc.SouthronEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class SouthronNpcProfessionLayer extends GeoRenderLayer<SouthronEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/villager/profession/cleric.png");

	public SouthronNpcProfessionLayer(GeoRenderer<SouthronEntity> entityRenderer) {
        super(entityRenderer);
    }

    // Apply the glasses texture layer to the existing geo model, and render it over the top of the existing model
    @Override
    public void render(PoseStack poseStack, SouthronEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int colour) {
        RenderType armorRenderType = RenderType.armorCutoutNoCull(TEXTURE);

        getRenderer().reRender(getDefaultBakedModel(animatable, getRenderer()), poseStack, bufferSource, animatable, armorRenderType,
                bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                colour);
    }
}
