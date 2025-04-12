package com.greatorator.tolkienmobs.block.custom.entity.function;

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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.greatorator.tolkienmobs.TolkienMobsMain.LOGGER;

public class ChameleonBlockDynamicBakedModel extends BakedModelWrapper<BakedModel> {
	public static final ModelProperty<BlockState> DISGUISED_STATE = new ModelProperty<>();
	private final BakedModel oldModel;
	private static boolean loggedError = false; // prevent spamming console

	public ChameleonBlockDynamicBakedModel(BakedModel originalModel) {
		super(originalModel);
		this.oldModel = originalModel;
	}

	@Override
	@Nonnull
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, @Nullable RenderType var5)
	{
		try {
			return getActualBakedModelFromIModelData(extraData).getQuads(state, side, rand);
		} catch (IllegalArgumentException e) {
			return oldModel.getQuads(state, side, rand);
		}
	}

	@Override
	@Nonnull
	public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData) {
		tileData = super.getModelData(world, pos, state, tileData);
		BlockState bestAdjacentBlock = ((ChameleonBlock<?>)state.getBlock()).selectBestAdjacentBlock(world, pos);
		tileData = tileData.derive().with(DISGUISED_STATE, bestAdjacentBlock).build();
		return tileData;
	}

	@Override
	public TextureAtlasSprite getParticleIcon(@Nonnull ModelData data)
	{
		return getActualBakedModelFromIModelData(data).getParticleIcon(data);
	}

	private BakedModel getActualBakedModelFromIModelData(@Nonnull ModelData data) {
		BakedModel retval = oldModel;
		Item item = Minecraft.getInstance().player.getMainHandItem().getItem();

		if (Minecraft.getInstance().player != null && item == TolkienItems.ITEM_DEV_TOOL.get()) {
			return retval;
		} else if (Minecraft.getInstance().player != null && item instanceof KeyItem) {
			return retval;
		} else if (!data.has(DISGUISED_STATE)) {
			if (!loggedError) {
				LOGGER.error("IModelData did not have expected property COPIED_BLOCK");
				loggedError = true;
			}
			return retval;
		} else {
			Optional<BlockState> copiedBlock = Optional.ofNullable(data.get(DISGUISED_STATE));
			if (!copiedBlock.isPresent()) return retval;
			Minecraft mc = Minecraft.getInstance();
			BlockRenderDispatcher blockRendererDispatcher = mc.getBlockRenderer();
			retval = blockRendererDispatcher.getBlockModel(copiedBlock.get());
		}
		return retval;
	}

	@NotNull
	@Override
	public final List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return List.of();
    }

	@Override
	public boolean useAmbientOcclusion() {
		return oldModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d()
	{
		return oldModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return null;
	}

	@Override
	public ItemOverrides getOverrides()
	{
		return oldModel.getOverrides();
	}
}
