package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

import javax.annotation.Nonnull;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienParticleDescriptionProvider extends ParticleDescriptionProvider {

    protected TolkienParticleDescriptionProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper);
    }

    @Override
    protected void addDescriptions() {
        this.sprite(TolkienParticleTypes.MALLORN_FLAME.get(), ResourceLocation.fromNamespaceAndPath(MODID, "mallorn_flame"));
        this.sprite(TolkienParticleTypes.MIRKWOOD_FLAME.get(), ResourceLocation.fromNamespaceAndPath(MODID, "mirkwood_flame"));
        this.sprite(TolkienParticleTypes.CULUMALDA_FLAME.get(), ResourceLocation.fromNamespaceAndPath(MODID, "culumalda_flame"));
        this.sprite(TolkienParticleTypes.LEBETHRON_FLAME.get(), ResourceLocation.fromNamespaceAndPath(MODID, "lebethron_flame"));
        this.sprite(TolkienParticleTypes.FANGORNOAK_FLAME.get(), ResourceLocation.fromNamespaceAndPath(MODID, "fangornoak_flame"));
        this.sprite(TolkienParticleTypes.DEADWOOD_FLAME.get(), ResourceLocation.fromNamespaceAndPath(MODID, "deadwood_flame"));
        this.sprite(TolkienParticleTypes.DWARVEN_FLAME.get(), ResourceLocation.fromNamespaceAndPath(MODID, "dwarven_flame"));
        this.sprite(TolkienParticleTypes.LIGHTNINGBUG.get(), ResourceLocation.fromNamespaceAndPath(MODID, "lightningbug"));
        this.sprite(TolkienParticleTypes.FALLING_LEAVES.get(), TolkienMobsMain.prefix("falling_leaves"));
        this.sprite(TolkienParticleTypes.FELLBEAST_BREATH.get(), ResourceLocation.fromNamespaceAndPath(MODID, "fellbeast_breath"));
        this.sprite(TolkienParticleTypes.WIND_PARTICLE.get(), TolkienMobsMain.prefix("wind"));
        this.sprite(TolkienParticleTypes.WANDERING_LIGHTNINGBUG.get(), TolkienMobsMain.prefix("lightningbug"));
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Particle Types";
    }
}
