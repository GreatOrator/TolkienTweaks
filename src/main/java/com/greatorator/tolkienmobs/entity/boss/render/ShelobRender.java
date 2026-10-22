package com.greatorator.tolkienmobs.entity.boss.render;

import software.bernie.geckolib.cache.object.BakedGeoModel;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.greatorator.tolkienmobs.entity.boss.ShelobEntity;
import com.greatorator.tolkienmobs.entity.boss.model.ShelobModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class ShelobRender extends GeoEntityRenderer<ShelobEntity> {
    public ShelobRender(EntityRendererProvider.Context context) {
        super(context, new ShelobModel());
        this.shadowRadius = 1.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(ShelobEntity goblinEntity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/tmshelob.png");
    }

    @Override
    public void preRender(PoseStack poseStack, ShelobEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!isReRender) {
            poseStack.scale(1.5F, 1.5F, 1.5F);        }


        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
