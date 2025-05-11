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
        tag(ItemTags.BOATS)
                .add(TolkienItems.MALLORN_BOAT.get())
                .add(TolkienItems.MIRKWOOD_BOAT.get())
                .add(TolkienItems.CULUMALDA_BOAT.get())
                .add(TolkienItems.LEBETHRON_BOAT.get())
                .add(TolkienItems.FANGORNOAK_BOAT.get())
                .add(TolkienItems.DEADWOOD_BOAT.get())
                .add(TolkienItems.DWARVEN_MAPLE_BOAT.get());
        tag(ItemTags.CHEST_BOATS)
                .add(TolkienItems.MALLORN_CHEST_BOAT.get())
                .add(TolkienItems.MIRKWOOD_CHEST_BOAT.get())
                .add(TolkienItems.CULUMALDA_CHEST_BOAT.get())
                .add(TolkienItems.LEBETHRON_CHEST_BOAT.get())
                .add(TolkienItems.FANGORNOAK_CHEST_BOAT.get())
                .add(TolkienItems.DEADWOOD_CHEST_BOAT.get())
                .add(TolkienItems.DWARVEN_MAPLE_BOAT.get());
        tag(ItemTags.CREEPER_DROP_MUSIC_DISCS)
                .add(TolkienItems.RECORD_EREBOR.get())
                .add(TolkienItems.RECORD_HOBBITS.get())
                .add(TolkienItems.RECORD_MINASTIRITH.get())
                .add(TolkienItems.RECORD_MURDERFROG.get())
                .add(TolkienItems.RECORD_BOMBADIL.get())
                .add(TolkienItems.RECORD_REDER.get())
                .add(TolkienItems.RECORD_RIVENDELL.get())
                .add(TolkienItems.RECORD_LOTHLORIEN.get())
                .add(TolkienItems.RECORD_WILLOW.get())
                .add(TolkienItems.RECORD_FUMBLE.get())
                .add(TolkienItems.RECORD_EDORAS.get())
                .add(TolkienItems.RECORD_WBATTLE.get());
        tag(ItemTags.ARROWS)
                .add(TolkienItems.GALADHRIM_ARROW.get())
                .add(TolkienItems.UTUMNO_ARROW.get());
        tag(ItemTags.SWORDS)
                .add(TolkienItems.SWORD_MITHRIL.get())
                .add(TolkienItems.SWORD_MORGULIRON.get())
                .add(TolkienItems.SWORD_AMMOLITE.get())
                .add(TolkienItems.SWORD_WITCHKING.get())
                .add(TolkienItems.SWORD_URUK.get());
        tag(ItemTags.AXES)
                .add(TolkienItems.AXE_MITHRIL.get())
                .add(TolkienItems.AXE_MORGULIRON.get())
                .add(TolkienItems.AXE_AMMOLITE.get());
        tag(ItemTags.PICKAXES)
                .add(TolkienItems.PICKAXE_MITHRIL.get())
                .add(TolkienItems.PICKAXE_MORGULIRON.get())
                .add(TolkienItems.PICKAXE_AMMOLITE.get());
        tag(ItemTags.SHOVELS)
                .add(TolkienItems.SHOVEL_MITHRIL.get())
                .add(TolkienItems.SHOVEL_MORGULIRON.get())
                .add(TolkienItems.SHOVEL_AMMOLITE.get());
        tag(ItemTags.HOES)
                .add(TolkienItems.HOE_MITHRIL.get())
                .add(TolkienItems.HOE_MORGULIRON.get())
                .add(TolkienItems.HOE_AMMOLITE.get());
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
        tag(ItemTags.WOODEN_SLABS)
                .add(TolkienBlocks.SLAB_MALLORN.get().asItem())
                .add(TolkienBlocks.SLAB_MIRKWOOD.get().asItem())
                .add(TolkienBlocks.SLAB_CULUMALDA.get().asItem())
                .add(TolkienBlocks.SLAB_LEBETHRON.get().asItem())
                .add(TolkienBlocks.SLAB_DEADWOOD.get().asItem())
                .add(TolkienBlocks.SLAB_FANGORNOAK.get().asItem())
                .add(TolkienBlocks.SLAB_DWARVEN_MAPLE.get().asItem());
        tag(ItemTags.SLABS)
                .add(TolkienBlocks.SLAB_DARK_STONE.get().asItem())
                .add(TolkienBlocks.SLAB_DARK_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_CHISELED_DARK_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_COBBLED_DARK_STONE.get().asItem())
                .add(TolkienBlocks.SLAB_CRACKED_DARK_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_MOSSY_DARK_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_DARK_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_MOUNTAIN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_CHISELED_MOUNTAIN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_COBBLED_MOUNTAIN_STONE.get().asItem())
                .add(TolkienBlocks.SLAB_CRACKED_MOUNTAIN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_MOSSY_MOUNTAIN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_MOUNTAIN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_DWARVEN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_CHISELED_DWARVEN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_COBBLED_DWARVEN_STONE.get().asItem())
                .add(TolkienBlocks.SLAB_CRACKED_DWARVEN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_MOSSY_DWARVEN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_DWARVEN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_ELVEN_MARBLE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_CHISELED_ELVEN_MARBLE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_COBBLED_ELVEN_MARBLE.get().asItem())
                .add(TolkienBlocks.SLAB_CRACKED_ELVEN_MARBLE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_MOSSY_ELVEN_MARBLE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_ELVEN_MARBLE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_IRON_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_CHISELED_IRON_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_COBBLED_IRON_STONE.get().asItem())
                .add(TolkienBlocks.SLAB_CRACKED_IRON_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_MOSSY_IRON_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_IRON_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_ANGAINOR_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_CHISELED_ANGAINOR_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_COBBLED_ANGAINOR.get().asItem())
                .add(TolkienBlocks.SLAB_CRACKED_ANGAINOR_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_MOSSY_ANGAINOR_BRICKS.get().asItem())
                .add(TolkienBlocks.SLAB_ANGAINOR_BRICKS.get().asItem());
        tag(ItemTags.STONE_BRICKS)
                .add(TolkienBlocks.DARK_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.MOSSY_DARK_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.DARK_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.MOUNTAIN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.CHISELED_MOUNTAIN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.CRACKED_MOUNTAIN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.MOSSY_MOUNTAIN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.MOUNTAIN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.DWARVEN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.CHISELED_DWARVEN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.CRACKED_DWARVEN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.MOSSY_DWARVEN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.DWARVEN_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.ELVEN_MARBLE_BRICKS.get().asItem())
                .add(TolkienBlocks.CHISELED_ELVEN_MARBLE_BRICKS.get().asItem())
                .add(TolkienBlocks.CRACKED_ELVEN_MARBLE_BRICKS.get().asItem())
                .add(TolkienBlocks.MOSSY_ELVEN_MARBLE_BRICKS.get().asItem())
                .add(TolkienBlocks.ELVEN_MARBLE_BRICKS.get().asItem())
                .add(TolkienBlocks.IRON_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.CHISELED_IRON_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.CRACKED_IRON_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.MOSSY_IRON_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.IRON_STONE_BRICKS.get().asItem())
                .add(TolkienBlocks.ANGAINOR_BRICKS.get().asItem())
                .add(TolkienBlocks.CHISELED_ANGAINOR_BRICKS.get().asItem())
                .add(TolkienBlocks.CRACKED_ANGAINOR_BRICKS.get().asItem())
                .add(TolkienBlocks.MOSSY_ANGAINOR_BRICKS.get().asItem())
                .add(TolkienBlocks.ANGAINOR_BRICKS.get().asItem());
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Item Tags";
    }
}
