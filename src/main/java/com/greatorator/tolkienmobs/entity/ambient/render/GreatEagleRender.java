package com.greatorator.tolkienmobs.entity.ambient.render;

import com.greatorator.tolkienmobs.entity.ambient.GreatEagleEntity;
import com.greatorator.tolkienmobs.entity.ambient.model.GreatEagleModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class GreatEagleRender extends GeoEntityRenderer<GreatEagleEntity> {
    public GreatEagleRender(EntityRendererProvider.Context context) {
        super(context, new GreatEagleModel());
        this.shadowRadius = 1.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(GreatEagleEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/entitygreateagle.png");
    }

    @Override
    public void render(GreatEagleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.scale(1.4F, 1.4F, 1.4F);

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}