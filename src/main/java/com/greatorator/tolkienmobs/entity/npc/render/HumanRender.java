package com.greatorator.tolkienmobs.entity.npc.render;

import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.npc.HumanEntity;
import com.greatorator.tolkienmobs.entity.npc.model.HumanModel;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class HumanRender extends GeoEntityRenderer<HumanEntity> {
    public static final Map<TolkienVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human1.png"));
                map.put(TolkienVariant.RED,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human2.png"));
                map.put(TolkienVariant.ORANGE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human3.png"));
                map.put(TolkienVariant.YELLOW,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human4.png"));
                map.put(TolkienVariant.GREEN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human5.png"));
                map.put(TolkienVariant.BLUE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human6.png"));
                map.put(TolkienVariant.INDIGO,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human7.png"));
                map.put(TolkienVariant.VIOLET,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human8.png"));
                map.put(TolkienVariant.MAGENTA,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human9.png"));
                map.put(TolkienVariant.PINK,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human10.png"));
                map.put(TolkienVariant.GRAY,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human11.png"));
                map.put(TolkienVariant.AQUA,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human12.png"));
                map.put(TolkienVariant.BEIGE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human13.png"));
                map.put(TolkienVariant.BROWN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human14.png"));
                map.put(TolkienVariant.CORAL,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human15.png"));
                map.put(TolkienVariant.CYAN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human16.png"));
                map.put(TolkienVariant.LAVENDER,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human1.png"));
            });

    public HumanRender(EntityRendererProvider.Context context) {
        super(context, new HumanModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public ResourceLocation getTextureLocation(HumanEntity entity) {
        return LOCATION_BY_VARIANT.get(entity.getTolkienVariant());
    }

    @Override
    public void render(HumanEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.50f, 0.50f, 0.50f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}