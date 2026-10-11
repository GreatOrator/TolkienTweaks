package com.greatorator.tolkienmobs.gametest;

import com.greatorator.tolkienmobs.block.custom.entity.TrinketTableBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.greatorator.tolkienmobs.network.component.TrinketComponent;
import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import com.greatorator.tolkienmobs.recipe.TrinketRecipeInput;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.block.custom.entity.TrinketTableBlockEntity.*;

@GameTestHolder(MODID)
@PrefixGameTestTemplate(false)
public class TrinketTableGameTests {
    @GameTest(template = "trinket_table_empty")
    public static void allTrinketsCraftOnlyInTable(GameTestHelper helper) {
        for (Item item : List.of(TolkienItems.TRINKET_RING.get(), TolkienItems.TRINKET_AMULET.get(),
                TolkienItems.TRINKET_CHARM.get(), TolkienItems.TRINKET_BELT.get(), TolkienItems.TRINKET_GLOVE.get(),
                TolkienItems.TRINKET_HAT.get(), TolkienItems.TRINKET_CLOAK.get())) {
            TrinketTableBlockEntity table = newTable(helper);
            ItemStack original = item.getDefaultInstance();
            original.set(DataComponents.CUSTOM_NAME, Component.literal("Named trinket"));
            original.set(TolkienDataComponents.TRINKET_ACTIVE, true);
            original.set(TolkienDataComponents.POTION_CONTENTS, new TrinketComponent(Potions.POISON));
            ItemStack potion = PotionContents.createItemStack(Items.POTION, Potions.LONG_SWIFTNESS);
            ItemStack fuel = new ItemStack(TolkienItems.GEM_AMMOLITE.get(), 2);
            TrinketRecipeInput input = new TrinketRecipeInput(original, potion, fuel);
            TrinketRecipe recipe = helper.getLevel().getRecipeManager()
                    .getRecipeFor(TolkienRecipesTypes.TRINKET_TABLE_TYPE.get(), input, helper.getLevel())
                    .orElseThrow().value();
            helper.assertTrue(helper.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING,
                    CraftingInput.of(3, 1, List.of(original, potion, fuel)), helper.getLevel()).isEmpty(),
                    "Potion infusion must not match vanilla crafting");

            table.itemHandler.setStackInSlot(TRINKET_SLOT, original.copy());
            table.itemHandler.setStackInSlot(POTION_SLOT, potion.copy());
            table.itemHandler.setStackInSlot(GEM_SLOT, fuel.copy());
            tick(table, 99);
            helper.assertTrue(table.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty(), "Craft finished early");
            tick(table, 1);
            ItemStack expected = original.copy();
            expected.set(TolkienDataComponents.POTION_CONTENTS, new TrinketComponent(Potions.LONG_SWIFTNESS));
            helper.assertTrue(ItemStack.isSameItemSameComponents(expected, table.itemHandler.getStackInSlot(OUTPUT_SLOT)),
                    "Output must retain item and components while replacing potion type");
            helper.assertTrue(table.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() == 1, "Expected one output");
            helper.assertTrue(table.itemHandler.getStackInSlot(TRINKET_SLOT).isEmpty(), "Trinket not consumed");
            helper.assertTrue(table.itemHandler.getStackInSlot(POTION_SLOT).is(Items.GLASS_BOTTLE), "Bottle not returned");
            helper.assertTrue(table.itemHandler.getStackInSlot(GEM_SLOT).getCount() == 1, "Expected one gem consumed");
            helper.assertTrue(original.get(TolkienDataComponents.POTION_CONTENTS).is(Potions.POISON), "Input mutated");
            verifyCodecs(helper, recipe, input, expected);
        }
        helper.succeed();
    }

    @GameTest(template = "trinket_table_empty")
    public static void rejectsInvalidInputsAndHonorsFuelCount(GameTestHelper helper) {
        ItemStack trinket = TolkienItems.TRINKET_RING.get().getDefaultInstance();
        ItemStack potion = PotionContents.createItemStack(Items.POTION, Potions.HEALING);
        ItemStack fuel = new ItemStack(TolkienItems.GEM_AMMOLITE.get(), 2);
        TrinketRecipe recipe = new TrinketRecipe("test", trinket.getItem(),
                SizedIngredient.of(TolkienItems.GEM_AMMOLITE.get(), 2), Items.POTION);
        List<TrinketRecipeInput> invalid = List.of(
                new TrinketRecipeInput(trinket, potion, fuel.copyWithCount(1)),
                new TrinketRecipeInput(trinket, potion, new ItemStack(Items.DIAMOND, 2)),
                new TrinketRecipeInput(trinket, new ItemStack(Items.POTION), fuel),
                new TrinketRecipeInput(trinket, new ItemStack(Items.GLASS_BOTTLE), fuel),
                new TrinketRecipeInput(potion, trinket, fuel),
                new TrinketRecipeInput(TolkienItems.TRINKET_HAT.get().getDefaultInstance(), potion, fuel),
                new TrinketRecipeInput(ItemStack.EMPTY, potion, fuel));
        for (TrinketRecipeInput input : invalid) {
            helper.assertTrue(!recipe.matches(input, helper.getLevel()), "Invalid input matched");
            helper.assertTrue(recipe.assemble(input, helper.getLevel().registryAccess()).isEmpty(), "Invalid input assembled");
        }
        TrinketRecipeInput valid = new TrinketRecipeInput(trinket, potion, fuel);
        helper.assertTrue(recipe.matches(valid, helper.getLevel()), "Valid input rejected");
        verifyCodecs(helper, recipe, valid, recipe.assemble(valid, helper.getLevel().registryAccess()));
        TrinketTableBlockEntity table = newTable(helper);
        helper.assertTrue(!table.itemHandler.insertItem(GEM_SLOT, new ItemStack(Items.COAL), false).isEmpty(), "Accepted wrong fuel");
        helper.assertTrue(!table.itemHandler.insertItem(OUTPUT_SLOT, trinket.copy(), false).isEmpty(), "Accepted output insertion");
        helper.assertTrue(table.itemHandler.insertItem(TRINKET_SLOT, trinket.copyWithCount(2), false).getCount() == 1,
                "Trinket input must hold only one item");
        helper.succeed();
    }

    @GameTest(template = "trinket_table_empty")
    public static void blockedOutputAndChangedInputsDoNotConsumeItems(GameTestHelper helper) {
        TrinketTableBlockEntity table = newTable(helper);
        ItemStack trinket = TolkienItems.TRINKET_RING.get().getDefaultInstance();
        ItemStack potion = PotionContents.createItemStack(Items.POTION, Potions.SWIFTNESS);
        table.itemHandler.setStackInSlot(TRINKET_SLOT, trinket.copy());
        table.itemHandler.setStackInSlot(POTION_SLOT, potion.copy());
        table.itemHandler.setStackInSlot(GEM_SLOT, new ItemStack(TolkienItems.GEM_AMMOLITE.get(), 3));
        // Same item, different potion: the existing output must never be overwritten or merged.
        table.itemHandler.setStackInSlot(OUTPUT_SLOT, trinket.copy());
        tick(table, 120);
        helper.assertTrue(table.itemHandler.getStackInSlot(GEM_SLOT).getCount() == 3, "Blocked output consumed fuel");
        helper.assertTrue(ItemStack.isSameItemSameComponents(table.itemHandler.getStackInSlot(POTION_SLOT), potion), "Blocked output consumed potion");
        helper.assertTrue(ItemStack.isSameItemSameComponents(table.itemHandler.getStackInSlot(OUTPUT_SLOT), trinket), "Output overwritten");
        table.itemHandler.extractItem(OUTPUT_SLOT, 1, false);
        tick(table, 50);
        table.itemHandler.setStackInSlot(POTION_SLOT, PotionContents.createItemStack(Items.POTION, Potions.HEALING));
        tick(table, 50);
        helper.assertTrue(table.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty(), "Changing input must reset progress");
        tick(table, 50);
        helper.assertTrue(table.itemHandler.getStackInSlot(OUTPUT_SLOT).get(TolkienDataComponents.POTION_CONTENTS).is(Potions.HEALING),
                "Craft must use replacement potion");
        helper.assertTrue(table.itemHandler.getStackInSlot(GEM_SLOT).getCount() == 2, "Expected exactly one craft");
        helper.succeed();
    }

    private static TrinketTableBlockEntity newTable(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, TolkienBlocks.TRINKET_TABLE.get());
        TrinketTableBlockEntity table = (TrinketTableBlockEntity) helper.getBlockEntity(pos);
        for (int slot = 0; slot < 4; slot++) {
            table.itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
        }
        return table;
    }

    private static void tick(TrinketTableBlockEntity table, int count) {
        for (int i = 0; i < count; i++) {
            table.tick(table.getLevel(), table.getBlockPos(), table.getBlockState());
        }
    }

    private static void verifyCodecs(GameTestHelper helper, TrinketRecipe recipe, TrinketRecipeInput input, ItemStack expected) {
        var ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().registryAccess());
        var codec = recipe.getSerializer().codec().codec();
        TrinketRecipe decoded = codec.parse(ops, codec.encodeStart(ops, recipe).getOrThrow()).getOrThrow();
        helper.assertTrue(decoded.matches(input, helper.getLevel()), "JSON round trip lost inputs");
        helper.assertTrue(decoded.getFuel().count() == recipe.getFuel().count(), "JSON round trip lost fuel count");
        helper.assertTrue(decoded.getGroup().equals(recipe.getGroup()), "JSON round trip lost group");
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        try {
            recipe.getSerializer().streamCodec().encode(buffer, decoded);
            decoded = recipe.getSerializer().streamCodec().decode(buffer);
            helper.assertTrue(decoded.matches(input, helper.getLevel()), "Network round trip lost inputs");
            helper.assertTrue(decoded.getFuel().count() == recipe.getFuel().count(), "Network round trip lost fuel count");
            helper.assertTrue(decoded.getGroup().equals(recipe.getGroup()), "Network round trip lost group");
            helper.assertTrue(ItemStack.isSameItemSameComponents(decoded.assemble(input, helper.getLevel().registryAccess()), expected),
                    "Network round trip changed output");
        } finally {
            buffer.release();
        }
    }
}
