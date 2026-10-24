package com.greatorator.tolkienmobs.entity.monster.render;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.util.RenderUtil;
import com.greatorator.tolkienmobs.entity.monster.NazgulEntity;
import com.greatorator.tolkienmobs.entity.monster.model.NazgulModel;
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
 * The nazgul wears one texture, so the renderer only has to place the sword in his hand now; 5 lets the
 * layer build that from the render state during extraction rather than reading the entity at render time.
 */
public class NazgulRender extends GeoEntityRenderer<NazgulEntity, TolkienGeoRenderState> {
    public NazgulRender(EntityRendererProvider.Context context) {
        super(context, new NazgulModel());
        this.shadowRadius = 0.50f;
        withRenderLayer(new BlockAndItemGeoLayer<NazgulEntity, Void, TolkienGeoRenderState>(context, this) {
            @Override
            protected List<RenderData> getRelevantBones(NazgulEntity animatable, @Nullable Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
                ItemDisplayContext hand = ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                ItemStack stack = new ItemStack(TolkienItems.SWORD_MORGULIRON.get());

                return List.of(RenderData.item("rightArm", hand,
                        RenderUtil.createRenderStateForItem(stack, this.itemModelResolver, hand, animatable)));
            }

            @Override
            public void addRenderData(NazgulEntity animatable, @Nullable Void relatedObject, TolkienGeoRenderState renderState, float partialTick) {
                renderState.addGeckolibData(CONTENTS, getRelevantBones(animatable, relatedObject, renderState, partialTick));
            }

            /** What used to be renderStackForBone, with the pose pushed around the submission. */
            @Override
            protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState stackState, ItemDisplayContext displayContext, TolkienGeoRenderState renderState, SubmitNodeCollector renderTasks, int packedLight) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.XP.rotationDegrees(-30));
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.translate(0.45D, -0.4D, 0.0D);
                super.submitItemStackRender(poseStack, bone, stackState, displayContext, renderState, renderTasks, packedLight);
                poseStack.popPose();
            }
        });
    }

    @Override
    public TolkienGeoRenderState createRenderState(NazgulEntity animatable, Void relatedObject) {
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
