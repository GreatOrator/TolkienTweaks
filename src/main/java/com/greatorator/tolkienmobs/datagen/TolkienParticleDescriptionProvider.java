package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

import javax.annotation.Nonnull;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienParticleDescriptionProvider extends ParticleDescriptionProvider {

    protected TolkienParticleDescriptionProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void addDescriptions() {
        this.sprite(TolkienParticleTypes.MALLORN_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "mallorn_flame"));
        this.sprite(TolkienParticleTypes.MIRKWOOD_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "mirkwood_flame"));
        this.sprite(TolkienParticleTypes.CULUMALDA_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "culumalda_flame"));
        this.sprite(TolkienParticleTypes.LEBETHRON_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "lebethron_flame"));
        this.sprite(TolkienParticleTypes.FANGORNOAK_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "fangornoak_flame"));
        this.sprite(TolkienParticleTypes.DEADWOOD_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "deadwood_flame"));
        this.sprite(TolkienParticleTypes.DWARVEN_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "dwarven_flame"));
        this.sprite(TolkienParticleTypes.LIGHTNINGBUG.get(), Identifier.fromNamespaceAndPath(MODID, "lightningbug"));
        this.sprite(TolkienParticleTypes.FELLBEAST_BREATH.get(), Identifier.fromNamespaceAndPath(MODID, "fellbeast_breath"));
        this.sprite(TolkienParticleTypes.WIND_PARTICLE.get(), TolkienMobsMain.prefix("wind"));
        this.sprite(TolkienParticleTypes.WANDERING_LIGHTNINGBUG.get(), TolkienMobsMain.prefix("lightningbug"));
        this.sprite(TolkienParticleTypes.MALLORN_LEAVES.get(), TolkienMobsMain.prefix("mallorn_leaves"));
        this.sprite(TolkienParticleTypes.MIRKWOOD_LEAVES.get(), TolkienMobsMain.prefix("mirkwood_leaves"));
        this.sprite(TolkienParticleTypes.CULUMALDA_LEAVES.get(), TolkienMobsMain.prefix("culumalda_leaves"));
        this.sprite(TolkienParticleTypes.LEBETHRON_LEAVES.get(), TolkienMobsMain.prefix("lebethron_leaves"));
        this.sprite(TolkienParticleTypes.FANGORNOAK_LEAVES.get(), TolkienMobsMain.prefix("fangornoak_leaves"));
        this.sprite(TolkienParticleTypes.DWARVEN_MAPLE_LEAVES.get(), TolkienMobsMain.prefix("dwarven_maple_leaves"));
        // Tinted smoke only adds a colour to vanilla campfire smoke, so it reuses those twelve sprites
        // rather than shipping a second copy of them.
        this.spriteSet(TolkienParticleTypes.TINTED_SMOKE.get(), Identifier.withDefaultNamespace("big_smoke"), 12, false);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tolkienmobs - Particle Types";
    }
}
