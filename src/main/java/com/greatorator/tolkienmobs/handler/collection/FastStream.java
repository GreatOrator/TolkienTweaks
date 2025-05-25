package com.greatorator.tolkienmobs.handler.collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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

    default FastStream<T> filter(Predicate<? super T> pred) {
        return new Filtered<>(this, pred);
    }

    default <R> FastStream<R> map(Function<? super T, ? extends R> func) {
        return new Mapped<>(this, func);
    }

    default FastStream<T> limit(@Range (from = -1, to = Integer.MAX_VALUE) int max) {
        if (max == -1) return this;
        if (max <= 0) return empty();

        return new Sliced<>(this, 0, max);
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

    default T first() {
        return ColUtils.head(this);
    }

    default T only() {
        return ColUtils.only(this);
    }

    default ArrayList<T> toList() {
        int len = knownLength(true);
        ArrayList<T> list = len < 0 ? new ArrayList<>() : new ArrayList<>(len);
        forEach(list::add);
        return list;
    }

    final class Wrapped<T> implements FastStream<T> {

        private final Iterable<T> _itr;
        private final int knownLength;

        private Wrapped(Iterable<T> itr, int knownLength) {
            _itr = itr;
            this.knownLength = knownLength;
        }

        @Override
        public @NotNull Iterator<T> iterator() {
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
        public @NotNull Iterator<T> iterator() {
            return new AbstractIterator<>() {
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
        public @NotNull Iterator<T> iterator() {
            return new Iterator<>() {
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
        public @NotNull Iterator<T> iterator() {
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

    final class Filtered<T> implements FastStream<T> {

        private final FastStream<T> parent;
        private final Predicate<? super T> pred;

        private Filtered(FastStream<T> parent, Predicate<? super T> pred) {
            this.parent = parent;
            this.pred = pred;
        }

        @Override
        public @NotNull Iterator<T> iterator() {
            return new AbstractIterator<>() {
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
        public @NotNull Iterator<R> iterator() {
            return new Iterator<>() {
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
            return new AbstractIterator<>() {
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
                parent.forEach(new Consumer<>() {
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

    class Internal {

        private Internal() { }

        private static <T> int knownLength(Iterable<? extends T> itr, boolean consumeToCalculate) {
            if (itr instanceof Collection) return ((Collection<?>) itr).size();
            if (itr instanceof FastStream) return ((FastStream<?>) itr).knownLength(consumeToCalculate);
            return -1;
        }

        private static class Empty<T> implements FastStream<T> {
            @Override public @NotNull Iterator<T> iterator() { return Collections.emptyIterator(); }
            @Override public void forEach(Consumer<? super T> action) { }
            @Override public FastStream<T> filter(Predicate<? super T> pred) { return this; }
            @Override public <R> FastStream<R> map(Function<? super T, ? extends R> func) { return empty(); }
            @Override public FastStream<T> limit(int max) { return this; }
            @Override public boolean isEmpty() { return true; }
            @Override public int knownLength() { return 0; }
            @Override public int knownLength(boolean consumeToCalculate) { return 0; }
            @Override public int count() { return 0; }
            @Override public T first() { throw new IllegalArgumentException("Not found."); }
            @Override public T only() { throw new IllegalArgumentException("Not found."); }
            @Override public ArrayList<T> toList() { return new ArrayList<>(); }
        }
    }
}
