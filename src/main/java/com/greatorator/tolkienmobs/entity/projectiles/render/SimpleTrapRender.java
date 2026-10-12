package com.greatorator.tolkienmobs.entity.projectiles.render;

import com.greatorator.tolkienmobs.entity.projectiles.SimpleTrapEntity;
import com.greatorator.tolkienmobs.entity.projectiles.model.SimpleTrapModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SimpleTrapRender extends GeoEntityRenderer<SimpleTrapEntity> {
    public SimpleTrapRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SimpleTrapModel<SimpleTrapEntity>());
    }

    @Override
    public void render(SimpleTrapEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float scaleFactor = 2.0F;
        if (animatable.lifeTime <= 1) {
            scaleFactor = 0.0F;
        } else {
            scaleFactor = 2.0F;
        }
        poseStack.scale(scaleFactor, scaleFactor, scaleFactor);

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public RenderType getRenderType(SimpleTrapEntity livingEntity, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        ResourceLocation resourcelocation = this.getTextureLocation(livingEntity);

        return RenderType.itemEntityTranslucentCull(resourcelocation);
    }
}
