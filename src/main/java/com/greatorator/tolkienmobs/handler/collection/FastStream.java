package com.greatorator.tolkienmobs.handler.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.greatorator.tolkienmobs.handler.annotation.Requires;
import com.greatorator.tolkienmobs.util.SneakyUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import static com.greatorator.tolkienmobs.util.SneakyUtils.unsafeCast;

public interface FastStream<T> extends Iterable<T> {

    FastStream<?> EMPTY = new Internal.Empty<>();

    static <T> FastStream<T> empty() {
        return unsafeCast(EMPTY);
    }

    static <T> FastStream<T> of() {
        return empty();
    }

    @SuppressWarnings ("unchecked")
    static <T> FastStream<T> of(Iterable<? extends T> itr) {
        if (itr instanceof FastStream) return (FastStream<T>) itr;

        int knownLength = Internal.knownLength(itr, false);
        if (knownLength == 0) return empty();

        return new Wrapped<>((Iterable<T>) itr, knownLength);
    }

    @SuppressWarnings ("unchecked")
    static <T> FastStream<T> of(Spliterator<? extends T> itr) {
        long exactSize = itr.getExactSizeIfKnown();
        if (exactSize == 0) return empty();
        // We can't express streams with more elements than Int.MAX here.
        // If this is ever actually used, I would be shocked.
        if (exactSize > Integer.MAX_VALUE) {
            exactSize = -1;
        }

        return new WrappedSpl<>((Spliterator<T>) itr, (int) exactSize);
    }

    static <T> FastStream<T> of(Stream<? extends T> stream) {
        return of(stream.spliterator());
    }

    static <T> FastStream<T> of(T thing) {
        return new OfSingle<>(thing);
    }

    static <T> FastStream<T> ofNullable(@Nullable T thing) {
        return thing != null ? of(thing) : empty();
    }

    static <T> FastStream<T> of(Optional<? extends T> opt) {
        return ofNullable(opt.orElse(null));
    }

    @SafeVarargs
    static <T> FastStream<T> of(T... things) {
        if (things.length == 0) return empty();

        return new OfN<>(things);
    }

    @SafeVarargs
    static <T> FastStream<T> concat(Iterable<? extends T>... iterables) {
        if (iterables.length == 0) return empty();
        if (iterables.length == 1) return of(iterables[0]);

        int nonEmptyCount = 0;
        Iterable<? extends T> lastNonEmpty = null;
        for (Iterable<? extends T> iterable : iterables) {
            if (Internal.knownLength(iterable, false) != 0) {
                nonEmptyCount++;
                lastNonEmpty = iterable;
            }
        }
        if (nonEmptyCount == 0) return empty();
        if (nonEmptyCount == 1) return of(lastNonEmpty);

        return new ConcatenatedN<>(iterables);
    }

    static <T> FastStream<T> concatMany(Iterable<? extends Iterable<? extends T>> iterables) {
        return new Concatenated<>(iterables);
    }

    default FastStream<T> concat(Iterable<? extends T> other) {
        if (other == EMPTY) return this;

        return concat(this, other);
    }

    default FastStream<T> filter(Predicate<? super T> pred) {
        return new Filtered<>(this, pred);
    }

    default FastStream<T> filterNot(Predicate<? super T> pred) {
        return new Filtered<>(this, pred.negate());
    }

    default <R> FastStream<R> map(Function<? super T, ? extends R> func) {
        return new Mapped<>(this, func);
    }

    default <R> FastStream<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> func) {
        return new FlatMapped<>(this, func);
    }

    default FastStream<T> distinct() {
        return new Distinct<>(this);
    }

    default <K> FastStream<Group<K, T>> groupBy(Function<? super T, ? extends K> keyFunc) {
        return new Grouped<>(this, keyFunc, Function.identity());
    }

    default <K, V> FastStream<Group<K, V>> groupBy(Function<? super T, ? extends K> keyFunc, Function<? super T, ? extends V> valueFunc) {
        return new Grouped<>(this, keyFunc, valueFunc);
    }

    default FastStream<T> sorted() {
        return new Sorted<>(this, unsafeCast(Comparator.naturalOrder()));
    }

    default FastStream<T> sorted(Comparator<? super T> comparator) {
        return new Sorted<>(this, comparator);
    }

    default FastStream<T> reversed() {
        return new Reversed<>(this);
    }

    default FastStream<T> peek(Consumer<? super T> cons) {
        return new Peeked<>(this, cons);
    }

    default FastStream<T> limit(@Range (from = -1, to = Integer.MAX_VALUE) int max) {
        if (max == -1) return this;
        if (max <= 0) return empty();

        return new Sliced<>(this, 0, max);
    }

    default FastStream<T> skip(@Range (from = 0, to = Integer.MAX_VALUE) int n) {
        if (n == 0) return this;

        return new Sliced<>(this, n, Integer.MAX_VALUE);
    }

    default boolean anyMatch(Predicate<? super T> pred) {
        try {
            forEach(e -> {
                if (pred.test(e)) {
                    throw new ForEachAbort();
                }
            });
        } catch (ForEachAbort ignored) {
            return true;
        }
        return false;
    }

    default boolean allMatch(Predicate<? super T> pred) {
        return !anyMatch(pred.negate());
    }

    default boolean noneMatch(Predicate<? super T> pred) {
        return allMatch(pred.negate());
    }

    default boolean isEmpty() {
        return knownLength() == 0 || !iterator().hasNext();
    }

    default int knownLength() {
        return knownLength(false);
    }

    default int knownLength(boolean consumeToCalculate) {
        return -1;
    }

    default int count() {
        int len = knownLength(true);
        if (len >= 0) return len;

        final class Cons implements Consumer<T> {

            private int count;

            @Override
            public void accept(T t) {
                count++;
            }
        }
        Cons cons = new Cons();
        forEach(cons);
        return cons.count;
    }

    @Nullable
    @Contract ("!null,_ -> !null")
    default <U> U fold(@Nullable U identity, BiFunction<? super @Nullable U, ? super T, ? extends U> accumulator) {
        final class Cons implements Consumer<T> {

            @Nullable
            U ret = identity;

            @Override
            public void accept(T t) {
                ret = accumulator.apply(ret, t);
            }
        }
        Cons cons = new Cons();
        forEach(cons);
        return cons.ret;
    }

    default Optional<T> fold(BinaryOperator<T> accumulator) {
        final class Cons implements Consumer<T> {

            @Nullable
            T ret = null;
            boolean found = false;

            @Override
            public void accept(T t) {
                if (!found) {
                    ret = t;
                } else {
                    ret = accumulator.apply(ret, t);
                }
                found = true;
            }
        }
        Cons cons = new Cons();
        forEach(cons);
        return cons.found ? Optional.ofNullable(cons.ret) : Optional.empty();
    }

    default int intSum(ToIntFunction<? super T> func) {
        final class Cons implements Consumer<T> {

            private int sum;

            @Override
            public void accept(T t) {
                sum += func.applyAsInt(t);
            }
        }
        Cons cons = new Cons();
        forEach(cons);
        return cons.sum;
    }

    default long longSum(ToLongFunction<? super T> func) {
        final class Cons implements Consumer<T> {

            private long sum;

            @Override
            public void accept(T t) {
                sum += func.applyAsLong(t);
            }
        }
        Cons cons = new Cons();
        forEach(cons);
        return cons.sum;
    }

    default double doubleSum(ToDoubleFunction<? super T> func) {
        final class Cons implements Consumer<T> {

            private double sum;

            @Override
            public void accept(T t) {
                sum += func.applyAsDouble(t);
            }
        }
        Cons cons = new Cons();
        forEach(cons);
        return cons.sum;
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
        return onlyOrDefault(null);
    }

    @Nullable
    @Contract ("!null->!null")
    default T onlyOrDefault(@Nullable T _default) {
        return ColUtils.onlyOrDefault(this, _default);
    }

    default T maxBy(ToIntFunction<T> func) {
        T t = maxByOrDefault(func);
        if (t == null) {
            throw new IllegalArgumentException("Not found.");
        }
        return t;
    }

    @Nullable
    default T maxByOrDefault(ToIntFunction<T> func) {
        return maxByOrDefault(func, null);
    }

    @Nullable
    @Contract ("_,!null->!null")
    default T maxByOrDefault(ToIntFunction<T> func, @Nullable T _default) {
        final class Cons implements Consumer<T> {

            int max = Integer.MIN_VALUE;
            @Nullable
            T maxT = _default;

            @Override
            public void accept(T t) {
                int x = func.applyAsInt(t);
                if (x > max) {
                    maxT = t;
                    max = x;
                }
            }
        }
        Cons cons = new Cons();
        forEach(cons);
        return cons.maxT;
    }

    default ArrayList<T> toList() {
        int len = knownLength(true);
        ArrayList<T> list = len < 0 ? new ArrayList<>() : new ArrayList<>(len);
        forEach(list::add);
        return list;
    }

    @SuppressWarnings ("unchecked")
    default <R> List<R> toList(TypeCheck<R, ? super T> check) {
        return (List<R>) toList();
    }

    default LinkedList<T> toLinkedList() {
        LinkedList<T> list = new LinkedList<>();
        forEach(list::add);
        return list;
    }

    @SuppressWarnings ("unchecked")
    default <R> LinkedList<R> toLinkedList(TypeCheck<R, ? super T> check) {
        return (LinkedList<R>) toLinkedList();
    }

    @Requires("com.google.guava:guava")
    default ImmutableList<T> toImmutableList() {
        int len = knownLength(true);
        ImmutableList.Builder<T> builder = len < 0 ? ImmutableList.builder() : ImmutableList.builderWithExpectedSize(len);
        forEach(builder::add);
        return builder.build();
    }

    @SuppressWarnings ("unchecked")
    @Requires ("com.google.guava:guava")
    default <R> ImmutableList<R> toImmutableList(TypeCheck<R, ? super T> check) {
        return (ImmutableList<R>) toImmutableList();
    }

    default HashSet<T> toSet() {
        HashSet<T> list = new HashSet<>();
        forEach(list::add);
        return list;
    }

    @SuppressWarnings ("unchecked")
    default <R> HashSet<R> toSet(TypeCheck<R, ? super T> check) {
        return (HashSet<R>) toSet();
    }

    default LinkedHashSet<T> toLinkedHashSet() {
        LinkedHashSet<T> list = new LinkedHashSet<>();
        forEach(list::add);
        return list;
    }

    @SuppressWarnings ("unchecked")
    default <R> LinkedHashSet<R> toLinkedHashSet(TypeCheck<R, ? super T> check) {
        return (LinkedHashSet<R>) toLinkedHashSet();
    }

    @Requires ("com.google.guava:guava")
    default ImmutableSet<T> toImmutableSet() {
        ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        forEach(builder::add);
        return builder.build();
    }

    @SuppressWarnings ("unchecked")
    @Requires ("com.google.guava:guava")
    default <R> ImmutableSet<R> toImmutableSet(TypeCheck<R, ? super T> check) {
        return (ImmutableSet<R>) toImmutableSet();
    }

    @SuppressWarnings ("unchecked")
    default Object[] toArray() {
        return toArray((T[]) new Object[0]);
    }

    default T[] toArray(T[] arr) {
        int len = knownLength(true);
        if (len < 0) return toList().toArray(arr);

        T[] array = len > arr.length ? Arrays.copyOf(arr, len) : arr;
        forEach(new Consumer<T>() {
            int i = 0;

            @Override
            public void accept(T t) {
                array[i++] = t;
            }
        });
        if (len < arr.length) {
            array[len] = null;
        }
        return array;
    }

    default <K, V> HashMap<K, V> toMap(Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc) {
        return toMap(new HashMap<>(), kFunc, vFunc);
    }

    default <K, V> HashMap<K, V> toMap(Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc, BinaryOperator<V> mergeFunc) {
        return toMap(new HashMap<>(), kFunc, vFunc, mergeFunc);
    }

    default <K, V> LinkedHashMap<K, V> toLinkedHashMap(Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc) {
        return toMap(new LinkedHashMap<>(), kFunc, vFunc);
    }

    default <K, V> LinkedHashMap<K, V> toLinkedHashMap(Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc, BinaryOperator<V> mergeFunc) {
        return toMap(new LinkedHashMap<>(), kFunc, vFunc, mergeFunc);
    }

    @Requires ("com.google.guava:guava")
    default <K, V> ImmutableMap<K, V> toImmutableMap(Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc) {
        return ImmutableMap.copyOf(toLinkedHashMap(kFunc, vFunc));
    }

    @Requires ("com.google.guava:guava")
    default <K, V> ImmutableMap<K, V> toImmutableMap(Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc, BinaryOperator<V> mergeFunc) {
        return ImmutableMap.copyOf(toLinkedHashMap(kFunc, vFunc, mergeFunc));
    }

    default <K, V, M extends Map<K, V>> M toMap(M map, Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc) {
        return toMap(map, kFunc, vFunc, SneakyUtils.first());
    }

    default <K, V, M extends Map<K, V>> M toMap(M map, Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc, BinaryOperator<V> mergeFunc) {
        forEach(t -> {
            K key = kFunc.apply(t);
            V value = vFunc.apply(t);
            V existing = map.get(key);
            if (existing == null) {
                map.put(key, value);
            } else {
                map.put(key, mergeFunc.apply(existing, value));
            }
        });
        return map;
    }

    default String join(String sep) {
        class Cons implements Consumer<T> {

            private final StringBuilder builder = new StringBuilder();
            private boolean first = true;

            @Override
            public void accept(T t) {
                if (!first) {
                    builder.append(sep);
                } else {
                    first = false;
                }
                builder.append(t);
            }
        }
        Cons cons = new Cons();
        forEach(cons);
        return cons.builder.toString();
    }

    final class Wrapped<T> implements FastStream<T> {

        private final Iterable<T> _itr;
        private final int knownLength;

        private Wrapped(Iterable<T> itr, int knownLength) {
            _itr = itr;
            this.knownLength = knownLength;
        }

        @Override
        public Iterator<T> iterator() {
            return _itr.iterator();
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            _itr.forEach(action);
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            return knownLength;
        }
    }

    final class WrappedSpl<T> implements FastStream<T> {

        private final Spliterator<T> _itr;
        private final int knownLength;

        private WrappedSpl(Spliterator<T> itr, int knownLength) {
            _itr = itr;
            this.knownLength = knownLength;
        }

        @Override
        public Iterator<T> iterator() {
            return new AbstractIterator<T>() {
                private @Nullable T t;
                private final Consumer<T> cons = e -> t = e;

                @Override
                protected @Nullable T computeNext() {
                    if (_itr.tryAdvance(cons)) {
                        return t;
                    }
                    return endOfData();
                }
            };
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            _itr.forEachRemaining(action);
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            return knownLength;
        }
    }

    final class OfSingle<T> implements FastStream<T> {

        private final T thing;

        private OfSingle(T thing) {
            this.thing = thing;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                boolean hasNext = true;

                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                @Override
                public T next() {
                    if (!hasNext) throw new NoSuchElementException();
                    hasNext = false;
                    return thing;
                }

                @Override
                public void forEachRemaining(Consumer<? super T> action) {
                    if (hasNext) {
                        action.accept(thing);
                    }
                }
            };
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            action.accept(thing);
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            return 1;
        }
    }

    final class OfN<T> implements FastStream<T> {

        private final T[] things;

        private OfN(T[] things) {
            this.things = things;
        }

        @Override
        public Iterator<T> iterator() {
            return ColUtils.iterator(things);
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            for (T t : things) {
                action.accept(t);
            }
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            return things.length;
        }
    }

    final class ConcatenatedN<T> implements FastStream<T> {

        private final Iterable<? extends T>[] iterables;

        private ConcatenatedN(Iterable<? extends T>[] iterables) {
            this.iterables = iterables;
        }

        @Override
        public Iterator<T> iterator() {
            return new AbstractIterator<T>() {

                private int i = 0;

                @Nullable
                private Iterator<? extends T> working;

                @Override
                protected T computeNext() {
                    while (true) {
                        if (working == null) {
                            if (i >= iterables.length) break;
                            working = iterables[i++].iterator();
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

        @Override
        public void forEach(Consumer<? super T> action) {
            for (Iterable<? extends T> iterable : iterables) {
                iterable.forEach(action);
            }
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            int len = 0;
            for (Iterable<? extends T> iterable : iterables) {
                int ilen = Internal.knownLength(iterable, consumeToCalculate);
                if (ilen < 0) return -1;

                len += ilen;
            }

            return len;
        }
    }

    final class Concatenated<T> implements FastStream<T> {

        private final Iterable<? extends Iterable<? extends T>> iterables;

        private Concatenated(Iterable<? extends Iterable<? extends T>> iterables) {
            this.iterables = iterables;
        }

        @Override
        public Iterator<T> iterator() {
            return new AbstractIterator<T>() {
                private final Iterator<? extends Iterable<? extends T>> itr = iterables.iterator();

                @Nullable
                private Iterator<? extends T> working;

                @Override
                protected T computeNext() {
                    while (true) {
                        if (working == null) {
                            if (!itr.hasNext()) break;
                            working = itr.next().iterator();
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

        @Override
        public void forEach(Consumer<? super T> action) {
            iterables.forEach(e -> e.forEach(action));
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            int len = 0;
            for (Iterable<? extends T> iterable : iterables) {
                int ilen = Internal.knownLength(iterable, consumeToCalculate);
                if (ilen < 0) return -1;

                len += ilen;
            }

            return len;
        }
    }

    final class Filtered<T> implements FastStream<T> {

        private final FastStream<T> parent;
        private final Predicate<? super T> pred;

        private Filtered(FastStream<T> parent, Predicate<? super T> pred) {
            this.parent = parent;
            this.pred = pred;
        }

        @Override
        public Iterator<T> iterator() {
            return new AbstractIterator<T>() {
                private final Iterator<T> itr = parent.iterator();

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

        @Override
        public void forEach(Consumer<? super T> action) {
            parent.forEach(e -> {
                if (pred.test(e)) {
                    action.accept(e);
                }
            });
        }
    }

    final class Mapped<T, R> implements FastStream<R> {

        private final FastStream<T> parent;
        private final Function<? super T, ? extends R> func;

        private Mapped(FastStream<T> parent, Function<? super T, ? extends R> func) {
            this.parent = parent;
            this.func = func;
        }

        @Override
        public Iterator<R> iterator() {
            return new Iterator<R>() {
                private final Iterator<T> itr = parent.iterator();

                @Override
                public boolean hasNext() {
                    return itr.hasNext();
                }

                @Override
                public R next() {
                    return func.apply(itr.next());
                }
            };
        }

        @Override
        public void forEach(Consumer<? super R> action) {
            parent.forEach(e -> action.accept(func.apply(e)));
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            return parent.knownLength(consumeToCalculate);
        }

    }

    final class FlatMapped<T, R> implements FastStream<R> {

        private final FastStream<T> parent;
        private final Function<? super T, ? extends Iterable<? extends R>> func;

        private FlatMapped(FastStream<T> parent, Function<? super T, ? extends Iterable<? extends R>> func) {
            this.parent = parent;
            this.func = func;
        }

        @Override
        public Iterator<R> iterator() {
            return new AbstractIterator<R>() {

                private final Iterator<T> itr = parent.iterator();

                @Nullable
                private Iterator<? extends R> working;

                @Override
                protected R computeNext() {
                    while (true) {
                        if (working == null) {
                            if (!itr.hasNext()) break;
                            working = func.apply(itr.next()).iterator();
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

        @Override
        public void forEach(Consumer<? super R> action) {
            parent.forEach(e -> func.apply(e).forEach(action));
        }
    }

    final class Distinct<T> implements FastStream<T> {

        private final FastStream<T> parent;

        private Distinct(FastStream<T> parent) {
            this.parent = parent;
        }

        private int knownLength = -1;

        @Override
        public Iterator<T> iterator() {
            return new AbstractIterator<T>() {
                private final Set<T> set = new HashSet<>();
                private final Iterator<T> itr = parent.iterator();

                @Override
                protected T computeNext() {
                    while (itr.hasNext()) {
                        T e = itr.next();
                        if (set.add(e)) {
                            return e;
                        }
                    }
                    knownLength = set.size();
                    return endOfData();
                }
            };
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            Set<T> set = new HashSet<>();
            parent.forEach(e -> {
                if (set.add(e)) {
                    action.accept(e);
                }
            });
            knownLength = set.size();
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            return knownLength;
        }

        @Override
        public HashSet<T> toSet() {
            return parent.toSet();
        }

        @Override
        public LinkedHashSet<T> toLinkedHashSet() {
            return parent.toLinkedHashSet();
        }

        @Override
        public ImmutableSet<T> toImmutableSet() {
            return parent.toImmutableSet();
        }
    }

    final class Group<K, V> implements FastStream<V> {

        private final K key;
        private int size;
        private Object[] values = new Object[1];

        public Group(K key) {
            this.key = key;
        }

        @Override
        @SuppressWarnings ("unchecked")
        public Iterator<V> iterator() {
            return ColUtils.iterator((V[]) values, 0, size);
        }

        @Override
        @SuppressWarnings ("unchecked")
        public void forEach(Consumer<? super V> action) {
            for (int i = 0; i < size; i++) {
                action.accept((V) values[i]);
            }
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            return size;
        }

        public K getKey() {
            return key;
        }

        private void add(V value) {
            resize();
            values[size++] = value;
        }

        private void resize() {
            if (size < values.length) return; // No need for resize.
            values = Arrays.copyOf(values, size > 1024 ? size * 2 : size * 4);
        }

        @Override
        @SuppressWarnings ({ "unchecked", "SuspiciousSystemArraycopy" })
        public V[] toArray(V[] arr) {
            if (arr.length >= size) {
                System.arraycopy(values, 0, arr, 0, size);
                if (arr.length >= size + 1) {
                    arr[size + 1] = null;
                }
                return arr;
            }

            return (V[]) Arrays.copyOf(values, size, arr.getClass());
        }
    }

    final class Grouped<T, K, V> implements FastStream<Group<K, V>> {

        private final FastStream<T> parent;
        private final Function<? super T, ? extends K> keyFunc;
        private final Function<? super T, ? extends V> valueFunc;

        @Nullable
        private Map<K, Group<K, V>> groups = null;

        public Grouped(FastStream<T> parent, Function<? super T, ? extends K> keyFunc, Function<? super T, ? extends V> valueFunc) {
            this.parent = parent;
            this.keyFunc = keyFunc;
            this.valueFunc = valueFunc;
        }

        @NotNull
        @Override
        public Iterator<Group<K, V>> iterator() {
            return getGroups().values().iterator();
        }

        @Override
        public void forEach(Consumer<? super Group<K, V>> action) {
            getGroups().values().forEach(action);
        }

        private Map<K, Group<K, V>> getGroups() {
            if (groups == null) {
                groups = new HashMap<>();
                parent.forEach(e -> groups.computeIfAbsent(keyFunc.apply(e), Group::new)
                        .add(valueFunc.apply(e)));
            }
            return groups;
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            return consumeToCalculate ? getGroups().size() : -1;
        }
    }

    final class Sorted<T> implements FastStream<T> {

        private final FastStream<T> parent;
        private final Comparator<? super T> comparator;

        T @Nullable [] sorted = null;

        private Sorted(FastStream<T> parent, Comparator<? super T> comparator) {
            this.parent = parent;
            this.comparator = comparator;
        }

        @Override
        public Iterator<T> iterator() {
            return ColUtils.iterator(getSorted());
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            for (T t : getSorted()) {
                action.accept(t);
            }
        }

        private T[] getSorted() {
            if (sorted == null) {
                sorted = unsafeCast(parent.toArray());
                Arrays.sort(sorted, comparator);
            }
            return sorted;
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            return consumeToCalculate ? getSorted().length : parent.knownLength(false);
        }

        @Override
        public T[] toArray(T[] arr) {
            T[] sorted = getSorted();
            if (arr.getClass() == sorted.getClass()) {
                this.sorted = null;
                return sorted;
            }
            //noinspection unchecked
            return (T[]) Arrays.copyOf(sorted, sorted.length, arr.getClass());
        }

        @Override
        public ArrayList<T> toList() {
            return new ArrayList<>(Arrays.asList(getSorted()));
        }

        @Override
        public ImmutableList<T> toImmutableList() {
            return ImmutableList.copyOf(getSorted());
        }
    }

    final class Reversed<T> implements FastStream<T> {

        private final FastStream<T> parent;

        T @Nullable [] reversed = null;

        private Reversed(FastStream<T> parent) {
            this.parent = parent;
        }

        @Override
        public Iterator<T> iterator() {
            return ColUtils.iterator(getReversed());
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            for (T t : getReversed()) {
                action.accept(t);
            }
        }

        private T[] getReversed() {
            if (reversed == null) {
                reversed = unsafeCast(parent.toArray());
                ColUtils.reverse(reversed);
            }
            return reversed;
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            return consumeToCalculate ? getReversed().length : parent.knownLength(false);
        }

        @Override
        public T[] toArray(T[] arr) {
            T[] reversed = getReversed();
            if (arr.getClass() == reversed.getClass()) {
                this.reversed = null;
                return reversed;
            }
            //noinspection unchecked
            return (T[]) Arrays.copyOf(reversed, reversed.length, arr.getClass());
        }
    }

    final class Peeked<T> implements FastStream<T> {

        private final FastStream<T> parent;
        private final Consumer<? super T> cons;

        private Peeked(FastStream<T> parent, Consumer<? super T> cons) {
            this.parent = parent;
            this.cons = cons;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private final Iterator<T> itr = parent.iterator();

                @Override
                public boolean hasNext() {
                    return itr.hasNext();
                }

                @Override
                public T next() {
                    T n = itr.next();
                    cons.accept(n);
                    return n;
                }
            };
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            parent.forEach(e -> {
                cons.accept(e);
                action.accept(e);
            });
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            return parent.knownLength(consumeToCalculate);
        }
    }

    final class Sliced<T> implements FastStream<T> {

        private final FastStream<T> parent;
        private final int min;
        private final int max;

        private Sliced(FastStream<T> parent, int min, int max) {
            this.parent = parent;
            this.min = min;
            this.max = max;
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return new AbstractIterator<T>() {
                private final Iterator<T> itr = parent.iterator();
                private int i;

                @Nullable
                @Override
                protected T computeNext() {
                    while (i < min && itr.hasNext()) {
                        itr.next();
                        i++;
                    }
                    if (i++ >= max || !itr.hasNext()) return endOfData();
                    return itr.next();
                }
            };
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            ForEachAbort abort = new ForEachAbort();
            try {
                parent.forEach(new Consumer<T>() {
                    int i = 0;

                    @Override
                    public void accept(T t) {
                        int n = i++;
                        if (n < min) return;
                        if (n >= max) throw abort;
                        action.accept(t);
                    }
                });
            } catch (ForEachAbort ex) {
                if (ex != abort) {
                    throw ex;
                }
            }
        }

        @Override
        public int knownLength(boolean consumeToCalculate) {
            int pLen = parent.knownLength(consumeToCalculate);
            if (pLen == -1) return -1;

            return Math.min(Math.max(pLen - min, 0), max);
        }
    }

    final class TypeCheck<T, S> {

        private TypeCheck() { }
    }

    static <T> TypeCheck<T, T> infer() { return null; }

    class Internal {

        private Internal() { }

        private static <T> int knownLength(Iterable<? extends T> itr, boolean consumeToCalculate) {
            if (itr instanceof Collection) return ((Collection<?>) itr).size();
            if (itr instanceof FastStream) return ((FastStream<?>) itr).knownLength(consumeToCalculate);
            return -1;
        }

        private static class Empty<T> implements FastStream<T> {
            @Override public Iterator<T> iterator() { return Collections.emptyIterator(); }
            @Override public void forEach(Consumer<? super T> action) { }
            @Override public FastStream<T> concat(Iterable<? extends T> other) { return of(other); }
            @Override public FastStream<T> filter(Predicate<? super T> pred) { return this; }
            @Override public FastStream<T> filterNot(Predicate<? super T> pred) { return this; }
            @Override public <R> FastStream<R> map(Function<? super T, ? extends R> func) { return empty(); }
            @Override public <R> FastStream<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> func) { return empty(); }
            @Override public FastStream<T> distinct() { return this; }
            @Override public <K> FastStream<Group<K, T>> groupBy(Function<? super T, ? extends K> keyFunc) { return empty(); }
            @Override public <K, V> FastStream<Group<K, V>> groupBy(Function<? super T, ? extends K> keyFunc, Function<? super T, ? extends V> valueFunc) { return empty(); }
            @Override public FastStream<T> sorted() { return this; }
            @Override public FastStream<T> sorted(Comparator<? super T> comparator) { return this; }
            @Override public FastStream<T> peek(Consumer<? super T> cons) { return this; }
            @Override public FastStream<T> limit(int max) { return this; }
            @Override public FastStream<T> skip(int n) { return this; }
            @Override @Nullable public <U> U fold(@Nullable U identity, BiFunction<? super @Nullable U, ? super T, ? extends U> accumulator) { return identity; }
            @Override public Optional<T> fold(BinaryOperator<T> accumulator) { return Optional.empty(); }
            @Override public boolean anyMatch(Predicate<? super T> pred) { return false; }
            @Override public boolean allMatch(Predicate<? super T> pred) { return true; }
            @Override public boolean noneMatch(Predicate<? super T> pred) { return true; }
            @Override public boolean isEmpty() { return true; }
            @Override public int knownLength() { return 0; }
            @Override public int knownLength(boolean consumeToCalculate) { return 0; }
            @Override public int count() { return 0; }
            @Override public int intSum(ToIntFunction<? super T> func) { return 0; }
            @Override public long longSum(ToLongFunction<? super T> func) { return 0; }
            @Override public double doubleSum(ToDoubleFunction<? super T> func) { return 0; }
            @Override public Optional<T> findFirst() { return Optional.empty(); }
            @Override public T first() { throw new IllegalArgumentException("Not found."); }
            @Nullable @Override public T firstOrDefault() { return null; }
            @Nullable @Override public T firstOrDefault(@Nullable T _default) { return _default; }
            @Override public Optional<T> findLast() { return Optional.empty(); }
            @Override public T last() { throw new IllegalArgumentException("Not found."); }
            @Nullable @Override public T lastOrDefault() { return null; }
            @Nullable @Override public T lastOrDefault(@Nullable T _default) { return _default; }
            @Override public T only() { throw new IllegalArgumentException("Not found."); }
            @Nullable @Override public T onlyOrDefault() { return null; }
            @Nullable @Override public T onlyOrDefault(@Nullable T _default) { return _default; }
            @Override public T maxBy(ToIntFunction<T> func) { throw new IllegalArgumentException("Not found."); }
            @Nullable @Override public T maxByOrDefault(ToIntFunction<T> func) { return null; }
            @Nullable @Override public T maxByOrDefault(ToIntFunction<T> func, @Nullable T _default) { return _default; }
            @Override public ArrayList<T> toList() { return new ArrayList<>(); }
            @Override public LinkedList<T> toLinkedList() { return new LinkedList<>(); }
            @Override public ImmutableList<T> toImmutableList() { return ImmutableList.of(); }
            @Override public HashSet<T> toSet() { return new HashSet<>(); }
            @Override public LinkedHashSet<T> toLinkedHashSet() { return new LinkedHashSet<>(); }
            @Override public ImmutableSet<T> toImmutableSet() { return ImmutableSet.of(); }
            @Override public Object[] toArray() { return new Object[0]; }
            @Override public T[] toArray(T[] arr) { return ColUtils.fill(arr, null); }
            @Override public <K, V> HashMap<K, V> toMap(Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc) { return new HashMap<>(); }
            @Override public <K, V> HashMap<K, V> toMap(Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc, BinaryOperator<V> mergeFunc) { return new HashMap<>(); }
            @Override public <K, V> LinkedHashMap<K, V> toLinkedHashMap(Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc) { return new LinkedHashMap<>(); }
            @Override public <K, V> LinkedHashMap<K, V> toLinkedHashMap(Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc, BinaryOperator<V> mergeFunc) { return new LinkedHashMap<>(); }
            @Override public <K, V> ImmutableMap<K, V> toImmutableMap(Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc) { return ImmutableMap.of(); }
            @Override public <K, V> ImmutableMap<K, V> toImmutableMap(Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc, BinaryOperator<V> mergeFunc) { return ImmutableMap.of(); }
            @Override public <K, V, M extends Map<K, V>> M toMap(M map, Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc) { return map; }
            @Override public <K, V, M extends Map<K, V>> M toMap(M map, Function<? super T, ? extends K> kFunc, Function<? super T, ? extends V> vFunc, BinaryOperator<V> mergeFunc) { return map; }
            @Override public String join(String sep) { return ""; }
        }
    }
}
