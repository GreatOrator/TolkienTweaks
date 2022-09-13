package com.greatorator.tolkientweaks.datagen;

import com.greatorator.tolkientweaks.TolkienContent;
import com.greatorator.tolkientweaks.integration.IntegrationHelper;
import com.greatorator.tolkientweaks.integration.curios.Curios;
import com.greatorator.tolkientweaks.util.TTTags;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemTagGenerator extends ItemTagsProvider {
    public ItemTagGenerator(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
    super(dataGenerator, blockTagProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(TTTags.items.COINS).add(TolkienContent.ITEM_COIN_BRONZE.get(), TolkienContent.ITEM_COIN_SILVER.get(), TolkienContent.ITEM_COIN_GOLD.get(), TolkienContent.ITEM_COIN_MITHRIL.get());
//        tag(TTMTags.items.LOGS).add(TTMContent.LOG_MALLORN_ITEM.get(), TTMContent.LOG_MIRKWOOD_ITEM.get(), TTMContent.LOG_CULUMALDA_ITEM.get(), TTMContent.LOG_LEBETHRON_ITEM.get());
//            tag(ItemTags.LOGS_THAT_BURN).addTag(TTMTags.items.LOGS);
//            tag(ItemTags.LOGS).addTag(TTMTags.items.LOGS);

        if (IntegrationHelper.isCuriosInstalled) {
            Curios.generateTags(this::tag);
        }
    }
    @Nonnull
    @Override
    public String getName() {
        return "Tolkien Tweaks - Mobs Edition Item Tags";
    }
}