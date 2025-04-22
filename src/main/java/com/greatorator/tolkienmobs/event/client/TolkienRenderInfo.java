package com.greatorator.tolkienmobs.event.client;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class TolkienRenderInfo extends DimensionSpecialEffects {

	public TolkienRenderInfo(float cloudHeight, boolean placebo, SkyType fogType, boolean brightenLightMap, boolean entityLightingBottomsLit) {
		super(cloudHeight, placebo, fogType, brightenLightMap, entityLightingBottomsLit);
	}

	@Nullable
	@Override
	public float[] getSunriseColor(float daycycle, float partialTicks) { // Fog color
		return null;
	}

	@Override
	public Vec3 getBrightnessDependentFogColor(Vec3 biomeFogColor, float daylight) {
		return biomeFogColor.multiply(daylight * 0.94F + 0.06F, (daylight * 0.94F + 0.06F), (daylight * 0.91F + 0.09F));
	}

	@Override
	public boolean isFoggyAt(int x, int y) { // true = nearFog
		Player player = Minecraft.getInstance().player;

		if (player != null) {
			Optional<ResourceKey<Biome>> biome = player.level().getBiome(player.blockPosition()).unwrapKey();
			if (biome.isPresent()) {
				boolean spooky = biome.get() == TolkienBiomes.MIRKWOOD || biome.get() == TolkienBiomes.FANGORN;

				if (player.position().y > 20 && !spooky) {
					return false; // If player is above the dark forest then no need to make it so spooky. The darkwood leaves cover everything as low as y42.
				}

				return spooky || biome.get() == TolkienBiomes.BARROW_DOWNS || biome.get() == TolkienBiomes.OLD_FOREST;
			}
		}

		return false;
	}

}
