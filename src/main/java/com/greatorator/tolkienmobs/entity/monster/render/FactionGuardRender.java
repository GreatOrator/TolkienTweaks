package com.greatorator.tolkienmobs.entity.monster.render;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;
import com.geckolib.util.RenderUtil;
import com.greatorator.tolkienmobs.entity.monster.FactionGuardEntity;
import com.greatorator.tolkienmobs.entity.monster.model.FactionGuardModel;
import com.greatorator.tolkienmobs.entity.util.TolkienGeoRenderState;
import com.greatorator.tolkienmobs.entity.util.TolkienVariant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A guard's skin is picked by two things: the alliance it serves, which chooses the folder, and the
 * variant it rolled, which chooses the file inside it. The West has eight liveries under
 * {@code faction_guard} and Harad five under {@code faction_harad}, so the file number runs one past the
 * variant and is wrapped to the alliance's own count - which means the model never has to know how many
 * skins either side painted.
 */
public class FactionGuardRender extends GeoEntityRenderer<FactionGuardEntity, TolkienGeoRenderState> {

    public FactionGuardRender(EntityRendererProvider.Context context) {
        super(context, new FactionGuardModel());
        this.shadowRadius = 0.50f;
        withRenderLayer(new BlockAndItemGeoLayer<FactionGuardEntity, Void, TolkienGeoRenderState>(context, this) {
            /** Whatever blade the guard was armed with, held the way the brigand holds his. */
            @Override
            protected List<RenderData> getRelevantBones(FactionGuardEntity animatable, @Nullable Void relatedObject,
                                                        TolkienGeoRenderState renderState, float partialTick) {
                var held = new ArrayList<RenderData>(2);
                ItemStack blade = animatable.getMainHandItem();
                if (!blade.isEmpty()) {
                    held.add(RenderData.item("rightArm", ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                            RenderUtil.createRenderStateForItem(blade, this.itemModelResolver,
                                    ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, animatable)));
                }
                // A doorway guard wears its shield on the other arm. Without this the shield would be in the
                // entity all along and seen by nobody, since a guard has no inventory to open.
                ItemStack shield = animatable.getOffhandItem();
                if (!shield.isEmpty()) {
                    held.add(RenderData.item("leftArm", ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                            RenderUtil.createRenderStateForItem(shield, this.itemModelResolver,
                                    ItemDisplayContext.THIRD_PERSON_LEFT_HAND, animatable)));
                }
                return held;
            }

            @Override
            public void addRenderData(FactionGuardEntity animatable, @Nullable Void relatedObject,
                                      TolkienGeoRenderState renderState, float partialTick) {
                renderState.addGeckolibData(CONTENTS, getRelevantBones(animatable, relatedObject, renderState, partialTick));
            }

            /** The same pose the brigand's sword sits in. */
            @Override
            protected void submitItemStackRender(PoseStack poseStack, GeoBone bone, ItemStackRenderState stackState,
                                                 ItemDisplayContext displayContext, TolkienGeoRenderState renderState,
                                                 SubmitNodeCollector renderTasks, int packedLight) {
                poseStack.pushPose();
                // The blade sits in the fist, the way the brigand holds his. A shield is a plate and not a
                // blade: it hangs square across the forearm with the design looking the way the guard looks,
                // low enough to sit on the arm rather than at the shoulder, and the other way round from the
                // turn that showed the back of it to whoever was behind the guard.
                if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                    poseStack.translate(0.0D, -0.45D, -0.05D);
                } else {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-30));
                    poseStack.mulPose(Axis.YP.rotationDegrees(90));
                    poseStack.translate(0.45D, -0.4D, 0.0D);
                }
                super.submitItemStackRender(poseStack, bone, stackState, displayContext, renderState, renderTasks, packedLight);
                poseStack.popPose();
            }
        });
    }

    /** The skin a guard wears: its alliance's folder, its variant's numbered file. */
    public static Identifier textureFor(TolkienGeoRenderState renderState) {
        int file = Math.floorMod(renderState.variant.getId(), FactionGuardEntity.skins(renderState.alliance)) + 1;
        return FactionGuardEntity.skinFolder(renderState.alliance).withSuffix(file + ".png");
    }

    @Override
    public TolkienGeoRenderState createRenderState(FactionGuardEntity animatable, Void relatedObject) {
        return new TolkienGeoRenderState();
    }

    @Override
    public Identifier getTextureLocation(TolkienGeoRenderState renderState) {
        return textureFor(renderState);
    }

    @Override
    public void addRenderData(FactionGuardEntity animatable, Void relatedObject, TolkienGeoRenderState renderState,
                              float partialTick) {
        renderState.alliance = animatable.alliance();
        renderState.variant = TolkienVariant.byId(animatable.variant());
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
