package com.greatorator.tolkientweaks.datagen.loot;

import com.greatorator.tolkientweaks.TolkienTweaks;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.conditions.TableBonus;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.LimitCount;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockLootGenerator extends BlockLootTables {
    private final Set<Block> knownBlocks = new HashSet<>();

    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
    private static final float[] RARE_SAPLING_DROP_RATES = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
    private static final ILootCondition.IBuilder SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));
    private static final ILootCondition.IBuilder SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
    private static final ILootCondition.IBuilder SILK_TOUCH_OR_SHEARS = SHEARS.or(SILK_TOUCH);
    private static final ILootCondition.IBuilder NOT_SILK_TOUCH_OR_SHEARS = SILK_TOUCH_OR_SHEARS.invert();

    @Override
    protected void add(Block block, LootTable.Builder builder) {
        super.add(block, builder);
        knownBlocks.add(block);
    }

    @Override
    protected void addTables() {
        // Blocks - Metals & Gems
//        dropSelf(TTMContent.BLOCK_MITHRIL.get());
//        add(TTMContent.LOG_DEADWOOD.get(), createSingleItemTable(Items.STICK, RandomValueRange.between(0.0F, 4.0F)));
//        add(TTMContent.LEAVES_MALLORN.get(), (block) -> createLeavesDrops(block, TTMContent.SAPLING_MALLORN.get(), .05f, .0625f, .083333336f, .1f));
//        add(TTMContent.BLOCK_BLOOM_DECAY.get(), (p_229434_0_) -> {
//            return createMushroomBlockDrop(p_229434_0_, TTMContent.BLOCK_BLOOM_DECAY_ITEM.get());
//        });
//        ILootCondition.IBuilder ilootcondition$ibuilder1 = BlockStateProperty.hasBlockStateProperties(TTMContent.PIPEWEED.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropsBlock.AGE, 7));
//        add(TTMContent.PIPEWEED.get(), createCropDrops(TTMContent.PIPEWEED.get(), TTMContent.PIPEWEED_ITEM.get(), TTMContent.PIPEWEED_SEEDS.get(), ilootcondition$ibuilder1));
//        add(TTMContent.SLEEPING_BAG_RED.get(), block -> createSinglePropConditionTable(block, BedBlock.PART, BedPart.HEAD));

        //Fortune
//        add(TTMContent.ORE_MITHRIL.get(), (block) -> createSilkTouchDispatchTable(block, applyExplosionDecay(block, ItemLootEntry.lootTableItem(TTMContent.DUST_MITHRIL.get()).apply(SetCount.setCount(RandomValueRange.between(1.0F, 2.0F))).apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)))));

        //Signs, You will need these for every type you add
//        dropOther(TTMContent.MALLORN_SIGN_WOOD_TYPE.get(), TTMContent.MALLORN_SIGN_ITEM_WOOD_TYPE.get());
    }

    protected static LootTable.Builder createMushroomBlockDrop(Block p_218491_0_, IItemProvider p_218491_1_) {
        return createSilkTouchDispatchTable(p_218491_0_, applyExplosionDecay(p_218491_0_, ItemLootEntry.lootTableItem(p_218491_1_).apply(SetCount.setCount(RandomValueRange.between(-6.0F, 2.0F))).apply(LimitCount.limitCount(IntClamper.lowerBound(0)))));
    }

    protected static LootTable.Builder ChancesAndSticks(Block block, Block log, float... chances) {
        return createSilkTouchDispatchTable(block, applyExplosionCondition(block, ItemLootEntry.lootTableItem(log)).when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, chances))).withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).when(NOT_SILK_TOUCH_OR_SHEARS).add(applyExplosionDecay(block, ItemLootEntry.lootTableItem(Items.STICK).apply(SetCount.setCount(RandomValueRange.between(1.0F, 2.0F)))).when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))));
    }

    protected static LootTable.Builder createCropDrops(Block block, Item item1, Item item2, ILootCondition.IBuilder builder) {
        return applyExplosionDecay(block, LootTable.lootTable().withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(item1).when(builder).otherwise(ItemLootEntry.lootTableItem(item2)))).withPool(LootPool.lootPool().when(builder).add(ItemLootEntry.lootTableItem(item2).apply(ApplyBonus.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))));
    }

    protected static LootTable.Builder createSingleItemTable(IItemProvider p_218463_0_, IRandomRange p_218463_1_) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(applyExplosionDecay(p_218463_0_, ItemLootEntry.lootTableItem(p_218463_0_).apply(SetCount.setCount(p_218463_1_)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Registry.BLOCK.stream().filter(block -> Objects.requireNonNull(block.getRegistryName()).getNamespace().equals(TolkienTweaks.MODID)).collect(Collectors.toList());
    }

    public String getName() {
        return "Tolkien Tweaks - Loot Tables - Blocks";
    }
}
