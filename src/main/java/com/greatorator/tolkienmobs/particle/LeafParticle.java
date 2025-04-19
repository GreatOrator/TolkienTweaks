package com.greatorator.tolkienmobs.particle;

import com.greatorator.tolkienmobs.particle.data.LeafParticleData;
import com.greatorator.tolkienmobs.util.BreezeUtility;
import com.greatorator.tolkienmobs.util.MathUtility;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class LeafParticle extends TextureSheetParticle {
    protected static final float TAU = (float) (2 * Math.PI); // 1 rotation

    protected static final int FADE_DURATION = 16; // ticks
    protected static final double WATER_FRICTION = 0.05;

    protected final float windCoefficient; // to emulate drag/lift
    private float rot;

    protected final float maxRotateSpeed; // rotations / tick
    protected final int maxRotateTime;
    protected int rotateTime = 0;

    protected LeafParticle(ClientLevel clientWorld, double x, double y, double z, double r, double g, double b) {
        super(clientWorld, x, y, z, 0, 0, 0);


        this.gravity = 0.08f + random.nextFloat() * 0.04f;
        this.windCoefficient = 0.6f + random.nextFloat() * 0.4f;

        // the Particle constructor adds random noise to the velocity which we don't want
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;

        this.hasPhysics = true;
        this.lifetime = MathUtility.getRandomInteger(200, 100);

        this.rCol = Mth.nextFloat(this.random, 0.1529411F, 0.7490196F);
        this.gCol = Mth.nextFloat(this.random, 0.6431372F, 0.8627450F);
        this.bCol = Mth.nextFloat(this.random, 0.2196078F, 0.2823529F);
        // accelerate over 3-7 seconds to at most 2.5 rotations per second
        this.maxRotateTime = (3 + random.nextInt(4 + 1)) * 20;
        this.maxRotateSpeed = (random.nextBoolean() ? -1 : 1) * (0.1f + 2.4f * random.nextFloat()) * TAU / 20f;

        this.roll = this.oRoll = random.nextFloat() * TAU;

        this.quadSize = MathUtility.getRandomInteger(4, 1) / 50f;
    }

    @Override
    public void tick() {
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.oRoll = this.roll;

        age++;

        // fade-out animation
        if (age >= lifetime + 1 - FADE_DURATION) {
            this.alpha -= 1F / FADE_DURATION;
        }

        if (age >= lifetime) {
            this.remove();
            return;
        }

        if (this.level.getFluidState(new BlockPos((int) x, (int) y, (int) z)).is(FluidTags.WATER)) {
            // float on water
            yd = 0.0;
            rotateTime = 0;

            xd *= (1 - WATER_FRICTION);
            zd *= (1 - WATER_FRICTION);
        } else {
            // apply gravity
            yd -= 0.04 * gravity;

            if (!onGround) {
                // spin when in the air
                rotateTime = Math.min(rotateTime + 1, maxRotateTime);
                this.roll += (rotateTime / (float) maxRotateTime) * maxRotateSpeed;
            } else {
                rotateTime = 0;
            }

            // approach the target wind velocity over time via vel += (target - vel) * f, where f is in (0, 1)
            // after n ticks, the distance closes to a factor of 1 - (1 - f)^n.
            // for f = 1 / 2, it would only take 4 ticks to close the distance by 90%
            // for f = 1 / 60, it takes ~2 seconds to halve the distance, ~5 seconds to reach 80%
            //
            // the wind coefficient is just another factor in (0, 1) to add some variance between leaves.
            // this implementation lags behind the actual wind speed and will never reach it fully,
            // so wind speeds needs to be adjusted accordingly
            xd += (BreezeUtility.windX - xd) * windCoefficient / 60.0f;
            zd += (BreezeUtility.windZ - zd) * windCoefficient / 60.0f;
        }

        move(xd, yd, zd);
    }

    @Override
    public void render(VertexConsumer buffer, Camera entity, float partialTicks) {
        this.alpha = Math.min(Mth.clamp(this.age, 0, 20) / 20.0F, Mth.clamp(this.lifetime - this.age, 0, 20) / 20.0F);
        Quaternionf quaternion = new Quaternionf();
        if (this.roll != 0.0F) {
            quaternion.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
        }
        quaternion.rotateY(Mth.cos((float) Math.toRadians(this.rot % 360.0F)));
        this.renderRotatedQuad(buffer, entity, quaternion, partialTicks);
        quaternion.rotateY(-Mth.PI).rotateZ(Mth.HALF_PI);
        this.renderRotatedQuad(buffer, entity, quaternion, partialTicks);
    }

    @Override
    public int getLightColor(float partialTicks) {
        return 240 | 240 << 16;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Factory(SpriteSet sprite) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            LeafParticle particle = new LeafParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
//            particle.setColor(data.r() / 255.0F, data.g() / 255.0F, data.b() / 255.0F);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}
