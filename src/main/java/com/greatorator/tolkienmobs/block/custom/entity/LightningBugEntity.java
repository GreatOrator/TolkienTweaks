package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LightningBugEntity extends BlockEntity {
	public final float randRot = RandomSource.create().nextInt(4) * 90.0F;
	public int currentYaw;
	public float glowIntensity;
	private int yawDelay;
	private int desiredYaw;
	private boolean glowing;
	private int glowDelay;

	public LightningBugEntity(BlockPos pos, BlockState state) {
		super(TolkienBlocks.LIGHTNINGBUG.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, LightningBugEntity te) {
		if (level.isClientSide()) {
			if (te.anyPlayerInRange() && level.getRandom().nextInt(20) == 0) {
				te.spawnParticles();
			}

			if (te.yawDelay > 0) {
				te.yawDelay--;
			} else {
				if (te.currentYaw == 0 && te.desiredYaw == 0) {
					// make it rotate!
					te.yawDelay = 200 + level.getRandom().nextInt(200);
					te.desiredYaw = level.getRandom().nextInt(15) - level.getRandom().nextInt(15);
				}

				if (te.currentYaw < te.desiredYaw) {
					te.currentYaw++;
				}
				if (te.currentYaw > te.desiredYaw) {
					te.currentYaw--;
				}
				if (te.currentYaw == te.desiredYaw) {
					te.desiredYaw = 0;
				}
			}

			if (te.glowDelay > 0) {
				te.glowDelay--;
			} else {
				if (te.glowing && te.glowIntensity >= 1.0) {
					te.glowing = false;
				}
				if (te.glowing && te.glowIntensity < 1.0) {
					te.glowIntensity += 0.05F;
				}
				if (!te.glowing && te.glowIntensity > 0) {
					te.glowIntensity -= 0.05F;
				}
				if (!te.glowing && te.glowIntensity <= 0) {
					te.glowing = true;
					te.glowIntensity = 0.0F;
					te.glowDelay = level.getRandom().nextInt(50);
				}
			}
		}
	}

	private boolean anyPlayerInRange() {
		return this.getLevel().hasNearbyAlivePlayer(this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + 0.5D, this.getBlockPos().getZ() + 0.5D, 16D);
	}

	private void spawnParticles() {
		double rx = this.getBlockPos().getX() + this.getLevel().getRandom().nextFloat();
		double ry = this.getBlockPos().getY() + this.getLevel().getRandom().nextFloat();
		double rz = this.getBlockPos().getZ() + this.getLevel().getRandom().nextFloat();
		// ModLoader.getMinecraftInstance().effectRenderer.addEffect(fireflyfx);
		// ^ keeping here only for pure lolz
		this.getLevel().addParticle(TolkienParticleTypes.LIGHTNINGBUG.get(), rx, ry, rz, 0, 0, 0);
	}
}