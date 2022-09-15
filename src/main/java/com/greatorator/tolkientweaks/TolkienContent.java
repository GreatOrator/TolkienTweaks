package com.greatorator.tolkientweaks;

import codechicken.lib.gui.SimpleItemGroup;
import com.brandon3055.brandonscore.blocks.ItemBlockBCore;
import com.greatorator.tolkientweaks.block.ChameleonBlock;
import com.greatorator.tolkientweaks.container.BronzeKeyAccessContainer;
import com.greatorator.tolkientweaks.container.KeyRingContainer;
import com.greatorator.tolkientweaks.item.KeyItem;
import com.greatorator.tolkientweaks.item.KeyRingItem;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
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

    public static ItemGroup itemsGroup = new SimpleItemGroup("tolkientweaks.items", () -> new ItemStack(TolkienContent.KEY_RING.get()));
    public static ItemGroup blocksGroup = new SimpleItemGroup("tolkientweaks.blocks", () -> new ItemStack(TolkienContent.CHAMELEON_BLOCK.get()));

    public static void init() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        LOGGER.info("Creating the light of the Valar in the land of Arda...");
        LOGGER.info("Asking the Ainur to sing the music of Eru Iluvatar...");
        LOGGER.info("Preparing the Dwarves...");
        BLOCKS.register(modBus);
        LOGGER.info("Stocking the markets...");
        ITEMS.register(modBus);
        TILE.register(modBus);
        CONTAINER.register(modBus);
        RECIPE_SERIALIZER.register(modBus);
        LOGGER.info("Populating the peoples of Middle-earth...");
        LOGGER.info("Setting the task master to work...");
        LOGGER.info("Time to create the land...");
        modBus.addGenericListener(ContainerType.class, TolkienContent::registerContainers);
    }

    //#################################################################
    // Blocks
    //#################################################################
    public static RegistryObject<Block> CHAMELEON_BLOCK = BLOCKS.register("chameleon_block", () -> new ChameleonBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission()));

    //#################################################################
    // Items
    //#################################################################
    // Blocks as Items
    public static RegistryObject<Item> CHAMELEON_BLOCK_ITEM = ITEMS.register("chameleon_block", () -> new ItemBlockBCore(CHAMELEON_BLOCK.get(), new Item.Properties().tab(blocksGroup)));

    // Items
    public static RegistryObject<Item> KEY_RING = ITEMS.register("key_ring", () -> new KeyRingItem(new Item.Properties().stacksTo(1).tab(itemsGroup)).setItemHasUse().setHasLore());
    public static RegistryObject<Item> BRONZE_KEY = ITEMS.register("bronze_key", () -> new KeyItem(new Item.Properties().stacksTo(1).tab(itemsGroup)));
    public static RegistryObject<Item> SILVER_KEY = ITEMS.register("silver_key", () -> new KeyItem(new Item.Properties().stacksTo(1).tab(itemsGroup)));
    public static RegistryObject<Item> GOLD_KEY = ITEMS.register("gold_key", () -> new KeyItem(new Item.Properties().stacksTo(1).tab(itemsGroup)));
    public static RegistryObject<Item> MITHRIL_KEY = ITEMS.register("mithril_key", () -> new KeyItem(new Item.Properties().stacksTo(1).tab(itemsGroup)));
    public static RegistryObject<Item> MASTER_KEY = ITEMS.register("master_key", () -> new KeyItem(new Item.Properties().stacksTo(1).tab(itemsGroup)));

    //#################################################################
    // Tile Entity Types
    //#################################################################

    //#################################################################
    // Containers
    //#################################################################
    public static ContainerType<KeyRingContainer> KEY_RING_CONTAINER;
    public static ContainerType<BronzeKeyAccessContainer> BRONZE_KEY_CONTAINER;
//    public static ContainerType<BronzeKeyAccessContainer> SILVER_KEY_CONTAINER;
//    public static ContainerType<BronzeKeyAccessContainer> GOLD_KEY_CONTAINER;
//    public static ContainerType<BronzeKeyAccessContainer> MITHRIL_KEY_CONTAINER;
//    public static ContainerType<BronzeKeyAccessContainer> MASTER_KEY_CONTAINER;

    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(KEY_RING_CONTAINER = (ContainerType<KeyRingContainer>) IForgeContainerType.create(KeyRingContainer::new).setRegistryName("key_ring_container"));
        event.getRegistry().register(BRONZE_KEY_CONTAINER = (ContainerType<BronzeKeyAccessContainer>) IForgeContainerType.create(BronzeKeyAccessContainer::new).setRegistryName("bronze_key_container"));
//        event.getRegistry().register(SILVER_KEY_CONTAINER = (ContainerType<BronzeKeyAccessContainer>) IForgeContainerType.create(BronzeKeyAccessContainer::new).setRegistryName("silver_key_container"));
//        event.getRegistry().register(GOLD_KEY_CONTAINER = (ContainerType<BronzeKeyAccessContainer>) IForgeContainerType.create(BronzeKeyAccessContainer::new).setRegistryName("gold_key_container"));
//        event.getRegistry().register(MITHRIL_KEY_CONTAINER = (ContainerType<BronzeKeyAccessContainer>) IForgeContainerType.create(BronzeKeyAccessContainer::new).setRegistryName("mithril_key_container"));
//        event.getRegistry().register(MASTER_KEY_CONTAINER = (ContainerType<BronzeKeyAccessContainer>) IForgeContainerType.create(BronzeKeyAccessContainer::new).setRegistryName("master_key_container"));
    }

    //#################################################################
    // Recipe Serializers
    //#################################################################

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