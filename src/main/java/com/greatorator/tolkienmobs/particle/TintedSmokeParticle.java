package com.greatorator.tolkienmobs.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.RandomSource;

/**
 * Vanilla's cosy campfire smoke with a colour of its own. The movement is copied from
 * {@link net.minecraft.client.particle.CampfireSmokeParticle} so that a dyed plume behaves exactly like an
 * undyed one, leaving the tint as the only difference.
 */
public class TintedSmokeParticle extends SingleQuadParticle {
    /** Campfire smoke's own size, and the size a pipe's shapes are drawn at so they can be read. */
    private static final float PLUME = 3.0F;
    private static final float DOT = 0.9F;

    private TintedSmokeParticle(
            ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
            TextureAtlasSprite sprite, float size
    ) {
        super(level, x, y, z, sprite);
        this.scale(size);
        this.setSize(0.25F, 0.25F);
        this.lifetime = this.random.nextInt(50) + 80;
        this.gravity = 3.0E-6F;
        this.xd = xSpeed;
        this.yd = ySpeed + (double) (this.random.nextFloat() / 500.0F);
        this.zd = zSpeed;
        this.setAlpha(0.9F);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
            this.xd += (double) (this.random.nextFloat() / 5000.0F * (float) (this.random.nextBoolean() ? 1 : -1));
            this.zd += (double) (this.random.nextFloat() / 5000.0F * (float) (this.random.nextBoolean() ? 1 : -1));
            this.yd -= this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.015F;
            }
        } else {
            this.remove();
        }
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    /**
     * The plume: one puff of coloured campfire smoke, at the size vanilla draws its own. A bowl or a
     * smoker gives one of these off, and so does the thin thread a pipe draws while it is lit.
     */
    public static class Provider extends SizedProvider {
        public Provider(SpriteSet sprites) {
            super(sprites, PLUME);
        }
    }

    /**
     * One dot of a blown shape, at a fraction of a puff: small enough that the gap between two points
     * of a ring still reads as a gap.
     */
    public static class DotProvider extends SizedProvider {
        public DotProvider(SpriteSet sprites) {
            super(sprites, DOT);
        }
    }

    public abstract static class SizedProvider implements ParticleProvider<ColorParticleOption> {
        private final SpriteSet sprites;
        private final float size;

        protected SizedProvider(SpriteSet sprites, float size) {
            this.sprites = sprites;
            this.size = size;
        }

        @Override
        public Particle createParticle(
                ColorParticleOption option,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed,
                RandomSource random
        ) {
            TintedSmokeParticle particle = new TintedSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,
                    this.sprites.get(random), this.size);
            particle.setColor(option.getRed(), option.getGreen(), option.getBlue());
            return particle;
        }
    }
}
