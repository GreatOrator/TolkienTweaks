package com.greatorator.tolkienmobs.handler.collection;

import com.greatorator.tolkienmobs.handler.annotation.ReplaceWith;
import com.greatorator.tolkienmobs.handler.annotation.ReplaceWithExpr;
import com.greatorator.tolkienmobs.util.Copyable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.greatorator.tolkienmobs.util.SneakyUtils.unsafeCast;

public class ColUtils {

    @SuppressWarnings ("unchecked")
    public static <T> T[] slice(T[] arr, int from, int until) {
        int low = Math.max(from, 0);
        int high = Math.min(Math.max(until, 0), arr.length);
        int size = Math.max(high - low, 0);
        T[] result = (T[]) Array.newInstance(arr.getClass().getComponentType(), size);
        if (size > 0) {
            System.arraycopy(arr, low, result, 0, size);
        }
        return result;
    }

    @Nullable
    public static <T> T maxBy(T[] col, ToIntFunction<T> func) {
        return maxBy(Arrays.asList(col), func);
    }

    @Nullable
    @Deprecated
    @ScheduledForRemoval (inVersion = "0.5.0")
    @ReplaceWithExpr ("maxByOrDefault(col, () -> ..., null)")
    public static <T> T maxBy(Iterable<T> col, ToIntFunction<T> func) {
        return maxByOrDefault(col, func, null);
    }

    public static <T> T requireMaxBy(Iterable<T> col, ToIntFunction<T> func) {
        T ret = maxByOrDefault(col, func, null);
        if (ret == null) {
            throw new IllegalArgumentException("Not found.");
        }
        return ret;
    }

    @Nullable
    @Contract ("_,_,!null -> !null")
    public static <T> T maxByOrDefault(Iterable<T> col, ToIntFunction<T> func, @Nullable T default_) {
        int max = Integer.MIN_VALUE;
        T maxT = null;
        for (T t : col) {
            int x = func.applyAsInt(t);
            if (x > max) {
                maxT = t;
                max = x;
            }
        }
        return maxT == null ? default_ : maxT;
    }

    public static <T> boolean allMatch(T[] col, Predicate<T> func) {
        return allMatch(Arrays.asList(col), func);
    }

    public static <T> boolean allMatch(Iterable<T> col, Predicate<T> func) {
        for (T t : col) {
            if (!func.test(t)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean anyMatch(Iterable<T> col, Predicate<T> func) {
        for (T t : col) {
            if (func.test(t)) {
                return true;
            }
        }
        return false;
    }

    public static <T> Optional<T> findFirst(Iterable<T> col, Predicate<T> func) {
        for (T t : col) {
            if (func.test(t)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public static <T> Optional<T> headOption(Iterable<T> col) {
        return Optional.ofNullable(headOrDefault(col));
    }

    public static <T> T head(Iterable<T> col) {
        T head = headOrDefault(col);
        if (head == null) {
            throw new IllegalArgumentException("Not found.");
        }
        return head;
    }

    @Nullable
    public static <T> T headOrDefault(Iterable<T> col) {
        return headOrDefault(col, null);
    }

    @Nullable
    @Contract ("_,!null -> !null")
    public static <T> T headOrDefault(Iterable<T> col, @Nullable T _default) {
        class Cons implements Consumer<T> {

            @Nullable
            T t = _default;

            @Override
            public void accept(T t) {
                if (this.t != _default) {
                    throw new ForEachAbort();
                }
                this.t = t;
            }
        }
        Cons cons = new Cons();
        try {
            col.forEach(cons);
        } catch (ForEachAbort ignored) {
        }
        return cons.t;
    }

    public static <T> Optional<T> tailOption(Iterable<T> col) {
        return Optional.ofNullable(tailOrDefault(col));
    }

    public static <T> T tail(Iterable<T> col) {
        T tail = tailOrDefault(col);
        if (tail == null) {
            throw new IllegalArgumentException("Not found.");
        }
        return tail;
    }

    @Nullable
    public static <T> T tailOrDefault(Iterable<T> col) {
        return tailOrDefault(col, null);
    }

    @Nullable
    @Contract ("_,!null -> !null")
    public static <T> T tailOrDefault(Iterable<T> col, @Nullable T _default) {
        class Cons implements Consumer<T> {

            @Nullable
            T t = _default;

            @Override
            public void accept(T t) {
                this.t = t;
            }
        }
        Cons cons = new Cons();
        col.forEach(cons);
        return cons.t;
    }

    @SafeVarargs
    public static <T> boolean containsKeys(Map<T, ?> map, T... keys) {
        for (T object : keys) {
            if (!map.containsKey(object)) {
                return false;
            }
        }
        return true;
    }

    public static <T> T[] addToArrayFirstNull(T[] array, T value) {
        int nullIndex = -1;
        for (int i = 0; i < array.length; i++) {
            T v = array[i];
            if (v == null) {
                nullIndex = i;
                break;
            }
        }
        if (nullIndex == -1) {
            T[] copy = createNewArray(array, array.length + 1);
            System.arraycopy(array, 0, copy, 0, array.length);
            nullIndex = array.length;
            array = copy;
        }
        array[nullIndex] = value;
        return array;
    }

    public static <T> List<T> addAllNoNull(T[] array, List<T> list) {
        for (T value : array) {
            if (value != null) {
                list.add(value);
            }
        }
        return list;
    }

    public static <T> boolean isEmpty(T[] array) {
        for (T value : array) {
            if (value != null) {
                return false;
            }
        }
        return true;
    }

    public static <T> int countNonNull(T[] array) {
        return count(array, Objects::nonNull);
    }

    public static <T> int count(T[] array, Function<T, Boolean> check) {
        int counter = 0;
        for (T value : array) {
            if (check.apply(value)) {
                counter++;
            }
        }
        return counter;
    }

    public static <T> T[] fill(T[] array, T value) {
        for (int i = 0; i < array.length; i++) {
            T newValue = value;
            if (value instanceof Copyable) {
                Copyable<T> copyable = unsafeCast(value);
                newValue = copyable.copy();
            }
            array[i] = newValue;
        }
        return array;
    }

    public static <T> void fillArray(T[] array, T value, Function<T, Boolean> check) {
        for (int i = 0; i < array.length; i++) {
            if (check.apply(array[i])) {
                T newValue = value;
                if (value instanceof Copyable) {
                    Copyable<T> copyable = unsafeCast(value);
                    newValue = copyable.copy();
                }
                array[i] = newValue;
            }
        }
    }

    public static void arrayCopy(Object src, int srcPos, Object dst, int destPos, int length) {
        System.arraycopy(src, srcPos, dst, destPos, length);
        if (dst instanceof Copyable[]) {
            Object[] oa = (Object[]) dst;
            Copyable<Object>[] c = unsafeCast(dst);
            for (int i = destPos; i < destPos + length; i++) {
                if (c[i] != null) {
                    oa[i] = c[i].copy();
                }
            }
        }
    }

    public static <T> int indexOf(T[] array, T object) {
        if (object == null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < array.length; i++) {
                if (object.equals(array[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static <T> int indexOfRef(T[] array, T object) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == object) {
                return i;
            }
        }
        return -1;
    }

    public static <T> T[] createNewArray(T[] array) {
        return createNewArray(array, array.length);
    }

    public static <T> T[] createNewArray(T[] array, int length) {
        Class<? extends T[]> newType = unsafeCast(array.getClass());
        T[] copy;
        if (newType.equals(Object[].class)) {
            copy = unsafeCast(new Object[length]);
        } else {
            copy = unsafeCast(newArray(newType.getComponentType(), length));
        }
        return copy;
    }

    public static <T> T[] newArray(Class<T> arrayClass, int length) {
        return unsafeCast(Array.newInstance(arrayClass, length));
    }

    public static <T> T[] rollArray(T[] input, int shift) {
        T[] newArray = createNewArray(input);

        for (int i = 0; i < input.length; i++) {
            int newPos = (i + shift) % input.length;

            if (newPos < 0) {
                newPos += input.length;
            }
            newArray[newPos] = input[i];
        }
        return newArray;
    }

    public static <T> boolean contains(T[] input, T element) {
        for (T test : input) {
            if (Objects.equals(test, element)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T[] inverse(T[] input, T[] allElements) {
        List<T> list = new LinkedList<>();
        for (T e : allElements) {
            if (!contains(input, e)) {
                list.add(e);
            }
        }

        return list.toArray(createNewArray(input, list.size()));
    }

    public static <T> boolean isNullOrContainsNull(T @Nullable [] input) {
        if (input == null) return true;
        for (T t : input) {
            if (t != null) continue;
            return true;
        }
        return false;
    }

    public static List<Integer> toList(int[] arr) {
        List<Integer> list = new ArrayList<>();
        for (int i : arr) {
            list.add(i);
        }
        return list;
    }

    public static <E> Iterable<E> iterable(Enumeration<E> enumeration) {
        return () -> new Iterator<E>() {
            //@formatter:off
            @Override public boolean hasNext() { return enumeration.hasMoreElements(); }
            @Override public E next() { return enumeration.nextElement(); }
            //@formatter:on
        };
    }

    @Deprecated
    @ScheduledForRemoval (inVersion = "0.5.0")
    @ReplaceWith ("Use FastStream in the first place. This has terrible performance.")
    public static <E> Iterable<E> iterable(Stream<E> stream) {
        return stream::iterator;
    }

    @Deprecated
    @ScheduledForRemoval (inVersion = "0.5.0")
    @ReplaceWithExpr("StreamSupport.stream(iter.spliterator(), false)")
    public static <E> Stream<E> stream(Iterable<E> iter) {
        return StreamSupport.stream(iter.spliterator(), false);
    }

    @Deprecated
    @ScheduledForRemoval (inVersion = "0.5.0")
    @ReplaceWithExpr ("StreamSupport.stream(iter.spliterator(), true)")
    public static <E> Stream<E> parallelStream(Iterable<E> iter) {
        return StreamSupport.stream(iter.spliterator(), true);
    }

    @Deprecated
    @ScheduledForRemoval (inVersion = "0.5.0")
    @ReplaceWith ("Use FastStream in the first place.")
    @Nullable
    public static <T> T onlyOrDefault(Stream<T> stream) {
        return onlyOrDefault(stream, null);
    }

    @Deprecated
    @ScheduledForRemoval (inVersion = "0.5.0")
    @ReplaceWith ("Use FastStream in the first place.")
    @Nullable
    @Contract ("_,!null -> !null")
    public static <T> T onlyOrDefault(Stream<T> stream, @Nullable T _default) {
        return onlyOrDefault(iterable(stream), _default);
    }

    @Nullable
    public static <T> T onlyOrDefault(Iterable<T> iterable) {
        return onlyOrDefault(iterable, null);
    }

    @Nullable
    @Contract ("_,!null -> !null")
    public static <T> T onlyOrDefault(Iterable<T> iterable, @Nullable T _default) {
        final class Cons implements Consumer<T> {

            @Nullable
            T thing = _default;
            boolean found = false;

            @Override
            public void accept(T t) {
                if (found) {
                    thing = _default;
                    throw new ForEachAbort();
                }
                found = true;
                thing = t;
            }
        }
        Cons cons = new Cons();
        try {
            iterable.forEach(cons);
        } catch (ForEachAbort ignored) {
        }
        return cons.thing;
    }

    @Deprecated
    @ScheduledForRemoval (inVersion = "0.5.0")
    @ReplaceWith("Use FastStream in the first place.")
    public static <T> T only(Stream<T> stream) {
        return only(iterable(stream));
    }

    public static <T> T only(Iterable<T> col) {
        final class Cons implements Consumer<T> {

            @Nullable
            T thing = null;
            boolean found = false;

            @Override
            public void accept(T t) {
                if (found) {
                    throw new IllegalArgumentException("More than one element.");
                }
                found = true;
                thing = t;
            }
        }
        Cons cons = new Cons();
        col.forEach(cons);
        if (!cons.found) {
            throw new IllegalArgumentException("Not found.");
        }
        return cons.thing;
    }

    public static <T> Iterator<T> iterator(T[] arr) {
        return iterator(arr, 0, arr.length);
    }

    public static <T> Iterator<T> iterator(T[] arr, int from, int until) {
        if (until > arr.length) throw new IndexOutOfBoundsException();
        return new Iterator<T>() {
            int i = from;

            @Override
            public boolean hasNext() {
                return i < until;
            }

            @Override
            public T next() {
                return arr[i++];
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                for (; i < until; i++) {
                    action.accept(arr[i]);
                }
            }
        };
    }

    public static <T> void reverse(T[] array) {
        for (int i = 0, j = array.length-1; i < j; i++, j--) {
            T temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}