package com.greatorator.tolkienmobs.handler.data;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

public record TrinketComponent(Optional<Holder<Potion>> potion, Optional<Integer> customColor, List<MobEffectInstance> customEffects) {
    public static final TrinketComponent EMPTY = new TrinketComponent(Optional.empty(), Optional.empty(), List.of());
    private static final Component NO_EFFECT;
    private static final int BASE_POTION_COLOR = 14687673;
    private static final Codec<TrinketComponent> FULL_CODEC;
    public static final Codec<TrinketComponent> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, TrinketComponent> STREAM_CODEC;

    public TrinketComponent(Holder<Potion> p_331208_) {
        this(Optional.of(p_331208_), Optional.empty(), List.of());
    }

    public TrinketComponent(Optional<Holder<Potion>> potion, Optional<Integer> customColor, List<MobEffectInstance> customEffects) {
        this.potion = potion;
        this.customColor = customColor;
        this.customEffects = customEffects;
    }

    public static ItemStack createItemStack(Item item, Holder<Potion> potion) {
        ItemStack itemstack = new ItemStack(item);
        itemstack.set(TolkienDataComponents.POTION_CONTENTS, new TrinketComponent(potion));
        return itemstack;
    }

    public boolean is(Holder<Potion> potion) {
        return this.potion.isPresent() && this.potion.get().is(potion) && this.customEffects.isEmpty();
    }

    public Iterable<MobEffectInstance> getAllEffects() {
        if (this.potion.isEmpty()) {
            return this.customEffects;
        } else {
            return this.customEffects.isEmpty() ? ((Potion)((Holder)this.potion.get()).value()).getEffects() : Iterables.concat(((Potion)((Holder)this.potion.get()).value()).getEffects(), this.customEffects);
        }
    }

    public void forEachEffect(Consumer<MobEffectInstance> action) {
        Iterator var2;
        MobEffectInstance mobeffectinstance1;
        if (this.potion.isPresent()) {
            var2 = ((Potion)((Holder)this.potion.get()).value()).getEffects().iterator();

            while(var2.hasNext()) {
                mobeffectinstance1 = (MobEffectInstance)var2.next();
                action.accept(new MobEffectInstance(mobeffectinstance1));
            }
        }

        var2 = this.customEffects.iterator();

        while(var2.hasNext()) {
            mobeffectinstance1 = (MobEffectInstance)var2.next();
            action.accept(new MobEffectInstance(mobeffectinstance1));
        }

    }

    public TrinketComponent withPotion(Holder<Potion> potion) {
        return new TrinketComponent(Optional.of(potion), this.customColor, this.customEffects);
    }

    public TrinketComponent withEffectAdded(MobEffectInstance effect) {
        return new TrinketComponent(this.potion, this.customColor, Util.copyAndAdd(this.customEffects, effect));
    }

    public int getColor() {
        return this.customColor.isPresent() ? this.customColor.get() : getColor(this.getAllEffects());
    }

    public static int getColor(Iterable<MobEffectInstance> effects) {
        return getColorOptional(effects).orElse(BASE_POTION_COLOR);
    }

    public static OptionalInt getColorOptional(Iterable<MobEffectInstance> effects) {
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        Iterator var5 = effects.iterator();

        while(var5.hasNext()) {
            MobEffectInstance mobeffectinstance = (MobEffectInstance)var5.next();
            if (mobeffectinstance.isVisible()) {
                int i1 = mobeffectinstance.getEffect().value().getColor();
                int j1 = mobeffectinstance.getAmplifier() + 1;
                i += j1 * FastColor.ARGB32.red(i1);
                j += j1 * FastColor.ARGB32.green(i1);
                k += j1 * FastColor.ARGB32.blue(i1);
                l += j1;
            }
        }

        return l == 0 ? OptionalInt.empty() : OptionalInt.of(FastColor.ARGB32.color(i / l, j / l, k / l));
    }

    public boolean hasEffects() {
        return !this.customEffects.isEmpty() || this.potion.isPresent() && !((Potion) ((Holder) this.potion.get()).value()).getEffects().isEmpty();
    }

    public List<MobEffectInstance> customEffects() {
        return Lists.transform(this.customEffects, MobEffectInstance::new);
    }

    public void addPotionTooltip(Consumer<Component> tooltipAdder, float durationFactor, float ticksPerSecond) {
        addPotionTooltip(this.getAllEffects(), tooltipAdder, durationFactor, ticksPerSecond);
    }

    public static void addPotionTooltip(Iterable<MobEffectInstance> effects, Consumer<Component> tooltipAdder, float durationFactor, float ticksPerSecond) {
        List<Pair<Holder<Attribute>, AttributeModifier>> list = Lists.newArrayList();
        boolean flag = true;

        Iterator var6;
        MutableComponent mutablecomponent;
        Holder holder;
        for(var6 = effects.iterator(); var6.hasNext(); tooltipAdder.accept(mutablecomponent.withStyle(((MobEffect)holder.value()).getCategory().getTooltipFormatting()))) {
            MobEffectInstance mobeffectinstance = (MobEffectInstance)var6.next();
            flag = false;
            mutablecomponent = Component.translatable(mobeffectinstance.getDescriptionId());
            holder = mobeffectinstance.getEffect();
            ((MobEffect)holder.value()).createModifiers(mobeffectinstance.getAmplifier(), (p_331556_, p_330860_) -> {
                list.add(new Pair(p_331556_, p_330860_));
            });
            if (mobeffectinstance.getAmplifier() > 0) {
                mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + mobeffectinstance.getAmplifier()));
            }

            if (!mobeffectinstance.endsWithin(20)) {
                mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobeffectinstance, durationFactor, ticksPerSecond));
            }
        }

        if (flag) {
            tooltipAdder.accept(NO_EFFECT);
        }

        if (!list.isEmpty()) {
            tooltipAdder.accept(CommonComponents.EMPTY);
            tooltipAdder.accept(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
            var6 = list.iterator();

            while(var6.hasNext()) {
                Pair<Holder<Attribute>, AttributeModifier> pair = (Pair)var6.next();
                AttributeModifier attributemodifier = pair.getSecond();
                double d1 = attributemodifier.amount();
                double d0;
                if (attributemodifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && attributemodifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    d0 = attributemodifier.amount();
                } else {
                    d0 = attributemodifier.amount() * 100.0;
                }

                if (d1 > 0.0) {
                    tooltipAdder.accept(Component.translatable("attribute.modifier.plus." + attributemodifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(d0), Component.translatable(((Attribute)((Holder)pair.getFirst()).value()).getDescriptionId())).withStyle(ChatFormatting.BLUE));
                } else if (d1 < 0.0) {
                    d0 *= -1.0;
                    tooltipAdder.accept(Component.translatable("attribute.modifier.take." + attributemodifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(d0), Component.translatable(((Attribute)((Holder)pair.getFirst()).value()).getDescriptionId())).withStyle(ChatFormatting.RED));
                }
            }
        }

    }

    public Optional<Holder<Potion>> potion() {
        return this.potion;
    }

    public Optional<Integer> customColor() {
        return this.customColor;
    }

    static {
        NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);
        FULL_CODEC = RecordCodecBuilder.create((p_348387_) -> {
            return p_348387_.group(Potion.CODEC.optionalFieldOf("potion").forGetter(TrinketComponent::potion), Codec.INT.optionalFieldOf("custom_color").forGetter(TrinketComponent::customColor), MobEffectInstance.CODEC.listOf().optionalFieldOf("custom_effects", List.of()).forGetter(TrinketComponent::customEffects)).apply(p_348387_, TrinketComponent::new);
        });
        CODEC = Codec.withAlternative(FULL_CODEC, Potion.CODEC, TrinketComponent::new);
        STREAM_CODEC = StreamCodec.composite(Potion.STREAM_CODEC.apply(ByteBufCodecs::optional), TrinketComponent::potion, ByteBufCodecs.INT.apply(ByteBufCodecs::optional), TrinketComponent::customColor, MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()), TrinketComponent::customEffects, TrinketComponent::new);
    }
}
