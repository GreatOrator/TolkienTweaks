package com.greatorator.tolkienmobs.entity.ambient.render;

import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.ambient.ThrushEntity;
import com.greatorator.tolkienmobs.entity.ambient.model.ThrushModel;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class ThrushRender extends MobRenderer<ThrushEntity, ThrushModel<ThrushEntity>> {
    public ThrushRender(EntityRendererProvider.Context context) {
        super(context, new ThrushModel<>(context.bakeLayer(ThrushModel.LAYER_LOCATION)), 0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrushEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/birds/thrush.png");
    }

    @Override
    public void render(ThrushEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}