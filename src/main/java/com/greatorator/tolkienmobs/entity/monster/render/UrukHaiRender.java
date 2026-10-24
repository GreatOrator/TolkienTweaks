package com.greatorator.tolkienmobs.entity.monster.render;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.util.RenderUtil;
import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.entity.monster.UrukHaiEntity;
import com.greatorator.tolkienmobs.entity.monster.model.UrukHaiModel;
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



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The uruk hai carries a sword and a bow, and the variant, like the two props, rides on the render state
 * now: 5 lets the renderer fill the state during extraction, and the item layer reads the posed bones from
 * that state instead of the entity.
 */
public class UrukHaiRender extends GeoEntityRenderer<UrukHaiEntity, TolkienGeoRenderState> {
    public static final Map<TolkienVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(TolkienVariant.class), map -> {
                map.put(TolkienVariant.DEFAULT,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai1.png"));
                map.put(TolkienVariant.RED,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai1.png"));
                map.put(TolkienVariant.ORANGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai2.png"));
                map.put(TolkienVariant.YELLOW,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai3.png"));
                map.put(TolkienVariant.GREEN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai4.png"));
                map.put(TolkienVariant.BLUE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai1.png"));
                map.put(TolkienVariant.INDIGO,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai2.png"));
                map.put(TolkienVariant.VIOLET,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai3.png"));
                map.put(TolkienVariant.MAGENTA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai4.png"));
                map.put(TolkienVariant.PINK,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai1.png"));
                map.put(TolkienVariant.GRAY,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai2.png"));
                map.put(TolkienVariant.AQUA,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai3.png"));
                map.put(TolkienVariant.BEIGE,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai4.png"));
                map.put(TolkienVariant.BROWN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai1.png"));
                map.put(TolkienVariant.CORAL,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai2.png"));
                map.put(TolkienVariant.CYAN,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai3.png"));
                map.put(TolkienVariant.LAVENDER,
                        Identifier.fromNamespaceAndPath(MODID, "textures/entity/urukhai/urukhai4.png"));
            });

    public UrukHaiRender(EntityRendererProvider.Context context) {
        super(context, new UrukHaiModel());
        this.shadowRadius = 0.30f;
        withRenderLayer(new BlockAndItemGeoLayer<UrukHaiEntity, Void, TolkienGeoRenderState>(context, this) {
            /**
             * The sword only showed while he was swinging and the bow only while a ranged goal was running,
             * and those two flags are still the cue: the list is built during extraction, where 5 wants it.
             */
            @Override
            protected List<RenderData> getRelevantBones(UrukHaiEntity animatable, @Nullable Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
                List<RenderData> contents = new ArrayList<>(2);

                if (animatable.swinging) {
                    ItemDisplayContext hand = ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                    ItemStack sword = new ItemStack(TolkienItems.SWORD_URUK.get());
                    contents.add(RenderData.item("rightArm", hand,
                            RenderUtil.createRenderStateForItem(sword, this.itemModelResolver, hand, animatable)));
                }

                if (animatable.getRanged()) {
                    ItemDisplayContext hand = ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
                    ItemStack bow = new ItemStack(TolkienItems.URUK_BOW.get());
                    contents.add(RenderData.item("leftArm", hand,
                            RenderUtil.createRenderStateForItem(bow, this.itemModelResolver, hand, animatable)));
                }

                return contents;
            }

            @Override
            public void addRenderData(UrukHaiEntity animatable, @Nullable Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
                renderState.addGeckolibData(CONTENTS, getRelevantBones(animatable, relatedObject, renderState, partialTick));
            }

            /** What used to be renderStackForBone, with the pose pushed around the submission. */
            @Override
            protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState stackState, ItemDisplayContext displayContext, TolkienGeoRenderState renderState, SubmitNodeCollector renderTasks, int packedLight) {
                poseStack.pushPose();

                if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                    poseStack.mulPose(Axis.ZP.rotationDegrees(-180));
                    poseStack.translate(0.23D, 0.5D, -0.2D);
                }
                else {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-30));
                    poseStack.mulPose(Axis.YP.rotationDegrees(90));
                    poseStack.translate(0.45D, -0.4D, 0.0D);
                }

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
    public TolkienGeoRenderState createRenderState(UrukHaiEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public void addRenderData(UrukHaiEntity animatable, Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
        renderState.variant = animatable.getVariant();
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
