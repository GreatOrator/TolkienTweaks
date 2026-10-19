package com.greatorator.tolkienmobs.world.components.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Map;

public record NamespacedSurfaceRuleSource(SurfaceRules.RuleSource base, Map<String, SurfaceRules.RuleSource> sources) implements SurfaceRules.RuleSource
{
    public static final KeyDispatchDataCodec<NamespacedSurfaceRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((builder) ->
    {
        return builder.group(
            SurfaceRules.RuleSource.CODEC.fieldOf("base").forGetter(NamespacedSurfaceRuleSource::base),
            Codec.unboundedMap(Codec.STRING, SurfaceRules.RuleSource.CODEC).fieldOf("sources").forGetter(NamespacedSurfaceRuleSource::sources)
        ).apply(builder, NamespacedSurfaceRuleSource::new);
    }));

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context)
    {
        ImmutableMap.Builder<String, SurfaceRules.SurfaceRule> rules = new ImmutableMap.Builder<>();
        this.sources.entrySet().forEach(entry -> rules.put(entry.getKey(), entry.getValue().apply(context)));
        return new NamespacedRule(context, this.base.apply(context), rules.build());
    }

    record NamespacedRule(SurfaceRules.Context context, SurfaceRules.SurfaceRule baseRule, Map<String, SurfaceRules.SurfaceRule> rules) implements SurfaceRules.SurfaceRule
    {
        public BlockState tryApply(int x, int y, int z)
        {
            Holder<Biome> biome = context.biome.get();
            BlockState state = null;

            if (biome.is(key -> this.rules.containsKey(key.location().getNamespace())))
                state = this.rules.get(biome.unwrapKey().get().location().getNamespace()).tryApply(x, y, z);

            if (state == null)
                state = this.baseRule.tryApply(x, y, z);

            return state;
        }
    }
}
