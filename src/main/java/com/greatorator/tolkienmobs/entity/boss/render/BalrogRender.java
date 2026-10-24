package com.greatorator.tolkienmobs.entity.boss.render;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.util.RenderUtil;
import com.greatorator.tolkienmobs.entity.boss.BalrogEntity;
import com.greatorator.tolkienmobs.entity.boss.model.BalrogModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * The balrog wears one texture, so the renderer only has to place the whip in its hand now; 5 lets the
 * layer build that from the render state during extraction rather than reading the entity at render time.
 */
public class BalrogRender extends GeoEntityRenderer<BalrogEntity, TolkienGeoRenderState> {
    public BalrogRender(EntityRendererProvider.Context context) {
        super(context, new BalrogModel());
        this.shadowRadius = 1.0f;
        withRenderLayer(new BlockAndItemGeoLayer<BalrogEntity, Void, TolkienGeoRenderState>(context, this) {
            @Override
            protected List<RenderData> getRelevantBones(BalrogEntity animatable, @Nullable Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
                ItemDisplayContext hand = ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
                ItemStack stack = new ItemStack(TolkienItems.WHIP_FIRE.get());

                return List.of(RenderData.item("rightArm", hand,
                        RenderUtil.createRenderStateForItem(stack, this.itemModelResolver, hand, animatable)));
            }

            @Override
            public void addRenderData(BalrogEntity animatable, @Nullable Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
                renderState.addGeckolibData(CONTENTS, getRelevantBones(animatable, relatedObject, renderState, partialTick));
            }

            /** What used to be renderStackForBone, with the pose pushed around the submission. */
            @Override
            protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState stackState, ItemDisplayContext displayContext, TolkienGeoRenderState renderState, SubmitNodeCollector renderTasks, int packedLight) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                poseStack.translate(-0.2D, 0.55D, -2.0D);
                super.submitItemStackRender(poseStack, bone, stackState, displayContext, renderState, renderTasks, packedLight);
                poseStack.popPose();
            }
        });
    }

    @Override
    public TolkienGeoRenderState createRenderState(BalrogEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
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
