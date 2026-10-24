package com.greatorator.tolkienmobs.block.custom.entity.function;

import com.greatorator.tolkienmobs.block.custom.ChameleonBlock;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.item.custom.KeyItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;

import java.util.List;

/**
 * Draws a chameleon block as whatever block it is imitating.
 *
 * <p>1.21.5 replaced the baked model with {@link BlockStateModel}, which is handed the level, the position and the
 * state when it collects its parts, so the disguise no longer has to be baked into model data by the block: it is
 * worked out from the neighbours on the spot and the model of that block collects its own parts. The delegate
 * forwards everything else, and the geometry key is deliberately left unset because the disguise depends on the
 * world around the block.
 */
public class ChameleonBlockDynamicBakedModel extends DelegateBlockStateModel {
	public ChameleonBlockDynamicBakedModel(BlockStateModel originalModel) {
		super(originalModel);
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
		this.getActualModel(level, pos, state).collectParts(level, pos, state, random, parts);
	}

	@Override
	public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return this.getActualModel(level, pos, state).particleMaterial(level, pos, state);
	}

	@Override
	public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return this.getActualModel(level, pos, state).materialFlags(level, pos, state);
	}

	/**
	 * The block renders as itself while the player holds the dev tool or a key, which would otherwise mask it.
	 */
	private BlockStateModel getActualModel(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		Player player = Minecraft.getInstance().player;

		if (player != null) {
			Item item = player.getMainHandItem().getItem();
			if (item == TolkienItems.ITEM_DEV_TOOL.get() || item instanceof KeyItem) {
				return this.delegate;
			}
		}

		BlockState disguisedState = ((ChameleonBlock<?>) state.getBlock()).selectBestAdjacentBlock(level, pos);
		// Prevent circular reference - if disguised state is a ChameleonBlock, use original model
		if (disguisedState.getBlock() instanceof ChameleonBlock) {
			return this.delegate;
		}

		return Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(disguisedState);
	}
}
