package com.greatorator.tolkienmobs.handler;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Tracks a handful of fields on one entity so they are written and read together. It sits on
 * top of the entity's own ValueOutput / ValueInput now, so each bridge knows how to store its
 * type and how to tell whether the key was present at all.
 */
public class EntityHandler<E extends Entity> {
    public static final NBTBridge<Boolean> BOOL = bridge(
            (key, output, value) -> output.putBoolean(key, value),
            (key, input) -> input.getBooleanOr(key, false),
            (key, input) -> input.read(key, Codec.BOOL).isPresent());
    public static final NBTBridge<Integer> INT = bridge(
            (key, output, value) -> output.putInt(key, value),
            (key, input) -> input.getIntOr(key, 0),
            (key, input) -> input.getInt(key).isPresent());
    public static final NBTBridge<String> STRING = bridge(
            (key, output, value) -> output.putString(key, value),
            (key, input) -> input.getStringOr(key, ""),
            (key, input) -> input.getString(key).isPresent());
    public static final NBTBridge<CompoundTag> NBT = bridge(
            (key, output, value) -> output.store(key, CompoundTag.CODEC, value),
            (key, input) -> input.read(key, CompoundTag.CODEC).orElseGet(CompoundTag::new),
            (key, input) -> input.child(key).isPresent());
    public static final NBTBridge<BlockPos> POS = bridge(
            (key, output, value) -> output.putLong(key, value.asLong()),
            (key, input) -> BlockPos.of(input.getLongOr(key, 0L)),
            (key, input) -> input.getLong(key).isPresent());

    final Entry<?, E>[] entries;

    private EntityHandler(Entry<?, E>[] entries)
    {
        this.entries = entries;
    }

    public void serialize(E entity, ValueOutput output)
    {
        for (Entry<?, E> entry : entries) entry.serialize(entity, output);
    }

    public void deserialize(E entity, ValueInput input)
    {
        for (Entry<?, E> entry : entries) entry.deserialize(entity, input);
    }

    @SuppressWarnings("unchecked")
    public <T extends E> EntityHandler<T> concat(Consumer<Builder<T>> consumer)
    {
        Builder<T> builder = new Builder<>();
        consumer.accept(builder);
        return new EntityHandler<>(ArrayUtils.addAll((Entry<?, T>[]) entries, builder.build()));
    }

    public static <E extends Entity> EntityHandler<E> builder(Consumer<Builder<E>> consumer)
    {
        Builder<E> builder = new Builder<>();
        consumer.accept(builder);
        return new EntityHandler<>(builder.build());
    }

    private static <T> NBTBridge<T> bridge(TriConsumer<String, ValueOutput, T> setter,
                                           BiFunction<String, ValueInput, T> getter,
                                           BiPredicate<String, ValueInput> exists)
    {
        return new NBTBridge<>(setter, getter, exists);
    }

    public static class Entry<T, E extends Entity>
    {
        private final NBTBridge<T> bridge;
        private final String key;
        private final Function<E, T> write;
        private final BiConsumer<E, T> read;

        public Entry(NBTBridge<T> type, String key, Function<E, T> write, BiConsumer<E, T> read)
        {
            this.bridge = type;
            this.key = key;
            this.write = write;
            this.read = read;
        }

        private void serialize(E entity, ValueOutput output)
        {
            bridge.setter.accept(key, output, write.apply(entity));
        }

        private void deserialize(E entity, ValueInput input)
        {
            read.accept(entity, bridge.getter.apply(key, input));
        }
    }

    public static class NBTBridge<T>
    {
        private final TriConsumer<String, ValueOutput, T> setter;
        private final BiFunction<String, ValueInput, T> getter;
        private final BiPredicate<String, ValueInput> exists;
        private NBTBridge<Optional<T>> optional;

        private NBTBridge(TriConsumer<String, ValueOutput, T> setter, BiFunction<String, ValueInput, T> getter,
                          BiPredicate<String, ValueInput> exists)
        {
            this.setter = setter;
            this.getter = getter;
            this.exists = exists;
        }

        public NBTBridge<Optional<T>> optional()
        {
            if (optional == null) // may not always be needed, so initalize lazily.
            {
                return optional = new NBTBridge<>(
                        (key, output, value) -> value.ifPresent(v -> setter.accept(key, output, v)),
                        (key, input) -> exists.test(key, input) ? Optional.of(getter.apply(key, input)) : Optional.empty(),
                        (key, input) -> exists.test(key, input));
            }
            return optional;
        }
    }

    public static class Builder<E extends Entity>
    {
        private final List<Entry<?, E>> entries = new ArrayList<>();

        public <T> Builder<E> track(NBTBridge<T> bridge, String key, Function<E, T> write, BiConsumer<E, T> read)
        {
            entries.add(new Entry<>(bridge, key, write, read));
            return this;
        }

        @SuppressWarnings("unchecked")
        private Entry<?, E>[] build()
        {
            return entries.toArray(new Entry[0]);
        }
    }
}
