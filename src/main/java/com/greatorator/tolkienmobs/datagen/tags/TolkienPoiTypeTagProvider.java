package com.greatorator.tolkienmobs.datagen.tags;

import com.greatorator.tolkienmobs.init.TolkienVillagers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienPoiTypeTagProvider extends PoiTypeTagsProvider {
    public TolkienPoiTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        TagAppender<PoiType> builder = tag(PoiTypeTags.ACQUIRABLE_JOB_SITE);
        for(var entry : TolkienVillagers.POI_TYPES.getEntries())
            builder.add(entry.getKey());
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - POI Type Tags";
    }
}