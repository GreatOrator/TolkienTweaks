package com.greatorator.tolkienmobs.datagen.loot;

import com.greatorator.tolkienmobs.block.custom.BramblesBushBlock;
import com.greatorator.tolkienmobs.block.custom.PipeweedCropBlock;
import com.greatorator.tolkienmobs.block.custom.SleepingBagBlock;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;
import java.util.function.Supplier;

public class TolkienBlockLootTableProvider extends BlockLootSubProvider {
    public TolkienBlockLootTableProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }
    @Override
    protected void generate() {
        /* Metals & Gems */
            // Mithril
        dropSelf(TolkienBlocks.BLOCK_MITHRIL.get());
        dropSelf(TolkienBlocks.RAW_MITHRIL_BLOCK.get());
        this.add(TolkienBlocks.ORE_MITHRIL.get(),
                block -> createMultipleOreDrops(TolkienBlocks.ORE_MITHRIL.get(), TolkienItems.RAW_MITHRIL.get(),1, 3));
        this.add(TolkienBlocks.ORE_END_MITHRIL.get(),
                block -> createMultipleOreDrops(TolkienBlocks.ORE_END_MITHRIL.get(), TolkienItems.RAW_MITHRIL.get(),2, 5));
        this.add(TolkienBlocks.ORE_NETHER_MITHRIL.get(),
                block -> createMultipleOreDrops(TolkienBlocks.ORE_NETHER_MITHRIL.get(), TolkienItems.RAW_MITHRIL.get(),4, 7));
        this.add(TolkienBlocks.ORE_DEEPSLATE_MITHRIL.get(),
                block -> createMultipleOreDrops(TolkienBlocks.ORE_DEEPSLATE_MITHRIL.get(), TolkienItems.RAW_MITHRIL.get(),3, 9));
        dropSelf(TolkienBlocks.STAIRS_MITHRIL.get());
        this.add(TolkienBlocks.SLAB_MITHRIL.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_MITHRIL.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_MITHRIL.get());
        dropSelf(TolkienBlocks.MITHRIL_BUTTON.get());
        dropSelf(TolkienBlocks.MITHRIL_BARS.get());
        dropSelf(TolkienBlocks.WALL_MITHRIL.get());
        dropSelf(TolkienBlocks.TRAPDOOR_MITHRIL.get());
        this.add(TolkienBlocks.DOOR_MITHRIL.get(),
                block -> createDoorTable(TolkienBlocks.DOOR_MITHRIL.get()));
        dropSelf(TolkienBlocks.BARREL_MITHRIL.get());

            // Morguliron
        dropSelf(TolkienBlocks.BLOCK_MORGULIRON.get());
        dropSelf(TolkienBlocks.RAW_MORGULIRON_BLOCK.get());
        this.add(TolkienBlocks.ORE_MORGULIRON.get(),
                block -> createMultipleOreDrops(TolkienBlocks.ORE_MORGULIRON.get(), TolkienItems.RAW_MORGULIRON.get(),1, 3));
        this.add(TolkienBlocks.ORE_END_MORGULIRON.get(),
                block -> createMultipleOreDrops(TolkienBlocks.ORE_END_MORGULIRON.get(), TolkienItems.RAW_MORGULIRON.get(),2, 5));
        this.add(TolkienBlocks.ORE_NETHER_MORGULIRON.get(),
                block -> createMultipleOreDrops(TolkienBlocks.ORE_NETHER_MORGULIRON.get(), TolkienItems.RAW_MORGULIRON.get(),4, 7));
        this.add(TolkienBlocks.ORE_DEEPSLATE_MORGULIRON.get(),
                block -> createMultipleOreDrops(TolkienBlocks.ORE_DEEPSLATE_MORGULIRON.get(), TolkienItems.RAW_MORGULIRON.get(),3, 9));
        dropSelf(TolkienBlocks.STAIRS_MORGULIRON.get());
        this.add(TolkienBlocks.SLAB_MORGULIRON.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_MORGULIRON.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_MORGULIRON.get());
        dropSelf(TolkienBlocks.MORGULIRON_BUTTON.get());
        dropSelf(TolkienBlocks.MORGULIRON_BARS.get());
        dropSelf(TolkienBlocks.WALL_MORGULIRON.get());
        dropSelf(TolkienBlocks.TRAPDOOR_MORGULIRON.get());
        this.add(TolkienBlocks.DOOR_MORGULIRON.get(),
                block -> createDoorTable(TolkienBlocks.DOOR_MORGULIRON.get()));
        dropSelf(TolkienBlocks.BARREL_MORGULIRON.get());

            // Ammolite
        dropSelf(TolkienBlocks.BLOCK_AMMOLITE.get());
        dropSelf(TolkienBlocks.PANE_AMMOLITE.get());
        this.add(TolkienBlocks.ORE_AMMOLITE.get(),
                block -> createMultipleOreDrops(TolkienBlocks.ORE_AMMOLITE.get(), TolkienItems.GEM_AMMOLITE.get(),1, 2));
        this.add(TolkienBlocks.ORE_END_AMMOLITE.get(),
                block -> createMultipleOreDrops(TolkienBlocks.ORE_END_AMMOLITE.get(), TolkienItems.GEM_AMMOLITE.get(),2, 3));
        this.add(TolkienBlocks.ORE_NETHER_AMMOLITE.get(),
                block -> createMultipleOreDrops(TolkienBlocks.ORE_NETHER_AMMOLITE.get(), TolkienItems.GEM_AMMOLITE.get(),3, 4));
        this.add(TolkienBlocks.ORE_DEEPSLATE_AMMOLITE.get(),
                block -> createMultipleOreDrops(TolkienBlocks.ORE_DEEPSLATE_AMMOLITE.get(), TolkienItems.GEM_AMMOLITE.get(),1, 3));
        this.add(TolkienBlocks.DOOR_DURIN.get(),
                block -> createDoorTable(TolkienBlocks.DOOR_DURIN.get()));

        /* Wood & Foliage */
            // Mallorn
        dropSelf(TolkienBlocks.LOG_MALLORN.get());
        dropSelf(TolkienBlocks.WOOD_MALLORN.get());
        dropSelf(TolkienBlocks.STRIPPED_MALLORN_LOG.get());
        dropSelf(TolkienBlocks.STRIPPED_MALLORN_WOOD.get());
        dropSelf(TolkienBlocks.PLANKS_MALLORN.get());
        dropLeaves(TolkienBlocks.LEAVES_MALLORN);
        dropSelf(TolkienBlocks.LEAFPILE_MALLORN.get());
        dropSelf(TolkienBlocks.STAIRS_MALLORN.get());
        this.add(TolkienBlocks.SLAB_MALLORN.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_MALLORN.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_MALLORN.get());
        dropSelf(TolkienBlocks.MALLORN_BUTTON.get());
        dropSelf(TolkienBlocks.FENCE_MALLORN.get());
        dropSelf(TolkienBlocks.FENCE_GATE_MALLORN.get());
        dropSelf(TolkienBlocks.TRAPDOOR_MALLORN.get());
        this.add(TolkienBlocks.DOOR_MALLORN.get(),
                block -> createDoorTable(TolkienBlocks.DOOR_MALLORN.get()));
        dropSelf(TolkienBlocks.TORCH_MALLORN.get());
        dropOther(TolkienBlocks.WALL_TORCH_MALLORN, TolkienBlocks.TORCH_MALLORN.get());
        dropSelf(TolkienBlocks.BARREL_MALLORN.get());
        dropSelf(TolkienBlocks.MALLORN_SIGN.get());
        dropSelf(TolkienBlocks.MALLORN_HANGING_SIGN.get());
        add(TolkienBlocks.MALLORN_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.MALLORN_SIGN));
        add(TolkienBlocks.MALLORN_HANGING_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.MALLORN_HANGING_SIGN));

        dropSelf(TolkienBlocks.LADDER_MALLORN.get());
        dropSelf(TolkienBlocks.LADDER_MIRKWOOD.get());
        dropSelf(TolkienBlocks.LADDER_CULUMALDA.get());
        dropSelf(TolkienBlocks.LADDER_LEBETHRON.get());
        dropSelf(TolkienBlocks.LADDER_FANGORNOAK.get());
        dropSelf(TolkienBlocks.LADDER_DEADWOOD.get());
        dropSelf(TolkienBlocks.LADDER_DWARVEN_MAPLE.get());

            // Mirkwood
        dropSelf(TolkienBlocks.LOG_MIRKWOOD.get());
        dropSelf(TolkienBlocks.WOOD_MIRKWOOD.get());
        dropSelf(TolkienBlocks.STRIPPED_MIRKWOOD_LOG.get());
        dropSelf(TolkienBlocks.STRIPPED_MIRKWOOD_WOOD.get());
        dropSelf(TolkienBlocks.PLANKS_MIRKWOOD.get());
        dropLeaves(TolkienBlocks.LEAVES_MIRKWOOD);
        dropSelf(TolkienBlocks.LEAFPILE_MIRKWOOD.get());
        dropSelf(TolkienBlocks.STAIRS_MIRKWOOD.get());
        this.add(TolkienBlocks.SLAB_MIRKWOOD.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_MIRKWOOD.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_MIRKWOOD.get());
        dropSelf(TolkienBlocks.MIRKWOOD_BUTTON.get());
        dropSelf(TolkienBlocks.FENCE_MIRKWOOD.get());
        dropSelf(TolkienBlocks.FENCE_GATE_MIRKWOOD.get());
        dropSelf(TolkienBlocks.TRAPDOOR_MIRKWOOD.get());
        this.add(TolkienBlocks.DOOR_MIRKWOOD.get(),
                block -> createDoorTable(TolkienBlocks.DOOR_MIRKWOOD.get()));
        dropSelf(TolkienBlocks.TORCH_MIRKWOOD.get());
        dropOther(TolkienBlocks.WALL_TORCH_MIRKWOOD, TolkienBlocks.TORCH_MIRKWOOD.get());
        dropSelf(TolkienBlocks.BARREL_MIRKWOOD.get());
        dropSelf(TolkienBlocks.MIRKWOOD_SIGN.get());
        dropSelf(TolkienBlocks.MIRKWOOD_HANGING_SIGN.get());
        add(TolkienBlocks.MIRKWOOD_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.MIRKWOOD_SIGN));
        add(TolkienBlocks.MIRKWOOD_HANGING_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.MIRKWOOD_HANGING_SIGN));

            // Culumalda
        dropSelf(TolkienBlocks.LOG_CULUMALDA.get());
        dropSelf(TolkienBlocks.WOOD_CULUMALDA.get());
        dropSelf(TolkienBlocks.STRIPPED_CULUMALDA_LOG.get());
        dropSelf(TolkienBlocks.STRIPPED_CULUMALDA_WOOD.get());
        dropSelf(TolkienBlocks.PLANKS_CULUMALDA.get());
        dropLeaves(TolkienBlocks.LEAVES_CULUMALDA);
        dropSelf(TolkienBlocks.LEAFPILE_CULUMALDA.get());
        dropSelf(TolkienBlocks.STAIRS_CULUMALDA.get());
        this.add(TolkienBlocks.SLAB_CULUMALDA.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_CULUMALDA.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_CULUMALDA.get());
        dropSelf(TolkienBlocks.CULUMALDA_BUTTON.get());
        dropSelf(TolkienBlocks.FENCE_CULUMALDA.get());
        dropSelf(TolkienBlocks.FENCE_GATE_CULUMALDA.get());
        dropSelf(TolkienBlocks.TRAPDOOR_CULUMALDA.get());
        this.add(TolkienBlocks.DOOR_CULUMALDA.get(),
                block -> createDoorTable(TolkienBlocks.DOOR_CULUMALDA.get()));
        dropSelf(TolkienBlocks.TORCH_CULUMALDA.get());
        dropOther(TolkienBlocks.WALL_TORCH_CULUMALDA, TolkienBlocks.TORCH_CULUMALDA.get());
        dropSelf(TolkienBlocks.BARREL_CULUMALDA.get());
        dropSelf(TolkienBlocks.CULUMALDA_SIGN.get());
        dropSelf(TolkienBlocks.CULUMALDA_HANGING_SIGN.get());
        add(TolkienBlocks.CULUMALDA_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.CULUMALDA_SIGN));
        add(TolkienBlocks.CULUMALDA_HANGING_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.CULUMALDA_HANGING_SIGN));

            // Lebethron
        dropSelf(TolkienBlocks.LOG_LEBETHRON.get());
        dropSelf(TolkienBlocks.WOOD_LEBETHRON.get());
        dropSelf(TolkienBlocks.STRIPPED_LEBETHRON_LOG.get());
        dropSelf(TolkienBlocks.STRIPPED_LEBETHRON_WOOD.get());
        dropSelf(TolkienBlocks.PLANKS_LEBETHRON.get());
        dropLeaves(TolkienBlocks.LEAVES_LEBETHRON);
        dropSelf(TolkienBlocks.LEAFPILE_LEBETHRON.get());
        dropSelf(TolkienBlocks.STAIRS_LEBETHRON.get());
        this.add(TolkienBlocks.SLAB_LEBETHRON.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_LEBETHRON.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_LEBETHRON.get());
        dropSelf(TolkienBlocks.LEBETHRON_BUTTON.get());
        dropSelf(TolkienBlocks.FENCE_LEBETHRON.get());
        dropSelf(TolkienBlocks.FENCE_GATE_LEBETHRON.get());
        dropSelf(TolkienBlocks.TRAPDOOR_LEBETHRON.get());
        this.add(TolkienBlocks.DOOR_LEBETHRON.get(),
                block -> createDoorTable(TolkienBlocks.DOOR_LEBETHRON.get()));
        dropSelf(TolkienBlocks.TORCH_LEBETHRON.get());
        dropOther(TolkienBlocks.WALL_TORCH_LEBETHRON, TolkienBlocks.TORCH_LEBETHRON.get());
        dropSelf(TolkienBlocks.BARREL_LEBETHRON.get());
        dropSelf(TolkienBlocks.LEBETHRON_SIGN.get());
        dropSelf(TolkienBlocks.LEBETHRON_HANGING_SIGN.get());
        add(TolkienBlocks.LEBETHRON_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.LEBETHRON_SIGN));
        add(TolkienBlocks.LEBETHRON_HANGING_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.LEBETHRON_HANGING_SIGN));

            // Fangorn Oak
        dropSelf(TolkienBlocks.LOG_FANGORNOAK.get());
        dropSelf(TolkienBlocks.WOOD_FANGORNOAK.get());
        dropSelf(TolkienBlocks.STRIPPED_FANGORNOAK_LOG.get());
        dropSelf(TolkienBlocks.STRIPPED_FANGORNOAK_WOOD.get());
        dropSelf(TolkienBlocks.PLANKS_FANGORNOAK.get());
        dropLeaves(TolkienBlocks.LEAVES_FANGORNOAK);
        dropSelf(TolkienBlocks.LEAFPILE_FANGORNOAK.get());
        dropSelf(TolkienBlocks.STAIRS_FANGORNOAK.get());
        this.add(TolkienBlocks.SLAB_FANGORNOAK.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_FANGORNOAK.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_FANGORNOAK.get());
        dropSelf(TolkienBlocks.FANGORNOAK_BUTTON.get());
        dropSelf(TolkienBlocks.FENCE_FANGORNOAK.get());
        dropSelf(TolkienBlocks.FENCE_GATE_FANGORNOAK.get());
        dropSelf(TolkienBlocks.TRAPDOOR_FANGORNOAK.get());
        this.add(TolkienBlocks.DOOR_FANGORNOAK.get(),
                block -> createDoorTable(TolkienBlocks.DOOR_FANGORNOAK.get()));
        dropSelf(TolkienBlocks.TORCH_FANGORNOAK.get());
        dropOther(TolkienBlocks.WALL_TORCH_FANGORNOAK, TolkienBlocks.TORCH_FANGORNOAK.get());
        dropSelf(TolkienBlocks.BARREL_FANGORNOAK.get());
        dropSelf(TolkienBlocks.FANGORNOAK_SIGN.get());
        dropSelf(TolkienBlocks.FANGORNOAK_HANGING_SIGN.get());
        add(TolkienBlocks.FANGORNOAK_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.FANGORNOAK_SIGN));
        add(TolkienBlocks.FANGORNOAK_HANGING_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.FANGORNOAK_HANGING_SIGN));

            // Deadwood
        dropSelf(TolkienBlocks.LOG_DEADWOOD.get());
        dropSelf(TolkienBlocks.WOOD_DEADWOOD.get());
        dropSelf(TolkienBlocks.STRIPPED_DEADWOOD_LOG.get());
        dropSelf(TolkienBlocks.STRIPPED_DEADWOOD_WOOD.get());
        dropSelf(TolkienBlocks.PLANKS_DEADWOOD.get());
        dropSelf(TolkienBlocks.STAIRS_DEADWOOD.get());
        this.add(TolkienBlocks.SLAB_DEADWOOD.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_DEADWOOD.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_DEADWOOD.get());
        dropSelf(TolkienBlocks.DEADWOOD_BUTTON.get());
        dropSelf(TolkienBlocks.FENCE_DEADWOOD.get());
        dropSelf(TolkienBlocks.FENCE_GATE_DEADWOOD.get());
        dropSelf(TolkienBlocks.TRAPDOOR_DEADWOOD.get());
        this.add(TolkienBlocks.DOOR_DEADWOOD.get(),
                block -> createDoorTable(TolkienBlocks.DOOR_DEADWOOD.get()));
        dropSelf(TolkienBlocks.TORCH_DEADWOOD.get());
        dropOther(TolkienBlocks.WALL_TORCH_DEADWOOD, TolkienBlocks.TORCH_DEADWOOD.get());
        dropSelf(TolkienBlocks.BARREL_DEADWOOD.get());
        dropSelf(TolkienBlocks.DEADWOOD_SIGN.get());
        dropSelf(TolkienBlocks.DEADWOOD_HANGING_SIGN.get());
        add(TolkienBlocks.DEADWOOD_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.DEADWOOD_SIGN));
        add(TolkienBlocks.DEADWOOD_HANGING_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.DEADWOOD_HANGING_SIGN));

            // Dwarven Maple
        dropSelf(TolkienBlocks.LOG_DWARVEN_MAPLE.get());
        dropSelf(TolkienBlocks.WOOD_DWARVEN_MAPLE.get());
        dropSelf(TolkienBlocks.STRIPPED_DWARVEN_MAPLE_LOG.get());
        dropSelf(TolkienBlocks.STRIPPED_DWARVEN_MAPLE_WOOD.get());
        dropSelf(TolkienBlocks.PLANKS_DWARVEN_MAPLE.get());
        dropLeaves(TolkienBlocks.LEAVES_DWARVEN_MAPLE);
        dropSelf(TolkienBlocks.LEAFPILE_DWARVEN_MAPLE.get());
        dropSelf(TolkienBlocks.STAIRS_DWARVEN_MAPLE.get());
        this.add(TolkienBlocks.SLAB_DWARVEN_MAPLE.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_DWARVEN_MAPLE.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_DWARVEN_MAPLE.get());
        dropSelf(TolkienBlocks.DWARVEN_MAPLE_BUTTON.get());
        dropSelf(TolkienBlocks.FENCE_DWARVEN_MAPLE.get());
        dropSelf(TolkienBlocks.FENCE_GATE_DWARVEN_MAPLE.get());
        dropSelf(TolkienBlocks.TRAPDOOR_DWARVEN_MAPLE.get());
        this.add(TolkienBlocks.DOOR_DWARVEN_MAPLE.get(),
                block -> createDoorTable(TolkienBlocks.DOOR_DWARVEN_MAPLE.get()));
        dropSelf(TolkienBlocks.TORCH_DWARVEN_MAPLE.get());
        dropOther(TolkienBlocks.WALL_TORCH_DWARVEN_MAPLE, TolkienBlocks.TORCH_DWARVEN_MAPLE.get());
        dropSelf(TolkienBlocks.BARREL_DWARVEN_MAPLE.get());
        dropSelf(TolkienBlocks.DWARVEN_MAPLE_SIGN.get());
        dropSelf(TolkienBlocks.DWARVEN_MAPLE_HANGING_SIGN.get());
        add(TolkienBlocks.DWARVEN_MAPLE_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.DWARVEN_MAPLE_SIGN));
        add(TolkienBlocks.DWARVEN_MAPLE_HANGING_WALL_SIGN.get(),
                block -> createSingleItemTable(TolkienBlocks.DWARVEN_MAPLE_HANGING_SIGN));

            // Darkstone
        dropSelf(TolkienBlocks.DARK_STONE.get());
        dropSelf(TolkienBlocks.STAIRS_DARK_STONE.get());
        this.add(TolkienBlocks.SLAB_DARK_STONE.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_DARK_STONE.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_DARK_STONE.get());
        dropSelf(TolkienBlocks.DARK_STONE_BUTTON.get());
        dropSelf(TolkienBlocks.WALL_DARK_STONE.get());
        dropSelf(TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.STAIRS_CHISELED_DARK_STONE_BRICKS.get());
        this.add(TolkienBlocks.SLAB_CHISELED_DARK_STONE_BRICKS.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_CHISELED_DARK_STONE_BRICKS.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_CHISELED_DARK_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.CHISELED_DARK_STONE_BRICKS_BUTTON.get());
        dropSelf(TolkienBlocks.WALL_CHISELED_DARK_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.COBBLED_DARK_STONE.get());
        dropSelf(TolkienBlocks.STAIRS_COBBLED_DARK_STONE.get());
        this.add(TolkienBlocks.SLAB_COBBLED_DARK_STONE.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_COBBLED_DARK_STONE.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_COBBLED_DARK_STONE.get());
        dropSelf(TolkienBlocks.COBBLED_DARK_STONE_BUTTON.get());
        dropSelf(TolkienBlocks.WALL_COBBLED_DARK_STONE.get());
        dropSelf(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.STAIRS_CRACKED_DARK_STONE_BRICKS.get());
        this.add(TolkienBlocks.SLAB_CRACKED_DARK_STONE_BRICKS.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_CRACKED_DARK_STONE_BRICKS.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_CRACKED_DARK_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.CRACKED_DARK_STONE_BRICKS_BUTTON.get());
        dropSelf(TolkienBlocks.WALL_CRACKED_DARK_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.DARK_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.STAIRS_DARK_STONE_BRICKS.get());
        this.add(TolkienBlocks.SLAB_DARK_STONE_BRICKS.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_DARK_STONE_BRICKS.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_DARK_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.DARK_STONE_BRICKS_BUTTON.get());
        dropSelf(TolkienBlocks.WALL_DARK_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.SMOOTH_DARK_STONE.get());
        dropSelf(TolkienBlocks.STAIRS_SMOOTH_DARK_STONE.get());
        this.add(TolkienBlocks.SLAB_SMOOTH_DARK_STONE.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_SMOOTH_DARK_STONE.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_SMOOTH_DARK_STONE.get());
        dropSelf(TolkienBlocks.SMOOTH_DARK_STONE_BUTTON.get());
        dropSelf(TolkienBlocks.WALL_SMOOTH_DARK_STONE.get());

            // Dwarven Stone
        dropSelf(TolkienBlocks.DWARVEN_STONE.get());
        dropSelf(TolkienBlocks.STAIRS_DWARVEN_STONE.get());
        this.add(TolkienBlocks.SLAB_DWARVEN_STONE.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_DWARVEN_STONE.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_DWARVEN_STONE.get());
        dropSelf(TolkienBlocks.DWARVEN_STONE_BUTTON.get());
        dropSelf(TolkienBlocks.WALL_DWARVEN_STONE.get());
        dropSelf(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.STAIRS_CHISELED_DWARVEN_STONE_BRICKS.get());
        this.add(TolkienBlocks.SLAB_CHISELED_DWARVEN_STONE_BRICKS.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_CHISELED_DWARVEN_STONE_BRICKS.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_CHISELED_DWARVEN_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS_BUTTON.get());
        dropSelf(TolkienBlocks.WALL_CHISELED_DWARVEN_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.COBBLED_DWARVEN_STONE.get());
        dropSelf(TolkienBlocks.STAIRS_COBBLED_DWARVEN_STONE.get());
        this.add(TolkienBlocks.SLAB_COBBLED_DWARVEN_STONE.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_COBBLED_DWARVEN_STONE.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_COBBLED_DWARVEN_STONE.get());
        dropSelf(TolkienBlocks.COBBLED_DWARVEN_STONE_BUTTON.get());
        dropSelf(TolkienBlocks.WALL_COBBLED_DWARVEN_STONE.get());
        dropSelf(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.STAIRS_CRACKED_DWARVEN_STONE_BRICKS.get());
        this.add(TolkienBlocks.SLAB_CRACKED_DWARVEN_STONE_BRICKS.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_CRACKED_DWARVEN_STONE_BRICKS.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_CRACKED_DWARVEN_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS_BUTTON.get());
        dropSelf(TolkienBlocks.WALL_CRACKED_DWARVEN_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.DWARVEN_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.STAIRS_DWARVEN_STONE_BRICKS.get());
        this.add(TolkienBlocks.SLAB_DWARVEN_STONE_BRICKS.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_DWARVEN_STONE_BRICKS.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_DWARVEN_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.DWARVEN_STONE_BRICKS_BUTTON.get());
        dropSelf(TolkienBlocks.WALL_DWARVEN_STONE_BRICKS.get());
        dropSelf(TolkienBlocks.SMOOTH_DWARVEN_STONE.get());
        dropSelf(TolkienBlocks.STAIRS_SMOOTH_DWARVEN_STONE.get());
        this.add(TolkienBlocks.SLAB_SMOOTH_DWARVEN_STONE.get(),
                block -> createSlabItemTable(TolkienBlocks.SLAB_SMOOTH_DWARVEN_STONE.get()));
        dropSelf(TolkienBlocks.PRESSURE_PLATE_SMOOTH_DWARVEN_STONE.get());
        dropSelf(TolkienBlocks.SMOOTH_DWARVEN_STONE_BUTTON.get());
        dropSelf(TolkienBlocks.WALL_SMOOTH_DWARVEN_STONE.get());

        //Flowers & Plants
        dropSelf(TolkienBlocks.FLOWER_SIMBELMYNE.get());
        dropSelf(TolkienBlocks.FLOWER_MIRKWOOD.get());
        dropSelf(TolkienBlocks.FLOWER_ALFIRIN.get());
        dropSelf(TolkienBlocks.FLOWER_ATHELAS.get());
        dropSelf(TolkienBlocks.FLOWER_NIPHREDIL.get());
        dropSelf(TolkienBlocks.FLOWER_SWAMPMILKWEED.get());
        dropSelf(TolkienBlocks.FLOWER_LILLYOFTHEVALLEY.get());
        dropSelf(TolkienBlocks.FLOWER_ELANOR.get());
        dropSelf(TolkienBlocks.FLOWER_AEGLOS.get());
        dropSelf(TolkienBlocks.FLOWER_LISSUIN.get());
        dropSelf(TolkienBlocks.FLOWER_MALLOS.get());
        dropPottedContents(TolkienBlocks.POTTED_FLOWER_SIMBELMYNE.get());
        dropPottedContents(TolkienBlocks.POTTED_FLOWER_MIRKWOOD.get());
        dropPottedContents(TolkienBlocks.POTTED_FLOWER_ALFIRIN.get());
        dropPottedContents(TolkienBlocks.POTTED_FLOWER_ATHELAS.get());
        dropPottedContents(TolkienBlocks.POTTED_FLOWER_NIPHREDIL.get());
        dropPottedContents(TolkienBlocks.POTTED_FLOWER_SWAMPMILKWEED.get());
        dropPottedContents(TolkienBlocks.POTTED_FLOWER_LILLYOFTHEVALLEY.get());
        dropPottedContents(TolkienBlocks.POTTED_FLOWER_ELANOR.get());
        dropPottedContents(TolkienBlocks.POTTED_FLOWER_AEGLOS.get());
        dropPottedContents(TolkienBlocks.POTTED_FLOWER_LISSUIN.get());
        dropPottedContents(TolkienBlocks.POTTED_FLOWER_MALLOS.get());
        dropPottedContents(TolkienBlocks.POTTED_FLOWER_BRAMBLES.get());
        dropPottedContents(TolkienBlocks.POTTED_SAPLING_MALLORN.get());
        dropPottedContents(TolkienBlocks.POTTED_SAPLING_MIRKWOOD.get());
        dropPottedContents(TolkienBlocks.POTTED_SAPLING_CULUMALDA.get());
        dropPottedContents(TolkienBlocks.POTTED_SAPLING_LEBETHRON.get());
        dropPottedContents(TolkienBlocks.POTTED_SAPLING_FANGORNOAK.get());
        dropPottedContents(TolkienBlocks.POTTED_SAPLING_DEADWOOD.get());
        dropPottedContents(TolkienBlocks.POTTED_SAPLING_DWARVEN_MAPLE.get());
        dropPottedContents(TolkienBlocks.POTTED_MUSHROOM_BLOOM_DECAY.get());
        dropPottedContents(TolkienBlocks.POTTED_MUSHROOM_DECAY_BLOOM.get());
        dropSelf(TolkienBlocks.SAPLING_MALLORN.get());
        dropSelf(TolkienBlocks.SAPLING_MIRKWOOD.get());
        dropSelf(TolkienBlocks.SAPLING_CULUMALDA.get());
        dropSelf(TolkienBlocks.SAPLING_LEBETHRON.get());
        dropSelf(TolkienBlocks.SAPLING_FANGORNOAK.get());
        dropSelf(TolkienBlocks.SAPLING_DEADWOOD.get());
        dropSelf(TolkienBlocks.SAPLING_DWARVEN_MAPLE.get());
        dropSelf(TolkienBlocks.MUSHROOM_DECAY_BLOOM.get());
        dropSelf(TolkienBlocks.MUSHROOM_BLOOM_DECAY.get());
        dropOther(TolkienBlocks.BLOCK_DECAY_BLOOM, TolkienBlocks.MUSHROOM_DECAY_BLOOM);
        dropOther(TolkienBlocks.BLOCK_BLOOM_DECAY, TolkienBlocks.MUSHROOM_BLOOM_DECAY);
        dropOther(TolkienBlocks.WALL_DECAY_BLOOM, TolkienBlocks.MUSHROOM_BLOOM_DECAY);
        dropOther(TolkienBlocks.WALL_MUSHROOM_RED, Blocks.RED_MUSHROOM);
        dropOther(TolkienBlocks.WALL_MUSHROOM_BROWN, Blocks.BROWN_MUSHROOM);
        dropOther(TolkienBlocks.LIVING_ROOTS, Items.STICK);

        LootItemCondition.Builder lootItemConditionBuilder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(TolkienBlocks.PIPEWEED.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PipeweedCropBlock.AGE, 7));
        this.add(TolkienBlocks.PIPEWEED.get(), this.createCropDrops(TolkienBlocks.PIPEWEED.get(),
                TolkienItems.PIPEWEED_ITEM.get(), TolkienItems.PIPEWEED_SEEDS.asItem(), lootItemConditionBuilder));
        LootItemCondition.Builder lootItemConditionBuilder2 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(TolkienBlocks.PIPEWEED.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BramblesBushBlock.AGE, 3));
        this.add(TolkienBlocks.FLOWER_BRAMBLES.get(), this.createCropDrops(TolkienBlocks.FLOWER_BRAMBLES.get(),
                TolkienBlocks.FLOWER_BRAMBLES.asItem(), TolkienItems.BRAMBLES_BERRY.asItem(), lootItemConditionBuilder2));

            // Custom
        dropOther(TolkienBlocks.LIGHTNINGBUG_BLOCK.get(), Items.GLOWSTONE);
        dropOther(TolkienBlocks.LOCUST_BLOCK.get(), Items.GUNPOWDER);
        dropSelf(TolkienBlocks.SILMARIL_LANTERN.get());
        dropSelf(TolkienBlocks.ELVEN_LANTERN.get());
        dropSelf(TolkienBlocks.MORGUL_LANTERN.get());
        dropSelf(TolkienBlocks.BLOCK_HALLOWED.get());
        dropOther(TolkienBlocks.STONE_PATH, Blocks.COBBLESTONE);
        dropSelf(TolkienBlocks.PLACARD.get());
        dropSelf(TolkienBlocks.ROCKPILE.get());

        // Sleeping Bags
        this.add(TolkienBlocks.SLEEPING_BAG_BLUE.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_RED.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_BLACK.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_BROWN.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_CYAN.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_GRAY.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_GREEN.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_LIGHT_BLUE.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_LIGHT_GRAY.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_LIME.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_MAGENTA.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_ORANGE.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_PINK.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_PURPLE.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_WHITE.get(), this::createSleepingBagDrop);
        this.add(TolkienBlocks.SLEEPING_BAG_YELLOW.get(), this::createSleepingBagDrop);
    }

    protected LootTable.Builder createSleepingBagDrop(Block block)
    {
        return LootTable.lootTable()
                .withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SleepingBagBlock.PART, BedPart.HEAD))))));
    }

    public void dropLeaves(Supplier<? extends Block> leaves) {
        this.add(leaves.get(), block -> createLeavesDrops(block, leaves.get(), NORMAL_LEAVES_SAPLING_CHANCES));
    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock, this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    public void dropOther(Supplier<? extends Block> brokenBlock, ItemLike droppedBlock) {
        this.dropOther(brokenBlock.get(), droppedBlock);
    }

    private LootTable.Builder createTable(Block block) {
        return LootTable.lootTable()
                .withPool(
                        this.applyExplosionCondition(
                                block,
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(block)
                                                        .apply(CopyComponentsFunction.copyComponents(
                                                                CopyComponentsFunction.Source.BLOCK_ENTITY).include(
                                                                DataComponents.CUSTOM_NAME))
                                                        .when(
                                                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                                        .setProperties(
                                                                                StatePropertiesPredicate.Builder.properties()
                                                                                        .hasProperty(BedBlock.PART, BedPart.HEAD))
                                                        )
                                        )
                        )
                );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return TolkienBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
