package com.greatorator.tolkienmobs.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienEntityTagProvider extends EntityTypeTagsProvider {
    public TolkienEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(output, provider, MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }


        @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Entity Tags";
    }
}
