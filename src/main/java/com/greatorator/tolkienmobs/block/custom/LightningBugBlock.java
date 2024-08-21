package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienBugBlock;
import com.greatorator.tolkienmobs.block.custom.entity.LightningBugEntity;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienLootTables;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

public class LightningBugBlock extends TolkienBugBlock {

	public static final MapCodec<LightningBugBlock> CODEC = simpleCodec(LightningBugBlock::new);

	public LightningBugBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LightningBugEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, TolkienBlocks.LIGHTNINGBUG.get(), LightningBugEntity::tick);
	}

	@Override
	public ResourceKey<LootTable> getSquishLootTable() {
		return TolkienLootTables.LIGHTNINGBUG_SQUISH_DROPS;
	}

	@Override
	public void destroy(LevelAccessor accessor, BlockPos pos, BlockState state) {
		super.destroy(accessor, pos, state);
		if (accessor.isClientSide())
			Minecraft.getInstance().getSoundManager().stop(TolkienSounds.LIGHTNINGBUG_AMBIENT.get().getLocation(), SoundSource.NEUTRAL);
	}
}
