package com.greatorator.tolkienmobs.block.custom.entity.function;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws another block model bigger than it was baked.
 * <p>
 * A model file cannot express this: every element's {@code from}/{@code to} has to sit within
 * {@code -16..32}, and it is checked before the element's own rotation is applied, so a model that
 * fills that box has no room left to grow. The block pipeline hands a model the job of collecting
 * quads, though, and a quad carries its own four corners, so wrapping the model and moving those
 * corners is enough - the block stays part of the chunk mesh, with its ambient occlusion and its
 * materials, and only the geometry changes.
 * <p>
 * Scaling is about the middle of the ground face, so a block keeps standing on its own floor as it
 * grows, and the block's own outline has to be scaled to match or clicks would land where the model
 * used to be.
 */
public class ScaledBlockStateModel extends DelegateBlockStateModel {
    private final float scale;

    public ScaledBlockStateModel(BlockStateModel delegate, float scale) {
        super(delegate);
        this.scale = scale;
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random,
                             List<BlockStateModelPart> parts) {
        this.collectScaled(this.delegate, level, pos, state, random, parts);
    }

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
        int from = parts.size();
        this.delegate.collectParts(random, parts);
        this.scaleParts(parts, from);
    }

    private void collectScaled(BlockStateModel model, BlockAndTintGetter level, BlockPos pos, BlockState state,
                               RandomSource random, List<BlockStateModelPart> parts) {
        int from = parts.size();
        model.collectParts(level, pos, state, random, parts);
        this.scaleParts(parts, from);
    }

    /** Only the parts this model just added are touched; a caller may be collecting several models. */
    private void scaleParts(List<BlockStateModelPart> parts, int from) {
        for (int i = from; i < parts.size(); i++) {
            parts.set(i, new ScaledPart(parts.get(i), this.scale));
        }
    }

    private static BakedQuad scaleQuad(BakedQuad quad, float scale) {
        return new BakedQuad(
                scale(quad.position0(), scale),
                scale(quad.position1(), scale),
                scale(quad.position2(), scale),
                scale(quad.position3(), scale),
                quad.packedUV0(),
                quad.packedUV1(),
                quad.packedUV2(),
                quad.packedUV3(),
                quad.direction(),
                quad.materialInfo(),
                quad.bakedNormals(),
                quad.bakedColors());
    }

    private static Vector3fc scale(Vector3fc position, float scale) {
        return new Vector3f(
                8.0F + (position.x() - 8.0F) * scale,
                position.y() * scale,
                8.0F + (position.z() - 8.0F) * scale);
    }

    /** One part of the bigger model: the quads are scaled as they are asked for, nothing else changes. */
    private record ScaledPart(BlockStateModelPart delegate, float scale) implements BlockStateModelPart {
        @Override
        public List<BakedQuad> getQuads(Direction direction) {
            List<BakedQuad> source = this.delegate.getQuads(direction);
            if (source.isEmpty()) {
                return source;
            }
            List<BakedQuad> scaled = new ArrayList<>(source.size());
            for (BakedQuad quad : source) {
                scaled.add(scaleQuad(quad, this.scale));
            }
            return scaled;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return this.delegate.useAmbientOcclusion();
        }

        @Override
        public Material.Baked particleMaterial() {
            return this.delegate.particleMaterial();
        }

        @Override
        public int materialFlags() {
            return this.delegate.materialFlags();
        }
    }
}
