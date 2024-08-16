package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.fluid.TolkienFluid;
import com.greatorator.tolkienmobs.init.types.TolkienFluidTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, MODID);

    public static final Supplier<FlowingFluid> MITHRIL_FLUID = FLUIDS.register("mithril_fluid",
            () -> new BaseFlowingFluid.Source(TolkienFluids.MOLTEN_MITHRIL_WATER_PROPERTIES));
    public static final Supplier<FlowingFluid> MITHRIL_FLOWING = FLUIDS.register("mithril_flowing",
            () -> new BaseFlowingFluid.Flowing(TolkienFluids.MOLTEN_MITHRIL_WATER_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> MITHRIL_BLOCK = TolkienBlocks.BLOCKS.register("mithril",
            () -> new LiquidBlock(TolkienFluids.MITHRIL_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable()));
    public static final DeferredItem<Item> MITHRIL_FLUID_BUCKET = TolkienItems.ITEMS.registerItem("mithril_fluid_bucket",
            properties -> new BucketItem(TolkienFluids.MITHRIL_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final Supplier<FlowingFluid> MORGULIRON_FLUID = FLUIDS.register("morguliron_fluid",
            () -> new BaseFlowingFluid.Source(TolkienFluids.MOLTEN_MORGULIRON_WATER_PROPERTIES));
    public static final Supplier<FlowingFluid> MORGULIRON_FLOWING = FLUIDS.register("morguliron_flowing",
            () -> new BaseFlowingFluid.Flowing(TolkienFluids.MOLTEN_MORGULIRON_WATER_PROPERTIES));
    public static final DeferredBlock<LiquidBlock> MORGULIRON_BLOCK = TolkienBlocks.BLOCKS.register("morguliron",
            () -> new LiquidBlock(TolkienFluids.MORGULIRON_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).noLootTable()));
    public static final DeferredItem<Item> MORGULIRON_FLUID_BUCKET = TolkienItems.ITEMS.registerItem("morguliron_fluid_bucket",
            properties -> new BucketItem(TolkienFluids.MORGULIRON_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final BaseFlowingFluid.Properties MOLTEN_MITHRIL_WATER_PROPERTIES = new BaseFlowingFluid.Properties(
            TolkienFluid.MOLTEN_MITHRIL_LAVA_FLUID_TYPE, MITHRIL_FLUID, MITHRIL_FLOWING)
            .slopeFindDistance(2).levelDecreasePerBlock(1)
            .block(TolkienFluids.MITHRIL_BLOCK).bucket(TolkienFluids.MITHRIL_FLUID_BUCKET);
    public static final BaseFlowingFluid.Properties MOLTEN_MORGULIRON_WATER_PROPERTIES = new BaseFlowingFluid.Properties(
            TolkienFluid.MOLTEN_MORGULIRON_LAVA_FLUID_TYPE, MORGULIRON_FLUID, MORGULIRON_FLOWING)
            .slopeFindDistance(2).levelDecreasePerBlock(1)
            .block(TolkienFluids.MORGULIRON_BLOCK).bucket(TolkienFluids.MORGULIRON_FLUID_BUCKET);

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}