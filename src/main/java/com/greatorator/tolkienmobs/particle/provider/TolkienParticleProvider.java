package com.greatorator.tolkienmobs.particle.provider;

import com.greatorator.tolkienmobs.particle.TolkienParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault

public class TolkienParticleProvider implements ParticleProvider<SimpleParticleType> {
    public SpriteSet set;

    public TolkienParticleProvider(SpriteSet pSet) {
        this.set = pSet;
    }

    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
        return new TolkienParticle(level, x, y, z, set);

    }
}
