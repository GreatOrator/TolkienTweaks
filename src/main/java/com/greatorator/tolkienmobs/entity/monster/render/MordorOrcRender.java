package com.greatorator.tolkienmobs.entity.monster.render;

import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.monster.MordorOrcEntity;
import com.greatorator.tolkienmobs.entity.monster.model.MordorOrcModel;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class MordorOrcRender extends GeoEntityRenderer<MordorOrcEntity> {
    public static final Map<TolkienVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc1.png"));
                map.put(TolkienVariant.RED,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc1.png"));
                map.put(TolkienVariant.ORANGE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc2.png"));
                map.put(TolkienVariant.YELLOW,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc3.png"));
                map.put(TolkienVariant.GREEN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc4.png"));
                map.put(TolkienVariant.BLUE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc1.png"));
                map.put(TolkienVariant.INDIGO,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc2.png"));
                map.put(TolkienVariant.VIOLET,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc3.png"));
                map.put(TolkienVariant.MAGENTA,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc4.png"));
                map.put(TolkienVariant.PINK,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc1.png"));
                map.put(TolkienVariant.GRAY,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc2.png"));
                map.put(TolkienVariant.AQUA,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc3.png"));
                map.put(TolkienVariant.BEIGE,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc4.png"));
                map.put(TolkienVariant.BROWN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc1.png"));
                map.put(TolkienVariant.CORAL,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc2.png"));
                map.put(TolkienVariant.CYAN,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc3.png"));
                map.put(TolkienVariant.LAVENDER,
                        ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/mordororc/mordororc4.png"));
            });

    public MordorOrcRender(EntityRendererProvider.Context context) {
        super(context, new MordorOrcModel());
        this.shadowRadius = 0.30f;
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, MordorOrcEntity animatable) {
                return switch (bone.getName()) {
                    case "rightArm" -> new ItemStack(TolkienItems.SWORD_MORGULIRON.get());
                    case "leftArm" -> new ItemStack(TolkienItems.URUK_BOW.get());
                    default -> null;
                };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, MordorOrcEntity animatable) {
                return switch (bone.getName()) {
                    case "leftArm" -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
                    default -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, MordorOrcEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (animatable.swinging && Objects.equals(bone.getName(), "rightArm")) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-30));
                    poseStack.mulPose(Axis.YP.rotationDegrees(90));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(0));
                    poseStack.translate(0.45D, -0.4D, 0.0D);
                    poseStack.scale(1.0F, 1.0F, 1.0F);
                    super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
                }else if (animatable.getRanged() && Objects.equals(bone.getName(), "leftArm")){
                    poseStack.mulPose(Axis.XP.rotationDegrees(0));
                    poseStack.mulPose(Axis.YP.rotationDegrees(0));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
                    poseStack.translate(0.23D, 0.5D, -0.2D);
                    poseStack.scale(1.0F, 1.0F, 1.0F);
                    super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
                }
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(MordorOrcEntity mordororcEntity) {
        return LOCATION_BY_VARIANT.get(mordororcEntity.getVariant());
    }

    @Override
    public void render(MordorOrcEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
