package com.greatorator.tolkienmobs.datagen.tags;

import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

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
    }
}
