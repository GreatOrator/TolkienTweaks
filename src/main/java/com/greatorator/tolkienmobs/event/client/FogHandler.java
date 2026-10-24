package com.greatorator.tolkienmobs.event.client;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * The overworld's own fog, previously installed as a {@code DimensionSpecialEffects}. 26.1 removed
 * that class, so the two things it did are done here: it dims the fog colour as daylight fades, and
 * it calls fog in the mod's dark woods - the near fog of the three spooky forests at any height, and
 * of the barrow downs and the old forest while the player is under their canopy. The three Mirkwood
 * and Fangorn woods also halve how far the fog is laid out.
 */
public class FogHandler {

	private static boolean CHUNK_LOADED = false;

	private static float SKY_FAR = 0.0F;

	private static float TERRAIN_FAR = 0.0F;
	private static float TERRAIN_NEAR = 0.0F;

	/**
	 * The sky and the terrain used to be set up as two passes with their own state; 26.1 hands one
	 * {@link FogData} to one event, so the sky end and the terrain end are smoothed side by side.
	 */
	protected static void renderFog(ViewportEvent.RenderFog event) {
		ClientLevel level = level();
		LocalPlayer player = player();

		// 26.1 reports the open air as ATMOSPHERIC; the old NONE is gone.
		if (level == null || player == null || event.getType() != FogType.ATMOSPHERIC) {
			return;
		}

		boolean spooky = isSpooky(level, player);
		FogData fog = event.getFogData();

		float skyFar = spooky ? fog.skyEnd * 0.5F : fog.skyEnd;
		float far = spooky ? fog.environmentalEnd * 0.5F : fog.environmentalEnd;
		float near = spooky ? far * 0.75F : isFoggy(level, player) ? fog.environmentalEnd * 0.75F : fog.environmentalStart;

		if (CHUNK_LOADED) {
			SKY_FAR = Mth.lerp(0.003F, SKY_FAR, skyFar);
			TERRAIN_FAR = Mth.lerp(0.003F, TERRAIN_FAR, far);
			TERRAIN_NEAR = Mth.lerp(0.003F * (TERRAIN_NEAR < near ? 0.5F : 2.0F), TERRAIN_NEAR, near);
		} else if (level.isLoaded(player.blockPosition())) { //We do a first-time set up after the chunk the player is in is loaded
			CHUNK_LOADED = true;
			SKY_FAR = skyFar;
			TERRAIN_FAR = far;
			TERRAIN_NEAR = near;
		} else {
			return;
		}

		fog.skyEnd = SKY_FAR;
		fog.environmentalEnd = TERRAIN_FAR;
		fog.environmentalStart = TERRAIN_NEAR;
		//The clouds took the terrain fog before 26.1 gave them their own end, so they follow it here.
		fog.cloudEnd = Math.min(fog.cloudEnd, TERRAIN_FAR);
		event.setFarPlaneDistance(TERRAIN_FAR);
		event.setNearPlaneDistance(TERRAIN_NEAR);
	}

	/**
	 * The old effect dimmed the biome fog colour with the daylight: red and green by
	 * {@code 0.94 * daylight + 0.06}, blue by {@code 0.91 * daylight + 0.09}. 26.1's daylight is the
	 * sky light factor, which runs 1 at noon and 0.24 at midnight exactly as the old value did.
	 */
	protected static void computeFogColor(ViewportEvent.ComputeFogColor event) {
		ClientLevel level = level();
		LocalPlayer player = player();
		if (level == null || player == null) {
			return;
		}

		float daylight = event.getCamera().attributeProbe()
				.getValue(EnvironmentAttributes.SKY_LIGHT_FACTOR, (float) event.getPartialTick());
		event.setRed(event.getRed() * (daylight * 0.94F + 0.06F));
		event.setGreen(event.getGreen() * (daylight * 0.94F + 0.06F));
		event.setBlue(event.getBlue() * (daylight * 0.91F + 0.09F));
	}

	protected static void unloadFog(LevelEvent.Unload event) { //As supernatural as the fog is, it shouldn't follow the player between worlds
		CHUNK_LOADED = false;
	}

	/** The overworld is the only dimension that had the mod's effects installed. */
	private static ClientLevel level() {
		if (Minecraft.getInstance().getCameraEntity() instanceof LocalPlayer player
				&& player.level() instanceof ClientLevel level && level.dimension() == Level.OVERWORLD) {
			return level;
		}
		return null;
	}

	private static LocalPlayer player() {
		return Minecraft.getInstance().getCameraEntity() instanceof LocalPlayer player ? player : null;
	}

	/** The woods whose fog is halved. */
	private static boolean isSpooky(ClientLevel level, LocalPlayer player) {
		return inBiome(level, player, TolkienBiomes.MIRKWOOD);
	}

	/**
	 * The biomes that called fog in the old effect. The three spooky forests are foggy at any height;
	 * the barrow downs and the old forest only under their canopy, since the leaves above the player
	 * are what makes the floor dark. Everywhere else the fog is the biome's own.
	 */
	private static boolean isFoggy(ClientLevel level, LocalPlayer player) {
		boolean spooky = inBiome(level, player, TolkienBiomes.MIRKWOOD)
				|| inBiome(level, player, TolkienBiomes.MIRKWOOD_SPOOKY)
				|| inBiome(level, player, TolkienBiomes.FANGORN);
		if (player.getY() > 20 && !spooky) {
			return false; // If player is above the dark forest then no need to make it so spooky. The darkwood leaves cover everything as low as y42.
		}
		return spooky || inBiome(level, player, TolkienBiomes.BARROW_DOWNS) || inBiome(level, player, TolkienBiomes.OLD_FOREST);
	}

	private static boolean inBiome(ClientLevel level, LocalPlayer player, ResourceKey<Biome> biome) {
		return level.getBiome(player.blockPosition()).is(biome);
	}
}
