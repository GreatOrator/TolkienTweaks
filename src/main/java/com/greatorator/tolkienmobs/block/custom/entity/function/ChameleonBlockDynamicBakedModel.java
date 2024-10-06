package com.greatorator.tolkienmobs.block.custom.entity.function;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.greatorator.tolkienmobs.block.custom.ChameleonBlock;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.item.custom.KeyItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.greatorator.tolkienmobs.TolkienMobsMain.LOGGER;

public class ChameleonBlockDynamicBakedModel implements IDynamicBakedModel {
	public static final ModelProperty<BlockState> DISGUISED_STATE = new ModelProperty<>();
	private final BakedModel oldModel;
	private static boolean loggedError = false; // prevent spamming console

	public ChameleonBlockDynamicBakedModel(BakedModel oldModel) {
		this.oldModel = oldModel;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData modelData, RenderType renderType) {
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != null && model != this)
					return model.getQuads(disguisedState, side, rand, modelData, renderType);
			}
		}
		return oldModel.getQuads(state, side, rand, modelData, renderType);
	}

	@Override
	public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData modelData) {
		BlockState disguisedState = modelData.get(DISGUISED_STATE);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != null && model != this)
					return model.getRenderTypes(disguisedState, rand, ModelData.EMPTY);
			}
		}

		return oldModel.getRenderTypes(state, rand, modelData);
	}

	@Override
	public TextureAtlasSprite getParticleIcon(ModelData data) {
		BlockState disguisedState = data.get(DISGUISED_STATE);

		if (disguisedState != null) {
			Block block = disguisedState.getBlock();

			if (block != Blocks.AIR) {
				BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguisedState);

				if (model != null && model != this)
					return model.getParticleIcon(data);
			}
		}

		return oldModel.getParticleIcon(data);
	}

//	private BakedModel getActualBakedModelFromIModelData(@Nonnull ModelData data) {
//		BakedModel retval = oldModel;
//		Item item = Minecraft.getInstance().player.getMainHandItem().getItem();
//
//		if (Minecraft.getInstance().player != null && item == TolkienItems.ITEM_DEV_TOOL.get()) {
//			return retval;
//		} else if (Minecraft.getInstance().player != null && item instanceof KeyItem) {
//			return retval;
//		} else if (!data.has(DISGUISED_STATE)) {
//			if (!loggedError) {
//				LOGGER.error("IModelData did not have expected property COPIED_BLOCK");
//				loggedError = true;
//			}
//			return retval;
//		} else {
//			BlockState copiedBlock = data.get(DISGUISED_STATE);
//			if (!copiedBlock.isPresent()) return retval;
//			Minecraft mc = Minecraft.getInstance();
//			BlockRenderDispatcher blockRendererDispatcher = mc.getBlockRenderer();
//			retval = blockRendererDispatcher.getBlockModel(copiedBlock.get());
//		}
//		return retval;
//	}

	@Override
	public boolean isGui3d() {
		return oldModel.isGui3d();
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return oldModel.useAmbientOcclusion();
	}

	@Override
	public ItemOverrides getOverrides() {
		return oldModel.getOverrides();
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return oldModel.getParticleIcon();
	}
}
