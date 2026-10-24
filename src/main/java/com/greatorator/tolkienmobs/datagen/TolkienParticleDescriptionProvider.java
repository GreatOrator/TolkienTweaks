package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.data.ParticleDescriptionProvider;

import javax.annotation.Nonnull;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienParticleDescriptionProvider extends ParticleDescriptionProvider {

    protected TolkienParticleDescriptionProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void addDescriptions() {
        // 26.1 dropped the single-sprite helper; a one-texture sprite set writes the same description.
        this.spriteSet(TolkienParticleTypes.MALLORN_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "mallorn_flame"));
        this.spriteSet(TolkienParticleTypes.MIRKWOOD_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "mirkwood_flame"));
        this.spriteSet(TolkienParticleTypes.CULUMALDA_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "culumalda_flame"));
        this.spriteSet(TolkienParticleTypes.LEBETHRON_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "lebethron_flame"));
        this.spriteSet(TolkienParticleTypes.FANGORNOAK_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "fangornoak_flame"));
        this.spriteSet(TolkienParticleTypes.DEADWOOD_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "deadwood_flame"));
        this.spriteSet(TolkienParticleTypes.DWARVEN_FLAME.get(), Identifier.fromNamespaceAndPath(MODID, "dwarven_flame"));
        this.spriteSet(TolkienParticleTypes.LIGHTNINGBUG.get(), Identifier.fromNamespaceAndPath(MODID, "lightningbug"));
        this.spriteSet(TolkienParticleTypes.FELLBEAST_BREATH.get(), Identifier.fromNamespaceAndPath(MODID, "fellbeast_breath"));
        this.spriteSet(TolkienParticleTypes.WIND_PARTICLE.get(), TolkienMobsMain.prefix("wind"));
        this.spriteSet(TolkienParticleTypes.WANDERING_LIGHTNINGBUG.get(), TolkienMobsMain.prefix("lightningbug"));
        this.spriteSet(TolkienParticleTypes.MALLORN_LEAVES.get(), TolkienMobsMain.prefix("mallorn_leaves"));
        this.spriteSet(TolkienParticleTypes.MIRKWOOD_LEAVES.get(), TolkienMobsMain.prefix("mirkwood_leaves"));
        this.spriteSet(TolkienParticleTypes.CULUMALDA_LEAVES.get(), TolkienMobsMain.prefix("culumalda_leaves"));
        this.spriteSet(TolkienParticleTypes.LEBETHRON_LEAVES.get(), TolkienMobsMain.prefix("lebethron_leaves"));
        this.spriteSet(TolkienParticleTypes.FANGORNOAK_LEAVES.get(), TolkienMobsMain.prefix("fangornoak_leaves"));
        this.spriteSet(TolkienParticleTypes.DWARVEN_MAPLE_LEAVES.get(), TolkienMobsMain.prefix("dwarven_maple_leaves"));
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
