package com.greatorator.tolkienmobs.datagen.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienDamageTagProvider extends DamageTypeTagsProvider {
    public TolkienDamageTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider provider) {
        //Custom Damage Source
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Damage Source Tags";
    }
}
