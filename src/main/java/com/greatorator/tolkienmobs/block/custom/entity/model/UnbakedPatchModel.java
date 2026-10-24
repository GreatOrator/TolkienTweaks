package com.greatorator.tolkienmobs.block.custom.entity.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

/**
 * The unbaked half of the patch model.
 *
 * <p>1.21.5 moved model loading from the block model file to the blockstate file itself: a variant may name a
 * {@link CustomUnbakedBlockStateModel}, which is read from the same object as {@code "type"} plus its own fields.
 * That is what the patch needs, because only a block-state model is handed the state and the position's random
 * source when it is asked for its quads, and those two decide the patch's shape.
 */
public record UnbakedPatchModel(Material material, boolean shaggify) implements CustomUnbakedBlockStateModel {
	public static final MapCodec<UnbakedPatchModel> CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
					Identifier.CODEC.fieldOf("texture").forGetter(model -> model.material().sprite()),
					Codec.BOOL.optionalFieldOf("shaggify", false).forGetter(UnbakedPatchModel::shaggify)
			).apply(i, UnbakedPatchModel::new));

	public UnbakedPatchModel(Identifier texture, boolean shaggify) {
		this(new Material(texture), shaggify);
	}

	/** The patch is drawn entirely from the texture the model names, so it has no parent to resolve. */
	@Override
	public void resolveDependencies(ResolvableModel.Resolver resolver) {
	}

	@Override
	public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
		return CODEC;
	}

	@Override
	public BlockStateModel bake(ModelBaker baker) {
		return new PatchModel(baker.materials().get(this.material, () -> "tolkienmobs:patch"), baker.interner(), this.shaggify);
	}
}
