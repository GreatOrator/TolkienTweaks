package com.greatorator.tolkienmobs.entity.npc.render.layer;

import com.greatorator.tolkienmobs.entity.npc.OrcEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class OrcProfessionLayer extends GeoRenderLayer<OrcEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/villager/profession/cleric.png");

	public OrcProfessionLayer(GeoRenderer<OrcEntity> entityRenderer) {
        super(entityRenderer);
    }

    // Apply the glasses texture layer to the existing geo model, and render it over the top of the existing model
    @Override
    public void render(PoseStack poseStack, OrcEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int colour) {
        RenderType armorRenderType = RenderType.armorCutoutNoCull(TEXTURE);

        getRenderer().reRender(getDefaultBakedModel(animatable, getRenderer()), poseStack, bufferSource, animatable, armorRenderType,
                bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                colour);
    }
}
