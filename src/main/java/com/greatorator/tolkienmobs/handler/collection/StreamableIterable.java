package com.greatorator.tolkienmobs.handler.collection;

import com.google.common.collect.*;
import com.greatorator.tolkienmobs.handler.annotation.ReplaceWith;
import com.greatorator.tolkienmobs.handler.annotation.Requires;
import com.greatorator.tolkienmobs.util.SneakyUtils;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.greatorator.tolkienmobs.util.SneakyUtils.unsafeCast;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Deprecated
@ScheduledForRemoval (inVersion = "0.6.0")
@ReplaceWith("Use FastStream. Built from the ground up and much faster. Also does not require Guava for base operation.")
@Requires("com.google.guava:guava")
public interface StreamableIterable<T> extends Iterable<T> {

    StreamableIterable<?> EMPTY = of(Collections.emptyList());

    static <T> StreamableIterable<T> empty() {
        return unsafeCast(EMPTY);
    }

    @SuppressWarnings ("unchecked")
    static <T> StreamableIterable<T> of(Iterable<? extends T> itr) {
        if (itr instanceof StreamableIterable) return (StreamableIterable<T>) itr;

        return ((Iterable<T>) itr)::iterator;
    }

    static <T> StreamableIterable<T> of() {
        return unsafeCast(EMPTY);
    }

    static <T> StreamableIterable<T> of(T thing) {
        return of(singletonList(thing));
    }

    static <T> StreamableIterable<T> ofNullable(@Nullable T thing) {
        if (thing == null) return empty();
        return of(thing);
    }

    static <T> StreamableIterable<T> of(Optional<? extends T> optional) {
        return optional.map(StreamableIterable::<T>of).orElse(empty());
    }

    @SafeVarargs
    static <T> StreamableIterable<T> of(T... things) {
        return of(asList(things));
    }

    @SafeVarargs
    static <T> StreamableIterable<T> concat(Iterable<? extends T>... iterables) {
        return of(Iterables.concat(iterables));
    }

    static <T> StreamableIterable<T> concatMany(Iterable<? extends Iterable<? extends T>> iterables) {
        return of(Iterables.concat(iterables));
    }

    default StreamableIterable<T> concat(Iterable<? extends T> other) {
        return concat(this, other);
    }

    default StreamableIterable<T> filter(Predicate<? super T> pred) {
        return () -> new AbstractIterator<T>() {
            private final Iterator<T> itr = iterator();

            @Override
            protected T computeNext() {
                while (itr.hasNext()) {
                    T e = itr.next();
                    if (pred.test(e)) {
                        return e;
                    }
                }
                return endOfData();
            }
        };
    }

    default StreamableIterable<T> filterNot(Predicate<? super T> pred) {
        return filter(pred.negate());
    }

    default <R> StreamableIterable<R> map(Function<? super T, ? extends R> func) {
        return () -> new Iterator<R>() {
            private final Iterator<T> itr = iterator();

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public R next() {
                return func.apply(itr.next());
            }

            @Override
            public void remove() {
                itr.remove();
            }
        };
    }

    default <R> StreamableIterable<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> func) {
        return () -> new AbstractIterator<R>() {
            private final Iterator<T> itr = iterator();
            @Nullable
            Iterator<? extends R> working = null;

            @Override
            protected R computeNext() {
                while (true) {
                    if (working == null) {
                        if (itr.hasNext()) {
                            working = func.apply(itr.next()).iterator();
                        } else {
                            break;
                        }
                    }
                    if (working.hasNext()) {
                        return working.next();
                    }
                    working = null;
                }
                return endOfData();
            }
        };
    }

    default StreamableIterable<T> distinct() {
        Set<T> set = new HashSet<>();
        return filter(set::add);
    }

    default StreamableIterable<T> peek(Consumer<? super T> action) {
        return () -> new Iterator<T>() {
            private final Iterator<T> itr = iterator();

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public T next() {
                T n = itr.next();
                action.accept(n);
                return n;
            }
        };
    }

    default StreamableIterable<T> limit(@Range (from = -1, to = Integer.MAX_VALUE) int max) {
        // No point doing filtering.
        if (max == -1) return this;
        if (max == 0) return Collections::emptyIterator;

        return () -> new AbstractIterator<T>() {
            private final Iterator<T> itr = iterator();
            private int count = 0;

            @Override
            protected T computeNext() {
                if (!itr.hasNext()) return endOfData();
                if (count++ >= max) return endOfData();
                return itr.next();
            }
        };
    }

    default StreamableIterable<T> skip(@Range (from = 0, to = Integer.MAX_VALUE) int n) {
        if (n == 0) return this;

        return () -> new AbstractIterator<T>() {
            private final Iterator<T> itr = iterator();
            private int count = 0;

            @Override
            protected T computeNext() {
                while (itr.hasNext()) {
                    T next = itr.next();
                    if (count++ < n) {
                        continue;
                    }
                    return next;
                }
                return endOfData();
            }
        };
    }

    default Object[] toArray() {
        return toList().toArray();
    }

    default T[] toArray(T[] arr) {
        return toList().toArray(arr);
    }

    @Nullable
    @Contract ("!null,_ -> !null")
    @Deprecated
    @ScheduledForRemoval (inVersion = "0.5.0")
    default T fold(@Nullable T identity, BinaryOperator<@Nullable T> accumulator) {
        return fold(identity, (BiFunction<@Nullable T, T, T>) accumulator);
    }

    @Nullable
    @Contract ("!null,_ -> !null")
    default <U> U fold(@Nullable U identity, BiFunction<? super @Nullable U, ? super T, ? extends U> accumulator) {
        U ret = identity;
        for (T t : this) {
            ret = accumulator.apply(ret, t);
        }
        return ret;
    }

    default Optional<T> fold(BinaryOperator<T> accumulator) {
        boolean found = false;
        T ret = null;
        for (T t : this) {
            if (found) {
                ret = accumulator.apply(ret, t);
            } else {
                ret = t;
            }
            found = true;
        }
        return found ? Optional.ofNullable(ret) : Optional.empty();
    }

    default int count() {
        int i = 0;
        for (T ignored : this) {
            i++;
        }
        return i;
    }

    default int intSum(ToIntFunction<? super T> func) {
        int i = 0;
        for (T thing : this) {
            i += func.applyAsInt(thing);
        }
        return i;
    }

    default long longSum(ToLongFunction<? super T> func) {
        long l = 0;
        for (T thing : this) {
            l += func.applyAsLong(thing);
        }
        return l;
    }

    default double doubleSum(ToDoubleFunction<? super T> func) {
        double d = 0;
        for (T thing : this) {
            d += func.applyAsDouble(thing);
        }
        return d;
    }

    default boolean isEmpty() {
        return !iterator().hasNext();
    }

    default boolean anyMatch(Predicate<? super T> test) {
        for (T t : this) {
            if (test.test(t)) return true;
        }
        return false;
    }

    default boolean allMatch(Predicate<? super T> test) {
        for (T t : this) {
            if (!test.test(t)) return false;
        }
        return true;
    }

    default boolean noneMatch(Predicate<? super T> test) {
        for (T t : this) {
            if (test.test(t)) return false;
        }
        return true;
    }

    default Optional<T> findFirst() {
        return ColUtils.headOption(this);
    }

    default T first() {
        return ColUtils.head(this);
    }

    @Nullable
    default T firstOrDefault() {
        return ColUtils.headOrDefault(this);
    }

    @Nullable
    @Contract ("!null -> !null")
    default T firstOrDefault(@Nullable T _default) {
        return ColUtils.headOrDefault(this, _default);
    }

    default Optional<T> findLast() {
        return ColUtils.tailOption(this);
    }

    default T last() {
        return ColUtils.tail(this);
    }

    @Nullable
    default T lastOrDefault() {
        return ColUtils.tailOrDefault(this);
    }

    @Nullable
    @Contract ("!null -> !null")
    default T lastOrDefault(@Nullable T _default) {
        return ColUtils.tailOrDefault(this, _default);
    }

    default T only() {
        return ColUtils.only(this);
    }

    @Nullable
    default T onlyOrDefault() {
        return ColUtils.onlyOrDefault(this, null);
    }

    @Nullable
    @Contract ("!null -> !null")
    default T onlyOrDefault(@Nullable T _default) {
        return ColUtils.onlyOrDefault(this, _default);
    }

    default T maxBy(ToIntFunction<T> func) {
        return ColUtils.requireMaxBy(this, func);
    }

    @Nullable
    default T maxByOrDefault(ToIntFunction<T> func) {
        return maxByOrDefault(func, null);
    }

    @Nullable
    @Contract ("_,!null -> !null")
    default T maxByOrDefault(ToIntFunction<T> func, @Nullable T _default) {
        return ColUtils.maxByOrDefault(this, func, _default);
    }

    default ArrayList<T> toList() {
        return Lists.newArrayList(this);
    }

    default LinkedList<T> toLinkedList() {
        return Lists.newLinkedList(this);
    }

    default ImmutableList<T> toImmutableList() {
        return ImmutableList.copyOf(this);
    }

    default HashSet<T> toSet() {
        return Sets.newHashSet(this);
    }

    default LinkedHashSet<T> toLinkedHashSet() {
        return Sets.newLinkedHashSet(this);
    }

    default ImmutableSet<T> toImmutableSet() {
        return ImmutableSet.copyOf(this);
    }

    default <K, V> HashMap<K, V> toMap(Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return toMap(new HashMap<>(), keyFunc, valueFunc);
    }

    default <K, V> HashMap<K, V> toMap(Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunc) {
        return toMap(new HashMap<>(), keyFunc, valueFunc, mergeFunc);
    }

    default <K, V> HashMap<K, V> toLinkedHashMap(Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return toMap(new LinkedHashMap<>(), keyFunc, valueFunc);
    }

    default <K, V> HashMap<K, V> toLinkedHashMap(Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunc) {
        return toMap(new LinkedHashMap<>(), keyFunc, valueFunc, mergeFunc);
    }

    default <K, V> ImmutableMap<K, V> toImmutableMap(Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return ImmutableMap.copyOf(toLinkedHashMap(keyFunc, valueFunc));
    }

    default <K, V> ImmutableMap<K, V> toImmutableMap(Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunc) {
        return ImmutableMap.copyOf(toLinkedHashMap(keyFunc, valueFunc, mergeFunc));
    }

    default <K, V, M extends Map<K, V>> M toMap(M map, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return toMap(map, keyFunc, valueFunc, SneakyUtils.first());
    }

    default <K, V, M extends Map<K, V>> M toMap(M map, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunc) {
        for (T t : this) {
            K key = keyFunc.apply(t);
            V value = valueFunc.apply(t);
            V existing = map.get(key);
            if (existing == null) {
                map.put(key, valueFunc.apply(t));
            } else {
                map.put(key, mergeFunc.apply(existing, value));
            }
        }
        return map;
    }

    default <K> HashMap<K, List<T>> groupBy(Function<T, K> keyFunc) {
        return groupBy(keyFunc, Function.identity());
    }

    default <K, V> HashMap<K, List<V>> groupBy(Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return groupBy(new HashMap<>(), ArrayList::new, keyFunc, valueFunc);
    }

    default <K> LinkedHashMap<K, List<T>> groupByLinked(Function<T, K> keyFunc) {
        return groupByLinked(keyFunc, Function.identity());
    }

    default <K, V> LinkedHashMap<K, List<V>> groupByLinked(Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return groupBy(new LinkedHashMap<>(), LinkedList::new, keyFunc, valueFunc);
    }

    default <K> ImmutableMap<K, List<T>> groupByImmutable(Function<T, K> keyFunc) {
        return groupByImmutable(keyFunc, Function.identity());
    }

    default <K, V> ImmutableMap<K, List<V>> groupByImmutable(Function<T, K> keyFunc, Function<T, V> valueFunc) {
        ImmutableMap.Builder<K, List<V>> builder = ImmutableMap.builder();
        for (Map.Entry<K, List<V>> entry : groupByLinked(keyFunc, valueFunc).entrySet()) {
            builder.put(entry.getKey(), ImmutableList.copyOf(entry.getValue()));
        }
        return builder.build();
    }

    default <K, V, L extends List<V>, M extends Map<K, L>> M groupBy(M map, Supplier<L> listFactory, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        for (T t : this) {
            K key = keyFunc.apply(t);
            L list = map.computeIfAbsent(key, k -> listFactory.get());
            list.add(valueFunc.apply(t));
        }
        return map;
    }

    default String join(String sep) {
        StringBuilder str = new StringBuilder();
        boolean first = true;
        for (T t : this) {
            if (!first) {
                str.append(sep);
            }
            first = false;
            str.append(t);
        }

        return str.toString();
    }

    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    default Stream<T> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}
