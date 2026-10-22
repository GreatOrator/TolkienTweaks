package com.greatorator.tolkienmobs.event;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * Mordor and the Ash Mountains lie below sea level in places, and the overworld aquifer floods
 * everything under that line, so their basins and caves fill with water. Which fluid an aquifer
 * uses is a property of the dimension's noise settings rather than of a biome, so no biome
 * setting or surface rule can turn it off; the water is swapped for lava here instead.
 * <p>
 * Doing it on chunk load rather than during generation also converts terrain that was generated
 * before this existed. A converted chunk saves without water, so later loads have nothing to do.
 */
@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
public final class MordorLavaEvents {
    private static final BlockState LAVA = Blocks.LAVA.defaultBlockState();
    private static final Predicate<BlockState> IS_WATER = state -> state.is(Blocks.WATER);

    private MordorLavaEvents() {}

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }
        // Almost every chunk in the world holds neither biome, so let their palettes say so
        // before sampling all two hundred and fifty six columns.
        if (!hasFireBiome(chunk)) {
            return;
        }
        boolean[][] fire = new boolean[16][16];
        boolean any = false;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                fire[x][z] = isFireBiome(chunk, x, z);
                any |= fire[x][z];
            }
        }
        if (!any) {
            return;
        }
        if (replaceWaterWithLava(level, chunk, fire)) {
            chunk.markUnsaved();
            chunk.setLightCorrect(false);
        }
    }

    /**
     * Palette level check for the two biomes. A surface column being Mordor means the section
     * holding that surface holds a Mordor biome cell, so this cannot miss a chunk that the
     * column sampling below would find.
     */
    private static boolean hasFireBiome(LevelChunk chunk) {
        for (LevelChunkSection section : chunk.getSections()) {
            if (section.getBiomes().maybeHas(biome -> biome.is(TolkienBiomes.MORDOR) || biome.is(TolkienBiomes.ASH_MOUNTAINS))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads the biome at the top of a column, the way the locate command does, rather than at the
     * block being replaced: biomes are placed by depth, so the caves under a Mordor basin report
     * cave biomes even though the column standing over them is Mordor.
     */
    private static boolean isFireBiome(LevelChunk chunk, int x, int z) {
        int y = Math.min(chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1, chunk.getMaxY() - 1);
        Holder<Biome> biome = chunk.getNoiseBiome(chunk.getPos().getMinBlockX() + x, y, chunk.getPos().getMinBlockZ() + z);
        return biome.is(TolkienBiomes.MORDOR) || biome.is(TolkienBiomes.ASH_MOUNTAINS);
    }

    /** Replaces the water in the given columns with lava. Returns whether anything changed. */
    private static boolean replaceWaterWithLava(ServerLevel level, LevelChunk chunk, boolean[][] fire) {
        LevelChunkSection[] sections = chunk.getSections();
        boolean changed = false;
        for (int index = 0; index < sections.length; index++) {
            LevelChunkSection section = sections[index];
            // Palette level check, so a section that cannot hold water is never read.
            if (!section.maybeHas(IS_WATER)) {
                continue;
            }
            int bottom = chunk.getMinY() + index * 16;
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        if (!fire[x][z] || !section.getBlockState(x, y, z).is(Blocks.WATER)) {
                            continue;
                        }
                        // Written into the section on purpose. Placing the block through the
                        // chunk runs block placement, whose fluid interactions reach into
                        // neighbouring chunks and deadlock while this one is still loading.
                        section.setBlockState(x, y, z, LAVA);
                        // Lava glows where water did not, so the light engine has to look again;
                        // this only queues the positions, it drains them later.
                        level.getChunkSource().getLightEngine()
                                .checkBlock(new BlockPos(chunk.getPos().getMinBlockX() + x, bottom + y,
                                        chunk.getPos().getMinBlockZ() + z));
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }
}
