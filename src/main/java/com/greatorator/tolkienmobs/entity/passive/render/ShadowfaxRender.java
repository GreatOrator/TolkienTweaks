package com.greatorator.tolkienmobs.entity.passive.render;

import com.greatorator.tolkienmobs.entity.passive.ShadowfaxEntity;
import com.greatorator.tolkienmobs.entity.passive.model.ShadowfaxModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class ShadowfaxRender extends GeoEntityRenderer<ShadowfaxEntity> {
    public ShadowfaxRender(EntityRendererProvider.Context context) {
        super(context, new ShadowfaxModel());
        this.shadowRadius = 1.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(ShadowfaxEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/horse/shadowfax.png");
    }

    @Override
    public void render(ShadowfaxEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public RenderType getRenderType(ShadowfaxEntity livingEntity, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        ResourceLocation resourcelocation = this.getTextureLocation(livingEntity);

        return RenderType.entityCutoutNoCull(resourcelocation);
    }
}