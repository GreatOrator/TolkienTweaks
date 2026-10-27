package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.world.registration.ArdaChunkGenerator;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** The chunk generators the mod's own dimension is built with. */
public class TolkienChunkGenerators {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, MODID);

    /**
     * Arda's terrain: the overworld's own noise, cut to the shape {@code ArdaMap} draws. Registering
     * the codec is what lets a dimension JSON name it, and the generated Arda stem does.
     */
    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<ArdaChunkGenerator>> ARDA =
            CHUNK_GENERATORS.register("arda", () -> ArdaChunkGenerator.CODEC);
}
