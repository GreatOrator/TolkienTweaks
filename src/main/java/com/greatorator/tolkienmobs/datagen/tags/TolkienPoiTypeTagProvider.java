package com.greatorator.tolkienmobs.datagen.tags;

import com.greatorator.tolkienmobs.init.TolkienVillagers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienPoiTypeTagProvider extends PoiTypeTagsProvider {
    public TolkienPoiTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        // 26.1 keys a POI tag by ResourceKey: TagAppender changed from one type parameter to two.
        TagAppender<ResourceKey<PoiType>, PoiType> builder = tag(PoiTypeTags.ACQUIRABLE_JOB_SITE);
        for(var entry : TolkienVillagers.POI_TYPES.getEntries())
            builder.add(entry.getKey());
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - POI Type Tags";
    }
}