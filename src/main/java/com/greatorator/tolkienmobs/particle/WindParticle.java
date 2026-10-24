package com.greatorator.tolkienmobs.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class WindParticle extends SingleQuadParticle {
    protected WindParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
                           double xd, double yd, double zd, TextureAtlasSprite sprite) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd, sprite);

        this.quadSize *= 1.75F;
        this.lifetime = 15 + this.random.nextInt(15);
        this.hasPhysics = false;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.xd = this.xd * 0.75F;
        this.yd = this.yd * 0.75F;
        this.zd = this.zd * 0.75F;
        fadeOut();
    }

    private void fadeOut() {
        this.alpha = (-(1/(float)lifetime) * age + 1);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz, RandomSource random) {
            return new WindParticle(level, x, y, z, dx, dy, dz, this.sprites.get(random));
        }
    }
}
