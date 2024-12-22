package com.greatorator.tolkienmobs.entity.passive.render;

import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.passive.MumakilEntity;
import com.greatorator.tolkienmobs.entity.passive.model.MumakilModel;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MumakilRender extends GeoEntityRenderer<MumakilEntity> {
    public static final Map<TolkienVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil1.png"));
                map.put(TolkienVariant.RED,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil1.png"));
                map.put(TolkienVariant.ORANGE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil2.png"));
                map.put(TolkienVariant.YELLOW,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil3.png"));
                map.put(TolkienVariant.GREEN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil4.png"));
                map.put(TolkienVariant.BLUE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil1.png"));
                map.put(TolkienVariant.INDIGO,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil2.png"));
                map.put(TolkienVariant.VIOLET,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil3.png"));
                map.put(TolkienVariant.MAGENTA,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil1.png"));
                map.put(TolkienVariant.PINK,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil2.png"));
                map.put(TolkienVariant.GRAY,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil3.png"));
                map.put(TolkienVariant.AQUA,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil4.png"));
                map.put(TolkienVariant.BEIGE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil1.png"));
                map.put(TolkienVariant.BROWN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil2.png"));
                map.put(TolkienVariant.CORAL,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil3.png"));
                map.put(TolkienVariant.CYAN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil4.png"));
                map.put(TolkienVariant.LAVENDER,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mumakil/mumakil1.png"));
            });

    public MumakilRender(EntityRendererProvider.Context context) {
        super(context, new MumakilModel());
        this.shadowRadius = 1.5F;
    }

    @Override
    public ResourceLocation getTextureLocation(MumakilEntity entity) {
        return LOCATION_BY_VARIANT.get(entity.getVariant());
    }

    @Override
    public void render(MumakilEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        } 

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}