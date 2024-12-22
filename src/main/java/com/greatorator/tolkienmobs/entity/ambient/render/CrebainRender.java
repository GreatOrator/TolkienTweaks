package com.greatorator.tolkienmobs.entity.ambient.render;

import com.greatorator.tolkienmobs.entity.ambient.CrebainEntity;
import com.greatorator.tolkienmobs.entity.ambient.model.CrebainModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class CrebainRender extends GeoEntityRenderer<CrebainEntity> {
    public CrebainRender(EntityRendererProvider.Context context) {
        super(context, new CrebainModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public ResourceLocation getTextureLocation(CrebainEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/crebain.png");
    }

    @Override
    public void render(CrebainEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}