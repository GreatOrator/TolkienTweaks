package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.particle.data.LeafParticleData;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(
            BuiltInRegistries.PARTICLE_TYPE, MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MALLORN_FLAME = PARTICLES.register("mallorn_flame", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MIRKWOOD_FLAME = PARTICLES.register("mirkwood_flame", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CULUMALDA_FLAME = PARTICLES.register("culumalda_flame", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LEBETHRON_FLAME = PARTICLES.register("lebethron_flame", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FANGORNOAK_FLAME = PARTICLES.register("fangornoak_flame", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DEADWOOD_FLAME = PARTICLES.register("deadwood_flame", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DWARVEN_FLAME = PARTICLES.register("dwarven_flame", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIGHTNINGBUG = PARTICLES.register("lightningbug", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WANDERING_LIGHTNINGBUG = PARTICLES.register("wandering_lightningbug", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, ParticleType<LeafParticleData>> FALLING_LEAVES = PARTICLES.register("falling_leaves", () -> new ParticleType<>(false) {
        @Override
        public MapCodec<LeafParticleData> codec() {
            return LeafParticleData.CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, LeafParticleData> streamCodec() {
            return LeafParticleData.STREAM_CODEC;
        }
    });

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}