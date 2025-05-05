package com.greatorator.tolkienmobs.datagen.tags;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienItemTagProvider extends ItemTagsProvider {
    public TolkienItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider,
                                  CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, MODID, existingFileHelper);
    }


    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        tag(TolkienTags.Items.COINS)
                .add(TolkienItems.ITEM_COIN_BRONZE.get())
                .add(TolkienItems.ITEM_COIN_SILVER.get())
                .add(TolkienItems.ITEM_COIN_GOLD.get())
                .add(TolkienItems.ITEM_COIN_MITHRIL.get())
                .add(TolkienItems.ITEM_DARKSIGIL.get())
                .add(TolkienItems.ITEM_FACTIONTOKEN.get())
                .add(TolkienItems.ITEM_FACTIONCOIN.get())
                .add(TolkienItems.ITEM_CAVECOMPLETE.get())
                .add(TolkienItems.ITEM_WATCHERCOMPLETE.get())
                .add(TolkienItems.ITEM_TOKEN_EASTERN_ALLIANCE.get())
                .add(TolkienItems.ITEM_TOKEN_WESTERN_ALLIANCE.get());
        tag(TolkienTags.Items.KEYS)
                .add(TolkienItems.BRONZE_KEY.get())
                .add(TolkienItems.SILVER_KEY.get())
                .add(TolkienItems.GOLD_KEY.get())
                .add(TolkienItems.MITHRIL_KEY.get())
                .add(TolkienItems.MASTER_KEY.get());
        tag(TolkienTags.Items.INSECTS)
                .add(TolkienItems.INSECT.get())
                .add(TolkienItems.GOLDEN_INSECT.get());
        tag(TolkienTags.Items.ACORNS)
                .add(TolkienItems.TREE_ACORN.get())
                .add(TolkienItems.GOLDEN_TREE_ACORN.get());
        tag(TolkienTags.Items.TRINKET)
                .add(TolkienItems.TRINKET_RING.get())
                .add(TolkienItems.TRINKET_AMULET.get())
                .add(TolkienItems.TRINKET_CLOAK.get())
                .add(TolkienItems.TRINKET_HAT.get())
                .add(TolkienItems.TRINKET_GLOVE.get())
                .add(TolkienItems.TRINKET_CHARM.get())
                .add(TolkienItems.TRINKET_BELT.get());
        tag(TolkienTags.Items.SLEEPING_BAG)
                .add(TolkienItems.SLEEPING_BAG_BLACK.get())
                .add(TolkienItems.SLEEPING_BAG_WHITE.get())
                .add(TolkienItems.SLEEPING_BAG_BLUE.get())
                .add(TolkienItems.SLEEPING_BAG_BROWN.get())
                .add(TolkienItems.SLEEPING_BAG_CYAN.get())
                .add(TolkienItems.SLEEPING_BAG_GRAY.get())
                .add(TolkienItems.SLEEPING_BAG_GREEN.get())
                .add(TolkienItems.SLEEPING_BAG_LIME.get())
                .add(TolkienItems.SLEEPING_BAG_MAGENTA.get())
                .add(TolkienItems.SLEEPING_BAG_ORANGE.get())
                .add(TolkienItems.SLEEPING_BAG_PINK.get())
                .add(TolkienItems.SLEEPING_BAG_PURPLE.get())
                .add(TolkienItems.SLEEPING_BAG_YELLOW.get())
                .add(TolkienItems.SLEEPING_BAG_RED.get())
                .add(TolkienItems.SLEEPING_BAG_LIGHT_BLUE.get())
                .add(TolkienItems.SLEEPING_BAG_LIGHT_GRAY.get());
        tag(ItemTags.SHOVELS)
                .add(TolkienItems.SHOVEL_MITHRIL.get())
                .add(TolkienItems.SHOVEL_MORGULIRON.get())
                .add(TolkienItems.SHEARS_AMMOLITE.get());
        tag(ItemTags.PICKAXES)
                .add(TolkienItems.PICKAXE_MITHRIL.get())
                .add(TolkienItems.PICKAXE_MORGULIRON.get())
                .add(TolkienItems.PICKAXE_AMMOLITE.get());
        tag(ItemTags.AXES)
                 .add(TolkienItems.AXE_MITHRIL.get())
                .add(TolkienItems.AXE_MORGULIRON.get())
                .add(TolkienItems.AXE_AMMOLITE.get());
        tag(ItemTags.SWORDS)
                 .add(TolkienItems.SWORD_MITHRIL.get())
                .add(TolkienItems.SWORD_MORGULIRON.get())
                .add(TolkienItems.SWORD_AMMOLITE.get());
        tag(ItemTags.HOES)
                .add(TolkienItems.HOE_MITHRIL.get())
                .add(TolkienItems.HOE_MORGULIRON.get())
                .add(TolkienItems.HOE_AMMOLITE.get());
        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(TolkienItems.HELMET_MITHRIL.get())
                .add(TolkienItems.CHESTPLATE_MITHRIL.get())
                .add(TolkienItems.LEGGINGS_MITHRIL.get())
                .add(TolkienItems.BOOTS_MITHRIL.get())
                .add(TolkienItems.HELMET_MORGULIRON.get())
                .add(TolkienItems.CHESTPLATE_MORGULIRON.get())
                .add(TolkienItems.LEGGINGS_MORGULIRON.get())
                .add(TolkienItems.BOOTS_MORGULIRON.get());
        tag(Tags.Items.FOODS)
                .add(TolkienItems.CRAM.get())
                .add(TolkienItems.FOOD_HONEY.get())
                .add(TolkienItems.LEMBAS.get())
                .add(TolkienItems.HONEY_CAKE.get())
                .add(TolkienItems.MONSTER_FLESH.get())
                .add(TolkienItems.INSECT.get())
                .add(TolkienItems.GOLDEN_INSECT.get())
                .add(TolkienItems.TREE_ACORN.get())
                .add(TolkienItems.GOLDEN_TREE_ACORN.get())
                .add(TolkienItems.BRAMBLES_BERRY.get())
                .add(TolkienItems.MIRUVOR.get())
                .add(TolkienItems.GROG.get())
                .add(TolkienItems.DRINK_ELF_FLEETFOOT.get())
                .add(TolkienItems.DRINK_ENT_DRAUGHT.get())
                .add(TolkienItems.DRINK_ELF_VITALITY.get())
                .add(TolkienItems.DRINK_ERU_BLESSING.get())
                .add(TolkienItems.DRINK_PERSONAL_BLACKSMITH.get());
        tag(TolkienTags.Items.UPGRADES)
                .add(TolkienItems.ITEM_BACKPACK_UPGRADE_SIZE.get())
                .add(TolkienItems.ITEM_BACKPACK_UPGRADE_SIZE_2.get())
                .add(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID.get())
                .add(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_2.get())
                .add(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_3.get())
                .add(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_4.get())
                .add(TolkienItems.ITEM_BACKPACK_UPGRADE_FLUID_5.get())
                .add(TolkienItems.ITEM_BACKPACK_UPGRADE_CRAFTING.get())
                .add(TolkienItems.ITEM_BACKPACK_UPGRADE_SLEEPING.get())
                .add(TolkienItems.ITEM_BACKPACK_UPGRADE_CAMPFIRE.get());
        tag(TolkienTags.Items.CURIOS_AMULET)
                .add(TolkienItems.TRINKET_AMULET.get());
        tag(TolkienTags.Items.CURIOS_RING)
                .add(TolkienItems.TRINKET_RING.get());
        tag(TolkienTags.Items.CURIOS_CHARM)
                .add(TolkienItems.TRINKET_CHARM.get());
        tag(TolkienTags.Items.CURIOS_BELT)
                .add(TolkienItems.TRINKET_BELT.get());
        tag(TolkienTags.Items.CURIOS_HANDS)
                .add(TolkienItems.TRINKET_GLOVE.get());
        tag(TolkienTags.Items.CURIOS_BODY)
                .add(TolkienItems.TRINKET_CLOAK.get());
        tag(TolkienTags.Items.CURIOS_HEAD)
                .add(TolkienItems.TRINKET_HAT.get());
        tag(TolkienTags.Items.CANNOT_AUTO_CONSUME)
                .add(Items.CHORUS_FRUIT)
                .add(Items.ENCHANTED_GOLDEN_APPLE)
                .add(Items.GOLDEN_APPLE)
                .add(Items.OMINOUS_BOTTLE)
                .addOptionalTag(Tags.Items.FOODS_RAW_MEAT)
                .addOptionalTag(Tags.Items.FOODS_RAW_FISH)
                .addOptionalTag(Tags.Items.FOODS_FOOD_POISONING)
                .addOptionalTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "foods/doughs")))
                .addOptionalTag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "foods/pastas")))
                .addOptionalTag(ResourceLocation.parse("farmersdelight:dog_food"))
                .addOptionalTag(ResourceLocation.parse("farmersdelight:pie_crust"))
                .addOptionalTag(ResourceLocation.parse("spelunkery:portal_fluid_bottle"));
        tag(ItemTags.ARROWS)
                .add(TolkienItems.GALADHRIM_ARROW.get())
                .add(TolkienItems.UTUMNO_ARROW.get());
        tag(ItemTags.SIGNS)
                .add(TolkienBlocks.MALLORN_SIGN.get().asItem())
                .add(TolkienBlocks.MIRKWOOD_SIGN.get().asItem())
                .add(TolkienBlocks.CULUMALDA_SIGN.get().asItem())
                .add(TolkienBlocks.LEBETHRON_SIGN.get().asItem())
                .add(TolkienBlocks.FANGORNOAK_SIGN.get().asItem())
                .add(TolkienBlocks.DEADWOOD_SIGN.get().asItem())
                .add(TolkienBlocks.DWARVEN_MAPLE_SIGN.get().asItem());
        tag(ItemTags.HANGING_SIGNS)
                .add(TolkienBlocks.MALLORN_HANGING_SIGN.get().asItem())
                .add(TolkienBlocks.MIRKWOOD_HANGING_SIGN.get().asItem())
                .add(TolkienBlocks.CULUMALDA_HANGING_SIGN.get().asItem())
                .add(TolkienBlocks.LEBETHRON_HANGING_SIGN.get().asItem())
                .add(TolkienBlocks.FANGORNOAK_HANGING_SIGN.get().asItem())
                .add(TolkienBlocks.DEADWOOD_HANGING_SIGN.get().asItem())
                .add(TolkienBlocks.DWARVEN_MAPLE_HANGING_SIGN.get().asItem());
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Item Tags";
    }
}
