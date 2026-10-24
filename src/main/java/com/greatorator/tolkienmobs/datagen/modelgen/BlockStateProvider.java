package com.greatorator.tolkienmobs.datagen.modelgen;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.WallSide;

/**
 * Writes blockstate JSON, and the block and item models its shapes need.
 *
 * <p>Replaces NeoForge's removed {@code BlockStateProvider}. The variant layouts and
 * rotations match vanilla's own blockstate files, which the committed generated output
 * already follows.</p>
 */
public abstract class BlockStateProvider implements DataProvider {
    private final PackOutput output;
    private final String modid;
    private final Map<Block, IGeneratedBlockState> registeredBlocks = new LinkedHashMap<>();
    private final BlockModelProvider blockModels;
    private final ItemModelProvider itemModels;

    public BlockStateProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        this.output = output;
        this.modid = modid;
        this.blockModels = new BlockModelProvider(output, modid, existingFileHelper);
        this.itemModels = new ItemModelProvider(output, modid, existingFileHelper);
    }

    protected abstract void registerStatesAndModels();

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        this.registeredBlocks.clear();
        this.blockModels.clear();
        this.itemModels.clear();
        this.registerStatesAndModels();

        List<CompletableFuture<?>> futures = new ArrayList<>();
        futures.add(this.blockModels.generateAll(cache));
        futures.add(this.itemModels.generateAll(cache));
        PackOutput.PathProvider blockstates = this.output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.registeredBlocks.forEach((block, state) -> futures.add(
                DataProvider.saveStable(cache, state.toJson(), blockstates.json(this.key(block)))));
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Override
    public String getName() {
        return "Block States: " + this.modid;
    }

    public VariantBlockStateBuilder getVariantBuilder(Block block) {
        IGeneratedBlockState existing = this.registeredBlocks.get(block);
        if (existing instanceof VariantBlockStateBuilder variants) {
            return variants;
        }
        VariantBlockStateBuilder builder = new VariantBlockStateBuilder(block);
        this.registeredBlocks.put(block, builder);
        return builder;
    }

    public MultiPartBlockStateBuilder getMultipartBuilder(Block block) {
        IGeneratedBlockState existing = this.registeredBlocks.get(block);
        if (existing instanceof MultiPartBlockStateBuilder multipart) {
            return multipart;
        }
        MultiPartBlockStateBuilder builder = new MultiPartBlockStateBuilder(block);
        this.registeredBlocks.put(block, builder);
        return builder;
    }

    public BlockModelProvider models() {
        return this.blockModels;
    }

    public ItemModelProvider itemModels() {
        return this.itemModels;
    }

    public Identifier modLoc(String name) {
        return Identifier.fromNamespaceAndPath(this.modid, name);
    }

    public Identifier mcLoc(String name) {
        return Identifier.parse(name);
    }

    // Private like NeoForge's original, so subclasses (BlockModelHelper, BlockModelBuilders)
    // can declare their own protected key/name without fighting an override.
    private Identifier key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return this.key(block).getPath();
    }

    public Identifier blockTexture(Block block) {
        Identifier key = this.key(block);
        return Identifier.fromNamespaceAndPath(key.getNamespace(), "block/" + key.getPath());
    }

    public ModelFile cubeAll(Block block) {
        return this.models().cubeAll(this.name(block), this.blockTexture(block));
    }

    // ------------------------------------------------------------------ simple

    public void simpleBlock(Block block, ModelFile model) {
        this.getVariantBuilder(block).partialState().setModels(new ConfiguredModel(model));
    }

    public void simpleBlock(Block block, ConfiguredModel... models) {
        this.getVariantBuilder(block).partialState().setModels(models);
    }

    public void simpleBlock(Block block, Function<ModelFile, ConfiguredModel[]> expander) {
        this.simpleBlock(block, expander.apply(this.cubeAll(block)));
    }

    public void simpleBlockItem(Block block, ModelFile model) {
        this.itemModels().withExistingParent(this.name(block), model.getLocation());
    }

    public void simpleBlockWithItem(Block block, ModelFile model) {
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    // ------------------------------------------------------------------- axis

    public void axisBlock(RotatedPillarBlock block) {
        this.axisBlock(block, this.blockTexture(block), this.blockTexture(block).withSuffix("_top"));
    }

    public void logBlock(RotatedPillarBlock block) {
        this.axisBlock(block);
    }

    public void axisBlock(RotatedPillarBlock block, Identifier side, Identifier end) {
        this.axisBlock(block, this.models().cubeColumn(this.name(block), side, end),
                this.models().cubeColumnHorizontal(this.name(block) + "_horizontal", side, end));
    }

    public void axisBlockWithRenderType(RotatedPillarBlock block, Identifier renderType) {
        this.axisBlockWithRenderType(block, this.blockTexture(block), this.blockTexture(block).withSuffix("_top"), renderType);
    }

    public void axisBlockWithRenderType(RotatedPillarBlock block, String renderType) {
        this.axisBlockWithRenderType(block, renderTypeLoc(renderType));
    }

    public void axisBlockWithRenderType(RotatedPillarBlock block, Identifier baseName, String renderType) {
        this.axisBlockWithRenderType(block, baseName, baseName.withSuffix("_top"), renderTypeLoc(renderType));
    }

    public void axisBlockWithRenderType(RotatedPillarBlock block, Identifier side, Identifier end, String renderType) {
        this.axisBlockWithRenderType(block, side, end, renderTypeLoc(renderType));
    }

    public void logBlockWithRenderType(RotatedPillarBlock block, Identifier renderType) {
        this.axisBlockWithRenderType(block, renderType);
    }

    public void logBlockWithRenderType(RotatedPillarBlock block, String renderType) {
        this.axisBlockWithRenderType(block, renderTypeLoc(renderType));
    }

    /**
     * Render types are written as bare names in the JSON ("cutout"), but the vanilla ones all live in
     * {@code minecraft:}. A bare name passed as a {@link Identifier} would resolve against the
     * mod namespace and produce an unknown render type, so normalise here.
     */
    protected static Identifier renderTypeLoc(String renderType) {
        return renderType.indexOf(':') >= 0
                ? renderTypeLoc(renderType)
                : Identifier.withDefaultNamespace(renderType);
    }

    public void axisBlockWithRenderType(RotatedPillarBlock block, Identifier side, Identifier end, Identifier renderType) {
        this.axisBlock(block,
                this.models().cubeColumn(this.name(block), side, end).renderType(renderType),
                this.models().cubeColumnHorizontal(this.name(block) + "_horizontal", side, end).renderType(renderType));
    }

    public void axisBlock(RotatedPillarBlock block, ModelFile vertical, ModelFile horizontal) {
        this.getVariantBuilder(block)
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y)
                .modelForState().modelFile(vertical).addModel()
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Z)
                .modelForState().modelFile(horizontal).rotationX(90).addModel()
                .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.X)
                .modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel();
    }

    // ------------------------------------------------------------ orientation

    public void horizontalBlock(Block block, ModelFile model) {
        this.horizontalBlock(block, model, 0);
    }

    public void horizontalBlock(Block block, ModelFile model, int angleOffset) {
        this.horizontalBlock(block, state -> model, angleOffset);
    }

    public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        this.horizontalBlock(block, modelFunc, 0);
    }

    public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        this.getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(modelFunc.apply(state))
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + angleOffset) % 360)
                .build());
    }

    public void horizontalFaceBlock(Block block, ModelFile model) {
        this.horizontalFaceBlock(block, state -> model, 0);
    }

    public void horizontalFaceBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        this.getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            AttachFace face = state.getValue(BlockStateProperties.ATTACH_FACE);
            return ConfiguredModel.builder()
                    .modelFile(modelFunc.apply(state))
                    .rotationX(face == AttachFace.FLOOR ? 0 : face == AttachFace.WALL ? 90 : 180)
                    .rotationY((((int) facing.toYRot()) + angleOffset) % 360)
                    .uvLock(face == AttachFace.WALL)
                    .build();
        });
    }

    public void directionalBlock(Block block, ModelFile model) {
        this.directionalBlock(block, state -> model, 0);
    }

    public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        this.directionalBlock(block, modelFunc, 0);
    }

    public void directionalBlock(Block block, ModelFile model, int angleOffset) {
        this.directionalBlock(block, state -> model, angleOffset);
    }

    /**
     * Variants for a block that points in one of the six directions. The x rotation already turns
     * a model drawn with its front on the top face towards the north, but the y rotation below is
     * measured from a model drawn facing south, so such a block (a barrel, whose lid is on top)
     * needs {@code angleOffset} 180 to line up with vanilla's variants.
     */
    public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        this.getVariantBuilder(block).forAllStates(state -> {
            Direction direction = state.getValue(BlockStateProperties.FACING);
            return ConfiguredModel.builder()
                    .modelFile(modelFunc.apply(state))
                    .rotationX(direction == Direction.DOWN ? 180 : direction.getAxis().isHorizontal() ? 90 : 0)
                    .rotationY(direction.getAxis().isVertical() ? 0 : (((int) direction.toYRot()) + angleOffset) % 360)
                    .build();
        });
    }

    // ----------------------------------------------------------------- stairs

    public void stairsBlock(StairBlock block, Identifier texture) {
        this.stairsBlock(block, texture, texture, texture);
    }

    public void stairsBlock(StairBlock block, String name, Identifier texture) {
        this.stairsBlock(block, name, texture, texture, texture);
    }

    public void stairsBlock(StairBlock block, Identifier side, Identifier bottom, Identifier top) {
        this.stairsBlock(block, this.name(block), side, bottom, top);
    }

    public void stairsBlock(StairBlock block, String name, Identifier side, Identifier bottom, Identifier top) {
        this.stairsBlock(block,
                this.models().stairs(name, side, bottom, top),
                this.models().stairsInner(name + "_inner", side, bottom, top),
                this.models().stairsOuter(name + "_outer", side, bottom, top));
    }

    public void stairsBlockWithRenderType(StairBlock block, Identifier texture, String renderType) {
        this.stairsBlockWithRenderType(block, texture, renderTypeLoc(renderType));
    }

    public void stairsBlockWithRenderType(StairBlock block, String name, Identifier texture, String renderType) {
        this.stairsBlockWithRenderType(block, name, texture, renderTypeLoc(renderType));
    }

    public void stairsBlockWithRenderType(StairBlock block, Identifier side, Identifier bottom, Identifier top, String renderType) {
        this.stairsBlockWithRenderType(block, side, bottom, top, renderTypeLoc(renderType));
    }

    public void stairsBlockWithRenderType(StairBlock block, String name, Identifier side, Identifier bottom, Identifier top, String renderType) {
        this.stairsBlockWithRenderType(block, name, side, bottom, top, renderTypeLoc(renderType));
    }

    public void stairsBlockWithRenderType(StairBlock block, Identifier texture, Identifier renderType) {
        this.stairsBlockWithRenderType(block, this.name(block), texture, texture, texture, renderType);
    }

    public void stairsBlockWithRenderType(StairBlock block, Identifier side, Identifier bottom, Identifier top, Identifier renderType) {
        this.stairsBlockWithRenderType(block, this.name(block), side, bottom, top, renderType);
    }

    public void stairsBlockWithRenderType(StairBlock block, String name, Identifier texture, Identifier renderType) {
        this.stairsBlockWithRenderType(block, name, texture, texture, texture, renderType);
    }

    public void stairsBlockWithRenderType(StairBlock block, String name, Identifier side, Identifier bottom, Identifier top, Identifier renderType) {
        this.stairsBlock(block,
                this.models().stairs(name, side, bottom, top).renderType(renderType),
                this.models().stairsInner(name + "_inner", side, bottom, top).renderType(renderType),
                this.models().stairsOuter(name + "_outer", side, bottom, top).renderType(renderType));
    }

    public void stairsBlock(StairBlock block, ModelFile stairs, ModelFile stairsInner, ModelFile stairsOuter) {
        this.getVariantBuilder(block).forAllStatesExcept(state -> {
            Direction facing = state.getValue(StairBlock.FACING);
            Half half = state.getValue(StairBlock.HALF);
            StairsShape shape = state.getValue(StairBlock.SHAPE);
            int yRot = (int) facing.getClockWise().toYRot();
            if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                yRot += 270;
            }
            if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
                yRot += 90;
            }
            yRot %= 360;
            boolean uvLock = yRot != 0 || half == Half.TOP;
            return ConfiguredModel.builder()
                    .modelFile(shape == StairsShape.STRAIGHT ? stairs
                            : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter)
                    .rotationX(half == Half.BOTTOM ? 0 : 180)
                    .rotationY(yRot)
                    .uvLock(uvLock)
                    .build();
        }, StairBlock.WATERLOGGED);
    }

    // ------------------------------------------------------------------- slab

    public void slabBlock(SlabBlock block, Identifier doubleslab, Identifier texture) {
        this.slabBlock(block, doubleslab, texture, texture, texture);
    }

    public void slabBlock(SlabBlock block, Identifier doubleslab, Identifier side, Identifier bottom, Identifier top) {
        this.slabBlock(block,
                this.models().slab(this.name(block), side, bottom, top),
                this.models().slabTop(this.name(block) + "_top", side, bottom, top),
                this.models().getExistingFile(doubleslab));
    }

    public void slabBlock(SlabBlock block, ModelFile bottom, ModelFile top, ModelFile doubleslab) {
        this.getVariantBuilder(block)
                .partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new ConfiguredModel(bottom))
                .partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new ConfiguredModel(top))
                .partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new ConfiguredModel(doubleslab));
    }

    // ----------------------------------------------------------------- button

    public void buttonBlock(ButtonBlock block, Identifier texture) {
        this.buttonBlock(block,
                this.models().withExistingParent(this.name(block), "block/button").texture("texture", texture),
                this.models().withExistingParent(this.name(block) + "_pressed", "block/button_pressed").texture("texture", texture));
    }

    public void buttonBlock(ButtonBlock block, ModelFile button, ModelFile buttonPressed) {
        this.getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(ButtonBlock.FACING);
            AttachFace face = state.getValue(ButtonBlock.FACE);
            boolean powered = state.getValue(ButtonBlock.POWERED);
            return ConfiguredModel.builder()
                    .modelFile(powered ? buttonPressed : button)
                    .rotationX(face == AttachFace.FLOOR ? 0 : face == AttachFace.WALL ? 90 : 180)
                    .rotationY((int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot())
                    .uvLock(face == AttachFace.WALL)
                    .build();
        });
    }

    // --------------------------------------------------------- pressure plate

    public void pressurePlateBlock(PressurePlateBlock block, Identifier texture) {
        this.pressurePlateBlock(block,
                this.models().withExistingParent(this.name(block), "block/pressure_plate_up").texture("texture", texture),
                this.models().withExistingParent(this.name(block) + "_down", "block/pressure_plate_down").texture("texture", texture));
    }

    public void pressurePlateBlock(PressurePlateBlock block, ModelFile pressurePlate, ModelFile pressurePlateDown) {
        this.getVariantBuilder(block)
                .partialState().with(PressurePlateBlock.POWERED, false).setModels(new ConfiguredModel(pressurePlate))
                .partialState().with(PressurePlateBlock.POWERED, true).setModels(new ConfiguredModel(pressurePlateDown));
    }

    // ------------------------------------------------------------------- sign

    public void signBlock(StandingSignBlock signBlock, WallSignBlock wallSignBlock, Identifier texture) {
        ModelFile sign = this.models().getBuilder(this.name(signBlock)).texture("particle", texture);
        this.signBlock(signBlock, wallSignBlock, sign);
    }

    public void signBlock(StandingSignBlock signBlock, WallSignBlock wallSignBlock, ModelFile sign) {
        this.simpleBlock(signBlock, sign);
        this.simpleBlock(wallSignBlock, sign);
    }

    public void hangingSignBlock(CeilingHangingSignBlock hangingSignBlock, WallHangingSignBlock wallHangingSignBlock, Identifier texture) {
        ModelFile sign = this.models().getBuilder(this.name(hangingSignBlock)).texture("particle", texture);
        this.hangingSignBlock(hangingSignBlock, wallHangingSignBlock, sign);
    }

    public void hangingSignBlock(CeilingHangingSignBlock hangingSignBlock, WallHangingSignBlock wallHangingSignBlock, ModelFile sign) {
        this.simpleBlock(hangingSignBlock, sign);
        this.simpleBlock(wallHangingSignBlock, sign);
    }

    // ---------------------------------------------------------- fences, walls

    public void fourWayBlock(CrossCollisionBlock block, ModelFile post, ModelFile side) {
        MultiPartBlockStateBuilder builder = this.getMultipartBuilder(block);
        builder.part().modelFile(post).addModel().end();
        this.fourWayMultipart(builder, side);
    }

    public void fourWayMultipart(MultiPartBlockStateBuilder builder, ModelFile side) {
        PipeBlock.PROPERTY_BY_DIRECTION.forEach((direction, property) -> {
            if (direction.getAxis().isHorizontal()) {
                builder.part()
                        .modelFile(side)
                        .rotationY((((int) direction.toYRot()) + 180) % 360)
                        .uvLock(true)
                        .addModel()
                        .condition(property, true);
            }
        });
    }

    public void fenceBlock(FenceBlock block, Identifier texture) {
        this.fenceBlock(block, this.name(block), texture);
    }

    public void fenceBlock(FenceBlock block, String name, Identifier texture) {
        this.fourWayBlock(block,
                this.models().fencePost(name + "_post", texture),
                this.models().fenceSide(name + "_side", texture));
    }

    public void fenceBlockWithRenderType(FenceBlock block, Identifier texture, String renderType) {
        this.fenceBlockWithRenderType(block, texture, renderTypeLoc(renderType));
    }

    public void fenceBlockWithRenderType(FenceBlock block, String name, Identifier texture, String renderType) {
        this.fenceBlockWithRenderType(block, name, texture, renderTypeLoc(renderType));
    }

    public void fenceBlockWithRenderType(FenceBlock block, Identifier texture, Identifier renderType) {
        this.fenceBlockWithRenderType(block, this.name(block), texture, renderType);
    }

    public void fenceBlockWithRenderType(FenceBlock block, String name, Identifier texture, Identifier renderType) {
        this.fourWayBlock(block,
                this.models().fencePost(name + "_post", texture).renderType(renderType),
                this.models().fenceSide(name + "_side", texture).renderType(renderType));
    }

    public void fenceGateBlock(FenceGateBlock block, Identifier texture) {
        this.fenceGateBlock(block, this.name(block), texture);
    }

    public void fenceGateBlock(FenceGateBlock block, String name, Identifier texture) {
        this.fenceGateBlock(block,
                this.models().fenceGate(name, texture),
                this.models().fenceGateOpen(name + "_open", texture),
                this.models().fenceGateWall(name + "_wall", texture),
                this.models().fenceGateWallOpen(name + "_wall_open", texture));
    }

    public void fenceGateBlockWithRenderType(FenceGateBlock block, Identifier texture, String renderType) {
        this.fenceGateBlockWithRenderType(block, this.name(block), texture, renderTypeLoc(renderType));
    }

    public void fenceGateBlockWithRenderType(FenceGateBlock block, String name, Identifier texture, String renderType) {
        this.fenceGateBlockWithRenderType(block, name, texture, renderTypeLoc(renderType));
    }

    public void fenceGateBlockWithRenderType(FenceGateBlock block, Identifier texture, Identifier renderType) {
        this.fenceGateBlockWithRenderType(block, this.name(block), texture, renderType);
    }

    public void fenceGateBlockWithRenderType(FenceGateBlock block, String name, Identifier texture, Identifier renderType) {
        this.fenceGateBlock(block,
                this.models().fenceGate(name, texture).renderType(renderType),
                this.models().fenceGateOpen(name + "_open", texture).renderType(renderType),
                this.models().fenceGateWall(name + "_wall", texture).renderType(renderType),
                this.models().fenceGateWallOpen(name + "_wall_open", texture).renderType(renderType));
    }

    public void fenceGateBlock(FenceGateBlock block, ModelFile gate, ModelFile gateOpen, ModelFile gateWall, ModelFile gateWallOpen) {
        this.getVariantBuilder(block).forAllStatesExcept(state -> {
            ModelFile model = gate;
            if (state.getValue(FenceGateBlock.IN_WALL)) {
                model = gateWall;
            }
            if (state.getValue(FenceGateBlock.OPEN)) {
                model = model == gateWall ? gateWallOpen : gateOpen;
            }
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY((int) state.getValue(FenceGateBlock.FACING).toYRot())
                    .uvLock(true)
                    .build();
        }, FenceGateBlock.POWERED);
    }

    public void wallBlock(WallBlock block, Identifier texture) {
        this.wallBlock(block, this.name(block), texture);
    }

    public void wallBlock(WallBlock block, String name, Identifier texture) {
        this.wallBlock(block,
                this.models().withExistingParent(name + "_post", "block/template_wall_post").texture("wall", texture),
                this.models().withExistingParent(name + "_side", "block/template_wall_side").texture("wall", texture),
                this.models().withExistingParent(name + "_side_tall", "block/template_wall_side_tall").texture("wall", texture));
    }

    public void wallBlockWithRenderType(WallBlock block, Identifier texture, String renderType) {
        this.wallBlockWithRenderType(block, this.name(block), texture, renderTypeLoc(renderType));
    }

    public void wallBlockWithRenderType(WallBlock block, String name, Identifier texture, String renderType) {
        this.wallBlockWithRenderType(block, name, texture, renderTypeLoc(renderType));
    }

    public void wallBlockWithRenderType(WallBlock block, Identifier texture, Identifier renderType) {
        this.wallBlockWithRenderType(block, this.name(block), texture, renderType);
    }

    public void wallBlockWithRenderType(WallBlock block, String name, Identifier texture, Identifier renderType) {
        this.wallBlock(block,
                this.models().withExistingParent(name + "_post", "block/template_wall_post").texture("wall", texture).renderType(renderType),
                this.models().withExistingParent(name + "_side", "block/template_wall_side").texture("wall", texture).renderType(renderType),
                this.models().withExistingParent(name + "_side_tall", "block/template_wall_side_tall").texture("wall", texture).renderType(renderType));
    }

    public void wallBlock(WallBlock block, ModelFile post, ModelFile side, ModelFile sideTall) {
        MultiPartBlockStateBuilder builder = this.getMultipartBuilder(block);
        builder.part().modelFile(post).addModel().condition(WallBlock.UP, true).end();
        this.wallSidePart(builder, side, WallSide.LOW);
        this.wallSidePart(builder, sideTall, WallSide.TALL);
    }

    private static final List<Map.Entry<Direction, net.minecraft.world.level.block.state.properties.EnumProperty<WallSide>>> WALL_SIDES = List.of(
            Map.entry(Direction.NORTH, WallBlock.NORTH_WALL),
            Map.entry(Direction.EAST, WallBlock.EAST_WALL),
            Map.entry(Direction.SOUTH, WallBlock.SOUTH_WALL),
            Map.entry(Direction.WEST, WallBlock.WEST_WALL));

    private void wallSidePart(MultiPartBlockStateBuilder builder, ModelFile model, WallSide height) {
        for (Map.Entry<Direction, net.minecraft.world.level.block.state.properties.EnumProperty<WallSide>> wallSide : WALL_SIDES) {
            Direction direction = wallSide.getKey();
            if (direction.getAxis().isHorizontal()) {
                builder.part()
                        .modelFile(model)
                        .rotationY((((int) direction.toYRot()) + 180) % 360)
                        .uvLock(true)
                        .addModel()
                        .condition(wallSide.getValue(), height);
            }
        }
    }

    // ------------------------------------------------------------------ panes

    public void paneBlock(IronBarsBlock block, Identifier pane, Identifier edge) {
        this.paneBlock(block, this.name(block), pane, edge);
    }

    public void paneBlock(IronBarsBlock block, String name, Identifier pane, Identifier edge) {
        this.paneBlock(block,
                this.models().withExistingParent(name + "_post", "block/template_glass_pane_post")
                        .texture("pane", pane).texture("edge", edge),
                this.models().withExistingParent(name + "_side", "block/template_glass_pane_side")
                        .texture("pane", pane).texture("edge", edge),
                this.models().withExistingParent(name + "_side_alt", "block/template_glass_pane_side_alt")
                        .texture("pane", pane).texture("edge", edge),
                this.models().withExistingParent(name + "_noside", "block/template_glass_pane_noside")
                        .texture("pane", pane),
                this.models().withExistingParent(name + "_noside_alt", "block/template_glass_pane_noside_alt")
                        .texture("pane", pane));
    }

    public void paneBlockWithRenderType(IronBarsBlock block, Identifier pane, Identifier edge, String renderType) {
        this.paneBlockWithRenderType(block, this.name(block), pane, edge, renderTypeLoc(renderType));
    }

    public void paneBlockWithRenderType(IronBarsBlock block, Identifier pane, Identifier edge, Identifier renderType) {
        this.paneBlockWithRenderType(block, this.name(block), pane, edge, renderType);
    }

    public void paneBlockWithRenderType(IronBarsBlock block, String name, Identifier pane, Identifier edge, String renderType) {
        this.paneBlockWithRenderType(block, name, pane, edge, renderTypeLoc(renderType));
    }

    public void paneBlockWithRenderType(IronBarsBlock block, String name, Identifier pane, Identifier edge, Identifier renderType) {
        this.paneBlock(block,
                this.models().withExistingParent(name + "_post", "block/template_glass_pane_post")
                        .texture("pane", pane).texture("edge", edge).renderType(renderType),
                this.models().withExistingParent(name + "_side", "block/template_glass_pane_side")
                        .texture("pane", pane).texture("edge", edge).renderType(renderType),
                this.models().withExistingParent(name + "_side_alt", "block/template_glass_pane_side_alt")
                        .texture("pane", pane).texture("edge", edge).renderType(renderType),
                this.models().withExistingParent(name + "_noside", "block/template_glass_pane_noside")
                        .texture("pane", pane).renderType(renderType),
                this.models().withExistingParent(name + "_noside_alt", "block/template_glass_pane_noside_alt")
                        .texture("pane", pane).renderType(renderType));
    }

    public void paneBlock(IronBarsBlock block, ModelFile post, ModelFile side, ModelFile sideAlt, ModelFile noSide, ModelFile noSideAlt) {
        MultiPartBlockStateBuilder builder = this.getMultipartBuilder(block);
        builder.part().modelFile(post).addModel().end();
        PipeBlock.PROPERTY_BY_DIRECTION.forEach((direction, property) -> {
            if (direction.getAxis().isHorizontal()) {
                boolean alt = direction == Direction.SOUTH;
                builder.part()
                        .modelFile(alt || direction == Direction.WEST ? sideAlt : side)
                        .rotationY(direction.getAxis() == Direction.Axis.X ? 90 : 0)
                        .addModel()
                        .condition(property, true)
                        .end()
                        .part()
                        .modelFile(alt || direction == Direction.EAST ? noSideAlt : noSide)
                        .rotationY(direction == Direction.WEST ? 270 : direction == Direction.SOUTH ? 90 : 0)
                        .addModel()
                        .condition(property, false);
            }
        });
    }

    // ------------------------------------------------------------------ doors

    public void doorBlock(DoorBlock block, Identifier bottom, Identifier top) {
        this.doorBlock(block, this.name(block), bottom, top);
    }

    public void doorBlock(DoorBlock block, String name, Identifier bottom, Identifier top) {
        this.doorBlock(block,
                this.models().withExistingParent(name + "_bottom_left", "block/door_bottom_left")
                        .texture("top", top).texture("bottom", bottom),
                this.models().withExistingParent(name + "_bottom_left_open", "block/door_bottom_left_open")
                        .texture("top", top).texture("bottom", bottom),
                this.models().withExistingParent(name + "_bottom_right", "block/door_bottom_right")
                        .texture("top", top).texture("bottom", bottom),
                this.models().withExistingParent(name + "_bottom_right_open", "block/door_bottom_right_open")
                        .texture("top", top).texture("bottom", bottom),
                this.models().withExistingParent(name + "_top_left", "block/door_top_left")
                        .texture("top", top).texture("bottom", bottom),
                this.models().withExistingParent(name + "_top_left_open", "block/door_top_left_open")
                        .texture("top", top).texture("bottom", bottom),
                this.models().withExistingParent(name + "_top_right", "block/door_top_right")
                        .texture("top", top).texture("bottom", bottom),
                this.models().withExistingParent(name + "_top_right_open", "block/door_top_right_open")
                        .texture("top", top).texture("bottom", bottom));
    }

    public void doorBlockWithRenderType(DoorBlock block, Identifier bottom, Identifier top, String renderType) {
        this.doorBlockWithRenderType(block, this.name(block), bottom, top, renderTypeLoc(renderType));
    }

    public void doorBlockWithRenderType(DoorBlock block, Identifier bottom, Identifier top, Identifier renderType) {
        this.doorBlockWithRenderType(block, this.name(block), bottom, top, renderType);
    }

    public void doorBlockWithRenderType(DoorBlock block, String name, Identifier bottom, Identifier top, String renderType) {
        this.doorBlockWithRenderType(block, name, bottom, top, renderTypeLoc(renderType));
    }

    public void doorBlockWithRenderType(DoorBlock block, String name, Identifier bottom, Identifier top, Identifier renderType) {
        this.doorBlock(block,
                this.models().withExistingParent(name + "_bottom_left", "block/door_bottom_left")
                        .texture("top", top).texture("bottom", bottom).renderType(renderType),
                this.models().withExistingParent(name + "_bottom_left_open", "block/door_bottom_left_open")
                        .texture("top", top).texture("bottom", bottom).renderType(renderType),
                this.models().withExistingParent(name + "_bottom_right", "block/door_bottom_right")
                        .texture("top", top).texture("bottom", bottom).renderType(renderType),
                this.models().withExistingParent(name + "_bottom_right_open", "block/door_bottom_right_open")
                        .texture("top", top).texture("bottom", bottom).renderType(renderType),
                this.models().withExistingParent(name + "_top_left", "block/door_top_left")
                        .texture("top", top).texture("bottom", bottom).renderType(renderType),
                this.models().withExistingParent(name + "_top_left_open", "block/door_top_left_open")
                        .texture("top", top).texture("bottom", bottom).renderType(renderType),
                this.models().withExistingParent(name + "_top_right", "block/door_top_right")
                        .texture("top", top).texture("bottom", bottom).renderType(renderType),
                this.models().withExistingParent(name + "_top_right_open", "block/door_top_right_open")
                        .texture("top", top).texture("bottom", bottom).renderType(renderType));
    }

    public void doorBlock(DoorBlock block, ModelFile bottomLeft, ModelFile bottomLeftOpen, ModelFile bottomRight,
                          ModelFile bottomRightOpen, ModelFile topLeft, ModelFile topLeftOpen,
                          ModelFile topRight, ModelFile topRightOpen) {
        this.getVariantBuilder(block).forAllStatesExcept(state -> {
            int yRot = ((int) state.getValue(DoorBlock.FACING).toYRot()) + 90;
            boolean right = state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
            boolean open = state.getValue(DoorBlock.OPEN);
            boolean lower = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
            if (open) {
                yRot += 90;
            }
            if (right && open) {
                yRot += 180;
            }
            yRot %= 360;

            ModelFile model;
            if (lower) {
                model = right ? (open ? bottomRightOpen : bottomRight) : (open ? bottomLeftOpen : bottomLeft);
            } else {
                model = right ? (open ? topRightOpen : topRight) : (open ? topLeftOpen : topLeft);
            }

            return ConfiguredModel.builder().modelFile(model).rotationY(yRot).build();
        }, DoorBlock.POWERED);
    }

    // -------------------------------------------------------------- trapdoors

    public void trapdoorBlock(TrapDoorBlock block, Identifier texture, boolean orientable) {
        this.trapdoorBlock(block, this.name(block), texture, orientable);
    }

    public void trapdoorBlock(TrapDoorBlock block, String name, Identifier texture, boolean orientable) {
        String base = orientable ? "block/template_orientable_trapdoor" : "block/template_trapdoor";
        this.trapdoorBlock(block,
                this.models().withExistingParent(name + "_bottom", base + "_bottom").texture("texture", texture),
                this.models().withExistingParent(name + "_top", base + "_top").texture("texture", texture),
                this.models().withExistingParent(name + "_open", base + "_open").texture("texture", texture),
                orientable);
    }

    public void trapdoorBlockWithRenderType(TrapDoorBlock block, Identifier texture, boolean orientable, String renderType) {
        this.trapdoorBlockWithRenderType(block, texture, orientable, renderTypeLoc(renderType));
    }

    public void trapdoorBlockWithRenderType(TrapDoorBlock block, Identifier texture, boolean orientable, Identifier renderType) {
        this.trapdoorBlockWithRenderType(block, this.name(block), texture, orientable, renderType);
    }

    public void trapdoorBlockWithRenderType(TrapDoorBlock block, String name, Identifier texture, boolean orientable, String renderType) {
        this.trapdoorBlockWithRenderType(block, name, texture, orientable, renderTypeLoc(renderType));
    }

    public void trapdoorBlockWithRenderType(TrapDoorBlock block, String name, Identifier texture, boolean orientable, Identifier renderType) {
        String base = orientable ? "block/template_orientable_trapdoor" : "block/template_trapdoor";
        this.trapdoorBlock(block,
                this.models().withExistingParent(name + "_bottom", base + "_bottom").texture("texture", texture).renderType(renderType),
                this.models().withExistingParent(name + "_top", base + "_top").texture("texture", texture).renderType(renderType),
                this.models().withExistingParent(name + "_open", base + "_open").texture("texture", texture).renderType(renderType),
                orientable);
    }

    public void trapdoorBlock(TrapDoorBlock block, ModelFile bottom, ModelFile top, ModelFile open, boolean orientable) {
        this.getVariantBuilder(block).forAllStatesExcept(state -> {
            int xRot = 0;
            int yRot = ((int) state.getValue(TrapDoorBlock.FACING).toYRot()) + 180;
            boolean isOpen = state.getValue(TrapDoorBlock.OPEN);
            if (orientable && isOpen && state.getValue(TrapDoorBlock.HALF) == Half.TOP) {
                xRot += 180;
                yRot += 180;
            }
            if (!orientable && !isOpen) {
                yRot = 0;
            }
            yRot %= 360;
            return ConfiguredModel.builder()
                    .modelFile(isOpen ? open : state.getValue(TrapDoorBlock.HALF) == Half.TOP ? top : bottom)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        }, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);
    }
}
