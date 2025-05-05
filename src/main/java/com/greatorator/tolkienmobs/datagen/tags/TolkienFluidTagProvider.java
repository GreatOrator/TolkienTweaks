package com.greatorator.tolkienmobs.datagen.tags;

import com.greatorator.tolkienmobs.init.TolkienFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienFluidTagProvider extends FluidTagsProvider {
    public TolkienFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(FluidTags.LAVA)
                .add(TolkienFluids.MITHRIL_FLUID.get())
                .add(TolkienFluids.MITHRIL_FLOWING.get())
                .add(TolkienFluids.MORGULIRON_FLUID.get())
                .add(TolkienFluids.MORGULIRON_FLOWING.get());
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Fluid Tags";
    }
}