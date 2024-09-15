package com.greatorator.tolkienmobs.fluid;

import com.greatorator.tolkienmobs.init.TolkienFluidTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienFluid {
    public static final ResourceLocation WATER_STILL_RL = ResourceLocation.parse("block/water_still");
    public static final ResourceLocation WATER_FLOWING_RL = ResourceLocation.parse("block/water_flow");
    public static final ResourceLocation WATER_OVERLAY_RL = ResourceLocation.parse("block/water_overlay");

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MODID);

    public static final Supplier<FluidType> MOLTEN_MITHRIL_LAVA_FLUID_TYPE = registerFluidType("molten_mithril_lava_fluid",
            new TolkienFluidTypes(WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL, 0xBFCBD2E8,
                    new Vector3f(108f / 255f, 168f / 255f, 212f / 255f),
                    FluidType.Properties.create()));
    public static final Supplier<FluidType> MOLTEN_MORGULIRON_LAVA_FLUID_TYPE = registerFluidType("molten_morguliron_lava_fluid",
            new TolkienFluidTypes(WATER_STILL_RL, WATER_FLOWING_RL, WATER_OVERLAY_RL, 0xBF232B27,
                    new Vector3f(108f / 255f, 168f / 255f, 212f / 255f),
                    FluidType.Properties.create()));

    private static Supplier<FluidType> registerFluidType(String name, FluidType fluidType) {
        return FLUID_TYPES.register(name, () -> fluidType);
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}