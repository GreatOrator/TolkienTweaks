package com.greatorator.tolkienmobs.entity.npc.render;

import software.bernie.geckolib.cache.object.BakedGeoModel;

import com.mojang.blaze3d.vertex.VertexConsumer;

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
    private static final ResourceLocation WINTER_HUMAN = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human_winter/winter");
    private static final ResourceLocation HUMAN = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/human/human");

    public static final Map<TolkienVariant, ResourceLocation> LOCATION_BY_VARIANT = textures(HUMAN);
    public static final Map<TolkienVariant, ResourceLocation> WINTER_LOCATION_BY_VARIANT = textures(WINTER_HUMAN);

    /**
     * Builds the variant-to-texture list for one folder. Every variant uses the file matching its id
     * plus one (human1.png for DEFAULT, human2.png for RED and so on); LAVENDER has no file of its
     * own and shares DEFAULT's. Giving both folders the same numbering is what lets a human keep its
     * face when it swaps to winter clothing.
     */
    private static Map<TolkienVariant, ResourceLocation> textures(ResourceLocation base) {
        return Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
            for (TolkienVariant variant : TolkienVariant.values()) {
                int file = variant == TolkienVariant.LAVENDER ? 1 : variant.getId() + 1;
                map.put(variant, base.withSuffix(file + ".png"));
            }
        });
    }

    public static ResourceLocation getTexture(HumanEntity entity) {
        Map<TolkienVariant, ResourceLocation> byVariant = entity.isWinter() ? WINTER_LOCATION_BY_VARIANT : LOCATION_BY_VARIANT;
        return byVariant.get(entity.getTolkienVariant());
    }

    public HumanRender(EntityRendererProvider.Context context) {
        super(context, new HumanModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public ResourceLocation getTextureLocation(HumanEntity entity) {
        return getTexture(entity);
    }

    @Override
    public void preRender(PoseStack poseStack, HumanEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!isReRender) {
            if(animatable.isBaby()) {
                poseStack.scale(0.50f, 0.50f, 0.50f);
            }
        }


        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
