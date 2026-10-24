package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.google.common.collect.ImmutableList;
import com.greatorator.tolkienmobs.block.TolkienPatchBlock;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.mojang.blaze3d.platform.Transparency;
import com.mojang.math.Quadrant;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.FaceBakery;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.neoforged.neoforge.client.model.ExtraFaceData;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * The baked half of the patch model.
 *
 * <p>1.21.5 removed {@code BakedModel} from the block pipeline: a block now renders whatever parts a
 * {@link BlockStateModel} collects, and the model is asked for them with the state, position and a random source.
 * That is exactly what a patch needs, because it only knows how far to grow towards its edges from the four
 * connection properties, and its blobs are scattered by the position's random source. So the patch is a
 * {@link BlockStateModel} that builds a fresh {@link BlockStateModelPart} whenever it is asked, and a quad carries
 * its own atlas layer and lighting now instead of the model choosing a render type for the whole block.
 */
public final class PatchModel implements BlockStateModel {
	/** A patch has no rotation or root transform, so every face is baked against the identity. */
	private static final ModelState IDENTITY = new ModelState() {
	};

	private final Material.Baked material;
	private final ModelBaker.Interner interner;
	private final boolean shaggify;
	/** One sprite covers every face, so its translucency is settled once at bake time rather than per quad. */
	private final Transparency transparency;

	public PatchModel(Material.Baked material, ModelBaker.Interner interner, boolean shaggify) {
		this.material = material;
		this.interner = interner;
		this.shaggify = shaggify;
		this.transparency = material.forceTranslucent()
				? Transparency.TRANSLUCENT
				: material.sprite().contents().computeTransparency(0.0F, 0.0F, 1.0F, 1.0F);
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
		parts.add(this.buildPart(state, random));
	}

	@Override
	@Deprecated
	public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
		parts.add(this.buildPart(null, random));
	}

	@Override
	public Material.Baked particleMaterial() {
		return this.material;
	}

	@Override
	public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return this.flags(state);
	}

	@BakedQuad.MaterialFlags
	@Override
	public int materialFlags() {
		return this.flags(null);
	}

	private PatchPart buildPart(@Nullable BlockState state, RandomSource posRandom) {
		boolean north = state == null || state.getValue(TolkienPatchBlock.NORTH);
		boolean east = state == null || state.getValue(TolkienPatchBlock.EAST);
		boolean south = state == null || state.getValue(TolkienPatchBlock.SOUTH);
		boolean west = state == null || state.getValue(TolkienPatchBlock.WEST);

		List<BakedQuad> list = new ArrayList<>();
		ChunkSectionLayer layer = this.layer(state);

		BoundingBox bb = TolkienPatchBlock.AABBFromRandom(posRandom);

		this.quadsFromAABB(list, layer, west ? 0 : bb.minX(), bb.minY(), north ? 0 : bb.minZ(), east ? 16 : bb.maxX(), bb.maxY(), south ? 16 : bb.maxZ());

		if (!this.shaggify)
			return new PatchPart(ImmutableList.copyOf(list), this.material, this.flags(state));

		// Poll these seeds before entering branching code, otherwise placing neighbors will cause odd changes
		long westSeed = posRandom.nextLong();
		long eastSeed = posRandom.nextLong();
		long northSeed = posRandom.nextLong();
		long southSeed = posRandom.nextLong();

		int minY = bb.minY();
		int maxY = bb.maxY();

		// add on shaggy edges
		if (!west) {
			long seed = westSeed;
			seed = seed * seed * 42317861L + seed * 7L;

			int num0 = (int) (seed >> 12 & 3L) + 1;
			int num1 = (int) (seed >> 15 & 3L) + 1;
			int num2 = (int) (seed >> 18 & 3L) + 1;
			int num3 = (int) (seed >> 21 & 3L) + 1;

			int minZ = bb.minZ() + num0;
			int maxZ = bb.maxZ();

			if (maxZ - ((num1 + num2 + num3)) > minZ) {
				// draw two blobs
				int innerZ = bb.maxZ() - num2;
				this.quadsFromAABB(list, layer, bb.minX() - 1, minY, minZ, bb.minX(), maxY, minZ + num1);
				this.quadsFromAABB(list, layer, bb.minX() - 1, minY, innerZ - num3, bb.minX(), maxY, innerZ);
			} else {
				//draw one blob
				this.quadsFromAABB(list, layer, bb.minX() - 1, minY, minZ, bb.minX(), maxY, maxZ - num2);
			}
		}

		if (!east) {
			long seed = eastSeed;
			seed = seed * seed * 42317861L + seed * 17L;

			int num0 = (int) (seed >> 12 & 3L) + 1;
			int num1 = (int) (seed >> 15 & 3L) + 1;
			int num2 = (int) (seed >> 18 & 3L) + 1;
			int num3 = (int) (seed >> 21 & 3L) + 1;

			int minZ = bb.minZ() + num0;
			int maxZ = bb.maxZ();

			if (maxZ - ((num1 + num2 + num3)) > minZ) {
				// draw two blobs
				int innerZ = maxZ - num2;
				this.quadsFromAABB(list, layer, bb.maxX(), minY, minZ, bb.maxX() + 1, maxY, minZ + num1);
				this.quadsFromAABB(list, layer, bb.maxX(), minY, innerZ - num3, bb.maxX() + 1, maxY, innerZ);
			} else {
				//draw one blob
				this.quadsFromAABB(list, layer, bb.maxX(), minY, minZ, bb.maxX() + 1, maxY, maxZ - num2);
			}
		}

		if (!north) {
			long seed = northSeed;
			seed = seed * seed * 42317861L + seed * 23L;

			int num0 = (int) (seed >> 12 & 3L) + 1;
			int num1 = (int) (seed >> 15 & 3L) + 1;
			int num2 = (int) (seed >> 18 & 3L) + 1;
			int num3 = (int) (seed >> 21 & 3L) + 1;

			int minX = bb.minX() + num0;
			int innerX = minX + num1;
			int maxX = bb.maxX() - num2;

			this.quadsFromAABB(list, layer, minX, minY, bb.minZ() - 1, innerX, maxY, bb.minZ());
			this.quadsFromAABB(list, layer, maxX - num3, minY, bb.minZ() - 1, maxX, maxY, bb.minZ());
		}

		if (!south) {
			long seed = southSeed;
			seed = seed * seed * 42317861L + seed * 11L;

			int num0 = (int) (seed >> 12 & 3L) + 1;
			int num1 = (int) (seed >> 15 & 3L) + 1;
			int num2 = (int) (seed >> 18 & 3L) + 1;
			int num3 = (int) (seed >> 21 & 3L) + 1;

			int minX = bb.minX() + num0;
			int maxX = bb.maxX() - num2;

			this.quadsFromAABB(list, layer, minX, minY, bb.maxZ(), minX + num1, maxY, bb.maxZ() + 1);
			this.quadsFromAABB(list, layer, maxX - num3, minY, bb.maxZ(), maxX, maxY, bb.maxZ() + 1);
		}

		return new PatchPart(ImmutableList.copyOf(list), this.material, this.flags(state));
	}

	private void quadsFromAABB(List<BakedQuad> quads, ChunkSectionLayer layer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		quads.add(this.quadFromVectors(Direction.UP, layer, minX, minY, minZ, maxX, maxY, maxZ));
		quads.add(this.quadFromVectors(Direction.NORTH, layer, minX, minY, minZ, maxX, maxY, maxZ));
		quads.add(this.quadFromVectors(Direction.EAST, layer, minX, minY, minZ, maxX, maxY, maxZ));
		quads.add(this.quadFromVectors(Direction.SOUTH, layer, minX, minY, minZ, maxX, maxY, maxZ));
		quads.add(this.quadFromVectors(Direction.WEST, layer, minX, minY, minZ, maxX, maxY, maxZ));
	}

	private BakedQuad quadFromVectors(Direction direction, ChunkSectionLayer layer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		CuboidFace.UVs uvs = switch (direction) {
			case NORTH -> new CuboidFace.UVs(maxX, minZ + 1f, minX, minZ);
			case EAST -> new CuboidFace.UVs(maxX, minZ, maxX - 1f, maxZ);
			case SOUTH -> new CuboidFace.UVs(minX, maxZ, maxX, maxZ - 1f);
			case WEST -> new CuboidFace.UVs(minX, maxZ, minX + 1f, minZ);
			default -> new CuboidFace.UVs(minX, minZ, maxX, maxZ);
		};
		Quadrant rotation = direction == Direction.EAST || direction == Direction.WEST ? Quadrant.R90 : Quadrant.R0;

		// 26.1 picks the atlas layer per quad, so the cutout the clover patch has always drawn in is chosen here.
		Transparency transparency = layer == ChunkSectionLayer.CUTOUT
				? Transparency.of(this.transparency.hasTransparent(), false)
				: this.transparency;

		BakedQuad.MaterialInfo info = this.interner.materialInfo(
				BakedQuad.MaterialInfo.of(this.material, transparency, 0, true, 0, false));

		return FaceBakery.bakeQuad(this.interner, new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ), uvs, rotation, info, direction, IDENTITY, null, ExtraFaceData.DEFAULT);
	}

	private ChunkSectionLayer layer(@Nullable BlockState state) {
		if (state != null && state.is(TolkienBlocks.CLOVER_PATCH))
			return ChunkSectionLayer.CUTOUT;

		return this.transparency.hasTranslucent() ? ChunkSectionLayer.TRANSLUCENT : ChunkSectionLayer.CUTOUT;
	}

	@BakedQuad.MaterialFlags
	private int flags(@Nullable BlockState state) {
		int flags = this.layer(state) == ChunkSectionLayer.TRANSLUCENT ? BakedQuad.FLAG_TRANSLUCENT : 0;
		if (this.material.sprite().contents().isAnimated())
			flags |= BakedQuad.FLAG_ANIMATED;

		return flags;
	}

	/** The patches never cull: their blobs lie in the middle of the block, so every face is drawn as it is. */
	private record PatchPart(List<BakedQuad> quads, Material.Baked particleMaterial, @BakedQuad.MaterialFlags int materialFlags) implements BlockStateModelPart {
		@Override
		public List<BakedQuad> getQuads(@Nullable Direction direction) {
			return direction == null ? this.quads : List.of();
		}

		@Override
		public boolean useAmbientOcclusion() {
			return false;
		}
	}
}
