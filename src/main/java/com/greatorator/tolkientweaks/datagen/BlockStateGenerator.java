package com.greatorator.tolkientweaks.datagen;

import com.google.gson.JsonElement;
import com.greatorator.tolkientweaks.TolkienTweaks;
import net.minecraft.block.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 28/2/20.
 */
public class BlockStateGenerator extends BlockStateProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = null;


    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, TolkienTweaks.MODID, exFileHelper);
    }

    //So this is where blockStates are generated. This is also where you generate simple block models or implement more advanced custom models.

    @Override
    protected void registerStatesAndModels() {
        // Metals & Gems
//        simpleBlock(TTMContent.ORE_MITHRIL.get());

        // Custom
//        simpleBlock(TTMContent.BLOCK_HALLOWED.get(), models().cubeBottomTop("block_hallowed", modLoc("block/block_hallowed_side"), modLoc("block/block_hallowed"), modLoc("block/block_hallowed_top")));
//        horizontalBlock(TTMContent.BACKPACK.get(), models().getExistingFile(modLoc("block/container_backpack")), 0);

//        ModelFile barrelMithril = models().cubeBottomTop("barrel_mithril", modLoc("block/barrel_mithril_side"), modLoc("block/barrel_mithril_bottom"), modLoc("block/barrel_mithril_top"));
//        ModelFile barrelMithrilOpen = models().cubeBottomTop("barrel_mithril_open", modLoc("block/barrel_mithril_side"), modLoc("block/barrel_mithril_bottom"), modLoc("block/barrel_mithril_top_open"));
//        directionalBlock(TTMContent.BARREL_MITHRIL.get(), state -> state.getValue(MithrilBarrelBlock.OPEN) ? barrelMithrilOpen : barrelMithril);


        //Bellow is some extra example stuff I left in for reference
        if (true) return;


        // Unnecessarily complicated example to showcase how manual building works
        ModelFile birchFenceGate = models().fenceGate("birch_fence_gate", mcLoc("block/birch_planks"));
        ModelFile birchFenceGateOpen = models().fenceGateOpen("birch_fence_gate_open", mcLoc("block/birch_planks"));
        ModelFile birchFenceGateWall = models().fenceGateWall("birch_fence_gate_wall", mcLoc("block/birch_planks"));
        ModelFile birchFenceGateWallOpen = models().fenceGateWallOpen("birch_fence_gate_wall_open", mcLoc("block/birch_planks"));
        ModelFile invisbleModel = new ModelFile.UncheckedModelFile(new ResourceLocation("builtin/generated"));
        VariantBlockStateBuilder builder = getVariantBuilder(Blocks.BIRCH_FENCE_GATE);
        for (Direction dir : FenceGateBlock.FACING.getPossibleValues()) {
            int angle = (int) dir.toYRot();
            builder
                    .partialState()
                    .with(FenceGateBlock.FACING, dir)
                    .with(FenceGateBlock.IN_WALL, false)
                    .with(FenceGateBlock.OPEN, false)
                    .modelForState()
                    .modelFile(invisbleModel)
                    .nextModel()
                    .modelFile(birchFenceGate)
                    .rotationY(angle)
                    .uvLock(true)
                    .weight(100)
                    .addModel()
                    .partialState()
                    .with(FenceGateBlock.FACING, dir)
                    .with(FenceGateBlock.IN_WALL, false)
                    .with(FenceGateBlock.OPEN, true)
                    .modelForState()
                    .modelFile(birchFenceGateOpen)
                    .rotationY(angle)
                    .uvLock(true)
                    .addModel()
                    .partialState()
                    .with(FenceGateBlock.FACING, dir)
                    .with(FenceGateBlock.IN_WALL, true)
                    .with(FenceGateBlock.OPEN, false)
                    .modelForState()
                    .modelFile(birchFenceGateWall)
                    .rotationY(angle)
                    .uvLock(true)
                    .addModel()
                    .partialState()
                    .with(FenceGateBlock.FACING, dir)
                    .with(FenceGateBlock.IN_WALL, true)
                    .with(FenceGateBlock.OPEN, true)
                    .modelForState()
                    .modelFile(birchFenceGateWallOpen)
                    .rotationY(angle)
                    .uvLock(true)
                    .addModel();
        }

        // Realistic examples using helpers
//            simpleBlock(Blocks.STONE, model -> ObjectArrays.concat(
//                    ConfiguredModel.allYRotations(model, 0, false),
//                    ConfiguredModel.allYRotations(model, 180, false),
//                    ConfiguredModel.class));

        // From here on, models are 1-to-1 copies of vanilla (except for model locations) and will be tested as such below
        ModelFile block = models().getBuilder("block").transforms()
                .transform(ModelBuilder.Perspective.GUI)
                .rotation(30, 225, 0)
                .scale(0.625f)
                .end()
                .transform(ModelBuilder.Perspective.GROUND)
                .translation(0, 3, 0)
                .scale(0.25f)
                .end()
                .transform(ModelBuilder.Perspective.FIXED)
                .scale(0.5f)
                .end()
                .transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
                .rotation(75, 45, 0)
                .translation(0, 2.5f, 0)
                .scale(0.375f)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
                .rotation(0, 45, 0)
                .scale(0.4f)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT)
                .rotation(0, 225, 0)
                .scale(0.4f)
                .end()
                .end();

        models().getBuilder("cube")
                .parent(block)
                .element()
                .allFaces((dir, face) -> face.texture("#" + dir.getSerializedName()).cullface(dir));


        ModelFile furnace = models().orientable("furnace", mcLoc("block/furnace_side"), mcLoc("block/furnace_front"), mcLoc("block/furnace_top"));
        ModelFile furnaceLit = models().orientable("furnace_on", mcLoc("block/furnace_side"), mcLoc("block/furnace_front_on"), mcLoc("block/furnace_top"));

        getVariantBuilder(Blocks.FURNACE)
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(state.getValue(FurnaceBlock.LIT) ? furnaceLit : furnace)
                        .rotationY((int) state.getValue(FurnaceBlock.FACING).getOpposite().toYRot())
                        .build()
                );

        ModelFile barrel = models().cubeBottomTop("barrel", mcLoc("block/barrel_side"), mcLoc("block/barrel_bottom"), mcLoc("block/barrel_top"));
        ModelFile barrelOpen = models().cubeBottomTop("barrel_open", mcLoc("block/barrel_side"), mcLoc("block/barrel_bottom"), mcLoc("block/barrel_top_open"));
        directionalBlock(Blocks.BARREL, state -> state.getValue(BarrelBlock.OPEN) ? barrelOpen : barrel); // Testing custom state interpreter

//        logBlock((LogBlock) Blocks.ACACIA_LOG);

        stairsBlock((StairsBlock) Blocks.ACACIA_STAIRS, "acacia", mcLoc("block/acacia_planks"));
        slabBlock((SlabBlock) Blocks.ACACIA_SLAB, Blocks.ACACIA_PLANKS.getRegistryName(), mcLoc("block/acacia_planks"));

        fenceBlock((FenceBlock) Blocks.ACACIA_FENCE, "acacia", mcLoc("block/acacia_planks"));
        fenceGateBlock((FenceGateBlock) Blocks.ACACIA_FENCE_GATE, "acacia", mcLoc("block/acacia_planks"));

        wallBlock((WallBlock) Blocks.COBBLESTONE_WALL, "cobblestone", mcLoc("block/cobblestone"));

        paneBlock((PaneBlock) Blocks.GLASS_PANE, "glass", mcLoc("block/glass"), mcLoc("block/glass_pane_top"));

        doorBlock((DoorBlock) Blocks.ACACIA_DOOR, "acacia", mcLoc("block/acacia_door_bottom"), mcLoc("block/acacia_door_top"));
        trapdoorBlock((TrapDoorBlock) Blocks.ACACIA_TRAPDOOR, "acacia", mcLoc("block/acacia_trapdoor"), true);
        trapdoorBlock((TrapDoorBlock) Blocks.OAK_TRAPDOOR, "oak", mcLoc("block/oak_trapdoor"), false); // Test a non-orientable trapdoor

        simpleBlock(Blocks.TORCH, models().torch("torch", mcLoc("block/torch")));
        horizontalBlock(Blocks.WALL_TORCH, models().torchWall("wall_torch", mcLoc("block/torch")), 90);
    }


    private void dummyBlock(Block block) {
        ModelFile model = models()//
                .withExistingParent("dummy", "block")//
                .texture("particle", "minecraft:block/glass");
        simpleBlock(block, model);
    }

    public void directionalFromNorth(Block block, ModelFile model) {
        directionalFromNorth(block, model, 180);
    }

    public void directionalFromNorth(Block block, ModelFile model, int angleOffset) {
        directionalFromNorth(block, $ -> model, angleOffset);
    }

    public void directionalFromNorth(Block block, Function<BlockState, ModelFile> modelFunc) {
        directionalFromNorth(block, modelFunc, 180);
    }

    public void directionalFromNorth(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        getVariantBuilder(block)
                .forAllStates(state -> {
                    Direction dir = state.getValue(BlockStateProperties.FACING);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                            .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
                            .build();
                });
    }

    public void directionalFromNorthHoz(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        getVariantBuilder(block)
                .forAllStates(state -> {
                    Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                    return ConfiguredModel.builder()
                            .modelFile(modelFunc.apply(state))
                            .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
                            .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
                            .build();
                });
    }

    private void cropsBlock(Block block, String name, int age) {
        String path = block.getRegistryName().getPath();
        //Integer cropAge = BlockTTMCrops.AGE;

        for (int i = 0; i < age; i++) {
            ResourceLocation resource = new ResourceLocation(block.getRegistryName().getNamespace(), "block/" + name + "_stage" + i);
            ModelFile model = models().crop(path + "_stage" + i, resource);
            simpleBlock(block, models().withExistingParent(path, mcLoc("block/crop")).texture("plant", resource));
//            getVariantBuilder(block)
//                .partialState().with(cropAge, i)
//                .modelForState().modelFile(path + "_stage" +  i).addModel();
        }

    }

    @Override
    public String getName() {
        return "Tolkien Tweaks - Mobs Edition Blockstates";
    }
}
