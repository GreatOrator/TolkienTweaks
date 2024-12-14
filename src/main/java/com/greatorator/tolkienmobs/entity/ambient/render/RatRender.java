package com.greatorator.tolkienmobs.entity.ambient.render;

import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.TolkienVariant;
import com.greatorator.tolkienmobs.entity.ambient.RatEntity;
import com.greatorator.tolkienmobs.entity.ambient.model.RatModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class RatRender extends MobRenderer<RatEntity, RatModel<RatEntity>> {
    private static final Map<TolkienVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/entityttmrat/entityttmrat1.png"));
                map.put(TolkienVariant.RED,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/entityttmrat/entityttmrat2.png"));
                map.put(TolkienVariant.ORANGE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/entityttmrat/entityttmrat3.png"));
                map.put(TolkienVariant.YELLOW,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/entityttmrat/entityttmrat4.png"));
            });

    public RatRender(EntityRendererProvider.Context context) {
        super(context, new RatModel<>(context.bakeLayer(RatModel.LAYER_LOCATION)), 0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(RatEntity entity) {
        return LOCATION_BY_VARIANT.get(entity.getVariant());
    }

    @Override
    public void render(RatEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.20f, 0.20f, 0.20f);
        } else {
            poseStack.scale(0.50f, 0.50f, 0.50f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}