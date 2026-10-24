package com.greatorator.tolkienmobs.entity.monster.render;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.util.RenderUtil;
import com.greatorator.tolkienmobs.entity.monster.MinotaurEntity;
import com.greatorator.tolkienmobs.entity.monster.model.MinotaurModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;



import java.util.List;

/**
 * The minotaur wears one texture and keeps its translucent render type; the axe now rides on the render
 * state, which is the only thing the item layer and the model get to see in 5.
 */
public class MinotaurRender extends GeoEntityRenderer<MinotaurEntity, TolkienGeoRenderState> {
    public MinotaurRender(EntityRendererProvider.Context context) {
        super(context, new MinotaurModel());
        this.shadowRadius = 0.50f;
        withRenderLayer(new BlockAndItemGeoLayer<MinotaurEntity, Void, TolkienGeoRenderState>(context, this) {
            @Override
            protected List<RenderData> getRelevantBones(MinotaurEntity animatable, @Nullable Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
                ItemDisplayContext hand = ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                ItemStack stack = new ItemStack(TolkienItems.AXE_MORGULIRON.get());

                return List.of(RenderData.item("rightArm", hand,
                        RenderUtil.createRenderStateForItem(stack, this.itemModelResolver, hand, animatable)));
            }

            @Override
            public void addRenderData(MinotaurEntity animatable, @Nullable Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
                renderState.addGeckolibData(CONTENTS, getRelevantBones(animatable, relatedObject, renderState, partialTick));
            }

            /** What used to be renderStackForBone, with the pose pushed around the submission. */
            @Override
            protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState stackState, ItemDisplayContext displayContext, TolkienGeoRenderState renderState, SubmitNodeCollector renderTasks, int packedLight) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-40));
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                poseStack.translate(-0.5D, 0.75D, 0.0D);
                poseStack.scale(2.5F, 2.5F, 2.5F);
                super.submitItemStackRender(poseStack, bone, stackState, displayContext, renderState, renderTasks, packedLight);
                poseStack.popPose();
            }
        });
    }

    @Override
    public TolkienGeoRenderState createRenderState(MinotaurEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public RenderType getRenderType(TolkienGeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
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
