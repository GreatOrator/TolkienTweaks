package com.greatorator.tolkienmobs.entity.boss.render;

import com.geckolib.cache.model.BakedGeoModel;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.greatorator.tolkienmobs.entity.boss.GwahirEntity;
import com.greatorator.tolkienmobs.entity.boss.model.GwahirModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GwahirRender extends GeoEntityRenderer<GwahirEntity> {
    public GwahirRender(EntityRendererProvider.Context context) {
        super(context, new GwahirModel());
        this.shadowRadius = 1.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(GwahirEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/entitygreateagle.png");
    }

    @Override
    public void preRender(PoseStack poseStack, GwahirEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!isReRender) {
            poseStack.scale(1.4F, 1.4F, 1.4F);        }


        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
