package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.world.components.structure.SurfaceStructures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE = DeferredRegister.create(Registries.STRUCTURE_TYPE, MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<SurfaceStructures>> SURFACE_STRUCTURES = STRUCTURE.register("tolkien_surface_structures", () -> structureCodec(SurfaceStructures.CODEC));

    private static <T extends Structure> StructureType<T> structureCodec(MapCodec<T> structureCodec) {
        return () -> structureCodec;
    }
}
