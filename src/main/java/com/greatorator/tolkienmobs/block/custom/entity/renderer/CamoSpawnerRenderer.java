package com.greatorator.tolkienmobs.block.custom.entity.renderer;

import com.greatorator.tolkienmobs.block.TolkienBaseSpawner;
import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.state.CamoSpawnerRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class CamoSpawnerRenderer implements BlockEntityRenderer<CamoSpawnerBlockEntity, CamoSpawnerRenderState> {
    private static final float BASE_SCALE = 0.53125F;

    private final EntityRenderDispatcher entityRenderer;

    public CamoSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.entityRenderer();
    }

    @Override
    public CamoSpawnerRenderState createRenderState() {
        return new CamoSpawnerRenderState();
    }

    @Override
    public void extractRenderState(
            CamoSpawnerBlockEntity blockEntity,
            CamoSpawnerRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.displayEntity = null;
        state.spin = 0.0F;
        state.scale = 1.0F;

        Level level = blockEntity.getLevel();
        if (level == null) return;

        TolkienBaseSpawner spawner = blockEntity.getSpawnerBase();
        Entity entity = spawner.getOrCreateDisplayEntity(level, blockEntity.getBlockPos());
        if (entity == null) return;

        state.displayEntity = this.entityRenderer.extractEntity(entity, partialTicks);
        state.displayEntity.lightCoords = state.lightCoords;
        state.spin = (float) Mth.lerp(partialTicks, spawner.getoSpin(), spawner.getSpin()) * 10.0F;
        state.scale = BASE_SCALE;

        float entitySize = Math.max(entity.getBbWidth(), entity.getBbHeight());
        if ((double) entitySize > 1.0) {
            state.scale /= entitySize;
        }
    }

    @Override
    public void submit(CamoSpawnerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.displayEntity == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.4F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.spin));
        poseStack.translate(0.0F, -0.2F, 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-30.0F));
        poseStack.scale(state.scale, state.scale, state.scale);
        this.entityRenderer.submit(state.displayEntity, camera, 0.0, 0.0, 0.0, poseStack, submitNodeCollector);
        poseStack.popPose();
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(CamoSpawnerBlockEntity blockEntity) {
        net.minecraft.core.BlockPos pos = blockEntity.getBlockPos();
        return new net.minecraft.world.phys.AABB(pos.getX() - 1.0, pos.getY() - 1.0, pos.getZ() - 1.0, pos.getX() + 2.0, pos.getY() + 2.0, pos.getZ() + 2.0);
    }
}
