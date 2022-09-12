package com.greatorator.tolkientweaks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.ToIntFunction;

import static com.greatorator.tolkientweaks.TolkienTweaks.LOGGER;
import static com.greatorator.tolkientweaks.TolkienTweaks.MODID;

/**
 * Created by brandon3055 on 31/1/21
 */
public class TolkienContent {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<TileEntityType<?>> TILE = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINER = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

//    public static ItemGroup toolsGroup = new SimpleItemGroup("tolkienmobs.tools", () -> new ItemStack(TTMContent.PICKAXE_MITHRIL.get()));
//    public static ItemGroup matsGroup = new SimpleItemGroup("tolkienmobs.mats", () -> new ItemStack(TTMContent.INGOT_MITHRIL.get()));
//    public static ItemGroup decoGroup = new SimpleItemGroup("tolkienmobs.deco", () -> new ItemStack(TTMContent.PIGGYBANK_ITEM.get()));
//    public static ItemGroup spawnGroup = new SimpleItemGroup("tolkienmobs.spawn", () -> new ItemStack(TTMContent.GOLEM_STONE_SUMMON.get()));
//    public static ItemGroup foodGroup = new SimpleItemGroup("tolkienmobs.food", () -> new ItemStack(TTMContent.LEMBAS.get()));
//    public static ItemGroup questGroup = new SimpleItemGroup("tolkienmobs.quest", () -> new ItemStack(TTMContent.ITEM_FORTRESSMAP.get()));

    public static void init() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        LOGGER.info("Creating the light of the Valar in the land of Arda...");
//        EnchantmentGenerator.ENCHANTS.register(modBus);
        LOGGER.info("Asking the Ainur to sing the music of Eru Iluvatar...");
//        SoundGenerator.SOUND_EVENTS.register(modBus);
        LOGGER.info("Preparing the Dwarves...");
        BLOCKS.register(modBus);
        LOGGER.info("Stocking the markets...");
        ITEMS.register(modBus);
        TILE.register(modBus);
        CONTAINER.register(modBus);
        RECIPE_SERIALIZER.register(modBus);
        LOGGER.info("Populating the peoples of Middle-earth...");
//        EntityGenerator.ENTITY.register(modBus);
//        EntityGenerator.SPAWN_EGGS.register(modBus);
        LOGGER.info("Setting the task master to work...");
        LOGGER.info("Time to create the land...");
        modBus.addGenericListener(ContainerType.class, TolkienContent::registerContainers);
    }

    //#################################################################
    // Blocks
    //#################################################################
    // Metals & Gems
//    public static RegistryObject<Block> ORE_MITHRIL = BLOCKS.register("ore_mithril", () -> new Block(AbstractBlock.Properties.of(Material.METAL)));

    //#################################################################
    // Items
    //#################################################################
    // Blocks - Metals & Gems
//    public static RegistryObject<Item> ORE_MITHRIL_ITEM = ITEMS.register("ore_mithril", () -> new ItemBlockBCore(ORE_MITHRIL.get(), new Item.Properties().tab(matsGroup)));

    //#################################################################
    // Tile Entity Types
    //#################################################################
    // Custom
//    public static RegistryObject<TileEntityType<FireplaceTile>> TMFIREPLACE_TILE = TILE.register("tmfireplace_tile", () -> TileEntityType.Builder.of(FireplaceTile::new, TTMFIREPLACE.get()).build(null));

    //#################################################################
    // Containers
    //#################################################################
//    public static ContainerType<ContainerBCTile<FireplaceTile>> TMFIREPLACE_CONTAINER;

    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
//        event.getRegistry().register(TMFIREPLACE_CONTAINER = (ContainerType<ContainerBCTile<FireplaceTile>>) IForgeContainerType.create((id, playerInv, extraData) -> new ContainerBCTile<>(TMFIREPLACE_CONTAINER, id, playerInv, extraData, FireplaceTile.SLOT_LAYOUT)).setRegistryName("tmfireplace_container"));
    }

    //#################################################################
    // Recipe Serializers
    //#################################################################
//    public static final RegistryObject<FireplaceRecipe.Serializer> TMFIREPLACE_SERIALIZER = RECIPE_SERIALIZER.register("tmfireplace", FireplaceRecipe.Serializer::new);

    private static boolean needsPostProcessing(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    private static boolean isntSolid(BlockState state, IBlockReader reader, BlockPos pos) {
        return false;
    }

    private static Boolean allowsSpawnOnLeaves(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entity) {
        return entity == EntityType.OCELOT || entity == EntityType.PARROT;
    }

    private static ToIntFunction<BlockState> litBlockEmission(int level) {
        return (p_235421_1_) -> {
            return p_235421_1_.getValue(BlockStateProperties.LIT) ? level : 0;
        };
    }

    private static boolean never(BlockState blockState, IBlockReader iBlockReader, BlockPos blockPos, EntityType<?> entityType) {
        return false;
    }

    private static LeavesBlock createLeavesBlock() {
        return new LeavesBlock(AbstractBlock.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(TolkienContent::allowsSpawnOnLeaves).isSuffocating(TolkienContent::isntSolid).isViewBlocking(TolkienContent::isntSolid));
    }

    private static RotatedPillarBlock createLogBlock(MaterialColor topColor, MaterialColor barkColor) {
        return new RotatedPillarBlock(AbstractBlock.Properties.of(Material.WOOD, (state) -> {
            return state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topColor : barkColor;
        }).strength(2.0F).sound(SoundType.WOOD));
    }
}