package com.greatorator.tolkienmobs.block.custom.entity.function;

import com.greatorator.tolkienmobs.block.custom.ChameleonBlock;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.item.custom.KeyItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.DelegateBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.greatorator.tolkienmobs.TolkienMobsMain.LOGGER;

/**
 * Draws a chameleon block as whatever block it is imitating.
 *
 * <p>1.21.4 removed NeoForge's {@code BakedModelWrapper} in favour of vanilla's {@link DelegateBakedModel}, which
 * already forwards every method to the model it wraps, so this class only overrides the three methods that decide
 * what the block looks like: the quads, the model data that carries the imitated state, and the particle icon.
 * Item overrides are gone from baked models altogether.
 */
public class ChameleonBlockDynamicBakedModel extends DelegateBakedModel {
	public static final ModelProperty<BlockState> DISGUISED_STATE = new ModelProperty<>();
	private static boolean loggedError = false; // prevent spamming console

	public ChameleonBlockDynamicBakedModel(BakedModel originalModel) {
		super(originalModel);
	}

	@Override
	@Nonnull
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, @Nullable RenderType renderType) {
		try {
			return getActualBakedModelFromIModelData(extraData).getQuads(state, side, rand);
		} catch (IllegalArgumentException e) {
			return this.parent.getQuads(state, side, rand);
		}
	}

	@Override
	@Nonnull
	public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData) {
		ModelData data = super.getModelData(world, pos, state, tileData);
		BlockState bestAdjacentBlock = ((ChameleonBlock<?>) state.getBlock()).selectBestAdjacentBlock(world, pos);
		return data.derive().with(DISGUISED_STATE, bestAdjacentBlock).build();
	}

	@Override
	public TextureAtlasSprite getParticleIcon(@Nonnull ModelData data) {
		return getActualBakedModelFromIModelData(data).getParticleIcon(data);
	}

	/** The block renders as itself while the player holds the dev tool or a key, which would otherwise mask it. */
	@Override
	public boolean usesBlockLight() {
		return false;
	}

	private BakedModel getActualBakedModelFromIModelData(@Nonnull ModelData data) {
		BakedModel retval = this.parent;
		Player player = Minecraft.getInstance().player;

		if (player != null) {
			Item item = player.getMainHandItem().getItem();
			if (item == TolkienItems.ITEM_DEV_TOOL.get() || item instanceof KeyItem) {
				return retval;
			}
		}

		if (!data.has(DISGUISED_STATE)) {
			if (!loggedError) {
				LOGGER.error("IModelData did not have expected property COPIED_BLOCK");
				loggedError = true;
			}
			return retval;
		}

		Optional<BlockState> copiedBlock = Optional.ofNullable(data.get(DISGUISED_STATE));
		if (copiedBlock.isEmpty()) return retval;
		BlockState disguisedState = copiedBlock.get();
		// Prevent circular reference - if disguised state is a ChameleonBlock, use original model
		if (disguisedState.getBlock() instanceof ChameleonBlock) {
			return retval;
		}

		BlockRenderDispatcher blockRendererDispatcher = Minecraft.getInstance().getBlockRenderer();
		return blockRendererDispatcher.getBlockModel(disguisedState);
	}

	@NotNull
	@Override
	public final List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
		return List.of();
	}
}
