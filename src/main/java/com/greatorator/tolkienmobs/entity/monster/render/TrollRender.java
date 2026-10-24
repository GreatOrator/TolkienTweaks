package com.greatorator.tolkienmobs.entity.monster.render;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.util.RenderUtil;
import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.monster.TrollEntity;
import com.greatorator.tolkienmobs.entity.monster.model.TrollModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;



import java.util.List;
import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The troll's variant rides on the render state now, and so does his club: 5 lets the renderer fill the
 * state during extraction, and the item layer reads the posed bone from that state instead of the entity.
 * Its 1.6x size moved from {@code preRender} to {@code scaleModelForRender}, where 5 scales a model.
 */
public class TrollRender extends GeoEntityRenderer<TrollEntity, TolkienGeoRenderState> {
    public static final Map<TolkienVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll1.png"));
                map.put(TolkienVariant.RED,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll1.png"));
                map.put(TolkienVariant.ORANGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll2.png"));
                map.put(TolkienVariant.YELLOW,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll3.png"));
                map.put(TolkienVariant.GREEN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll4.png"));
                map.put(TolkienVariant.BLUE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll1.png"));
                map.put(TolkienVariant.INDIGO,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll2.png"));
                map.put(TolkienVariant.VIOLET,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll3.png"));
                map.put(TolkienVariant.MAGENTA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll4.png"));
                map.put(TolkienVariant.PINK,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll1.png"));
                map.put(TolkienVariant.GRAY,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll2.png"));
                map.put(TolkienVariant.AQUA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll3.png"));
                map.put(TolkienVariant.BEIGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll4.png"));
                map.put(TolkienVariant.BROWN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll1.png"));
                map.put(TolkienVariant.CORAL,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll2.png"));
                map.put(TolkienVariant.CYAN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll3.png"));
                map.put(TolkienVariant.LAVENDER,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/troll/cave_troll4.png"));
            });

    public TrollRender(EntityRendererProvider.Context context) {
        super(context, new TrollModel());
        this.shadowRadius = 0.50f;
        withRenderLayer(new BlockAndItemGeoLayer<TrollEntity, Void, TolkienGeoRenderState>(context, this) {
            /** The club he always carries, so 5 only needs to say where it goes, not whether it shows. */
            @Override
            protected List<RenderData> getRelevantBones(TrollEntity animatable, @Nullable Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
                ItemDisplayContext hand = ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                ItemStack stack = new ItemStack(TolkienItems.CLUB_WOODEN.get());

                return List.of(RenderData.item("rightArm", hand,
                        RenderUtil.createRenderStateForItem(stack, this.itemModelResolver, hand, animatable)));
            }

            @Override
            public void addRenderData(TrollEntity animatable, @Nullable Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
                renderState.addGeckolibData(CONTENTS, getRelevantBones(animatable, relatedObject, renderState, partialTick));
            }

            /** What used to be renderStackForBone, with the pose pushed around the submission. */
            @Override
            protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState stackState, ItemDisplayContext displayContext, TolkienGeoRenderState renderState, SubmitNodeCollector renderTasks, int packedLight) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                poseStack.translate(-1.1D, 0.3D, 0.05D);
                poseStack.scale(1.5F, 1.5F, 1.5F);
                super.submitItemStackRender(poseStack, bone, stackState, displayContext, renderState, renderTasks, packedLight);
                poseStack.popPose();
            }
        });
    }

    /** The variant's texture, for the model and for the base class's texture lookup. */
    public static Identifier textureFor(TolkienGeoRenderState renderState) {
        return LOCATION_BY_VARIANT.get(renderState.variant);
    }

    @Override
    public TolkienGeoRenderState createRenderState(TrollEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public void addRenderData(TrollEntity animatable, Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
        renderState.variant = animatable.getVariant();
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, float widthScale, float heightScale) {
        super.scaleModelForRender(renderPassInfo, 1.6F, 1.6F);
    }

    /** The head turn, which the model used to make in setCustomAnimations. */
    @Override
    public void adjustModelBonesForRender(RenderPassInfo<TolkienGeoRenderState> renderPassInfo, BoneSnapshots snapshots) {
        TolkienGeoRenderState renderState = renderPassInfo.renderState();
        float pitch = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float yaw = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);

        snapshots.ifPresent("head", bone -> bone.setRotation(pitch * Mth.DEG_TO_RAD, yaw * 0.5f * Mth.DEG_TO_RAD, 0f));
    }
}
