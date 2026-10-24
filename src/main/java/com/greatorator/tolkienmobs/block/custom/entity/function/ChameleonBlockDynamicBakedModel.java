package com.greatorator.tolkienmobs.block.custom.entity.function;

import com.greatorator.tolkienmobs.block.custom.ChameleonBlock;
import com.greatorator.tolkienmobs.handler.interfaces.block.KeystoneCodeBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.item.custom.KeyItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;

import java.util.List;
import java.util.function.Predicate;

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
	/** Anything a section might hold a camo block of. */
	private static final Predicate<BlockState> CAMO = state -> state.getBlock() instanceof ChameleonBlock<?>;

	/** What the player's two hands held when the camo sections were last rebuilt. */
	private static String heldSignature = "|";

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
	 * The block renders as itself while the player holds something that sees through the disguise, which would
	 * otherwise mask it.
	 */
	private BlockStateModel getActualModel(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		if (isRevealed(level, pos)) {
			return this.delegate;
		}

		BlockState disguisedState = ((ChameleonBlock<?>) state.getBlock()).selectBestAdjacentBlock(level, pos);
		// Prevent circular reference - if disguised state is a ChameleonBlock, use original model
		if (disguisedState.getBlock() instanceof ChameleonBlock) {
			return this.delegate;
		}

		return Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(disguisedState);
	}

	/**
	 * Whether either hand is holding something that undoes the disguise at this position: the dev tool, or a key
	 * carrying the code this block answers to.
	 *
	 * <p>This is asked while a section is being built, off the render thread, so it only reads the position's block
	 * entity from the level view it is given - the render region, which holds copies of the loaded block entities.
	 */
	public static boolean isRevealed(BlockAndTintGetter level, BlockPos pos) {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return false;
		}

		return reveals(player.getMainHandItem(), level, pos) || reveals(player.getOffhandItem(), level, pos);
	}

	private static boolean reveals(ItemStack stack, BlockAndTintGetter level, BlockPos pos) {
		if (stack.is(TolkienItems.ITEM_DEV_TOOL.get())) {
			return true;
		}
		if (!(stack.getItem() instanceof KeyItem)) {
			return false;
		}

		String keyCode = KeyItem.getKeyCode(stack);
		return !keyCode.isEmpty() && keyCode.equals(blockCode(level, pos));
	}

	/** The code the block at this position answers to, or empty when it has none. */
	private static String blockCode(BlockAndTintGetter level, BlockPos pos) {
		return level.getBlockEntity(pos) instanceof KeystoneCodeBlockEntity keystone
				? keystone.getKeyStoneData().keyCode
				: "";
	}

	/**
	 * A section is built once and kept, so the model is only asked about a camo block when that section is rebuilt.
	 * Nothing in the world announces a change of hand item, so a change is turned into a rebuild of every section
	 * that holds a camo block - otherwise the reveal would only appear once the chunk was rebuilt for a reason of
	 * its own.
	 */
	public static void refreshRevealedBlocks() {
		Player player = Minecraft.getInstance().player;
		String signature = player == null ? "" : heldSignature(player);
		if (signature.equals(heldSignature)) {
			return;
		}
		heldSignature = signature;
		if (player == null) {
			return;
		}

		ClientChunkCache chunks = (ClientChunkCache) player.level().getChunkSource();
		LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
		int radius = Minecraft.getInstance().options.getEffectiveRenderDistance();
		ChunkPos centre = player.chunkPosition();

		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				LevelChunk chunk = chunks.getChunk(centre.x() + dx, centre.z() + dz, ChunkStatus.FULL, false);
				if (chunk == null) {
					continue;
				}

				for (int sectionY = chunk.getMinSectionY(); sectionY <= chunk.getMaxSectionY(); sectionY++) {
					if (chunk.getSection(chunk.getSectionIndexFromSectionY(sectionY)).maybeHas(CAMO)) {
						renderer.setSectionDirty(chunk.getPos().x(), sectionY, chunk.getPos().z());
					}
				}
			}
		}
	}

	/**
	 * The hands, in the terms the reveal cares about: the dev tool, and a key together with the code it carries - so a
	 * key whose code is edited while it is held counts as a change, and swapping a sword for a pickaxe does not.
	 */
	private static String heldSignature(Player player) {
		return stackSignature(player.getMainHandItem()) + "|" + stackSignature(player.getOffhandItem());
	}

	private static String stackSignature(ItemStack stack) {
		if (stack.is(TolkienItems.ITEM_DEV_TOOL.get())) {
			return "dev";
		}
		if (stack.getItem() instanceof KeyItem) {
			return "key#" + KeyItem.getKeyCode(stack);
		}

		return "";
	}
}
