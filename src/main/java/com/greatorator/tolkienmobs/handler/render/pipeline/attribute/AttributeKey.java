package com.greatorator.tolkienmobs.handler.render.pipeline.attribute;

import com.greatorator.tolkienmobs.handler.render.pipeline.IVertexOperation;
import com.greatorator.tolkienmobs.util.ArrayUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import static com.greatorator.tolkienmobs.util.SneakyUtils.unsafeCast;


public abstract class AttributeKey<T> {

    private final String name;
    public final int attributeKeyIndex;
    public final int operationIndex;

    public AttributeKey(String name) {
        this.name = name;

        attributeKeyIndex = AttributeKeyRegistry.registerAttributeKey(this);
        operationIndex = IVertexOperation.registerOperation();
    }

    public static <T> AttributeKey<T> create(String name, IntFunction<T> factory) {
        return new AttributeKey<>(name) {
            @Override
            public T createDefault(int length) {
                return factory.apply(length);
            }

            @Override
            public T copy(T src, int length) {
                T dst = createDefault(length);
                ArrayUtils.arrayCopy(src, 0, dst, 0, Array.getLength(src));
                return dst;
            }

            @Override
            public T copyRange(T src, int srcpos, T dest, int destpos, int length) {
                ArrayUtils.arrayCopy(src, srcpos, dest, destpos, length);
                return dest;
            }
        };
    }

    public abstract T createDefault(int length);

    public abstract T copy(T src, int length);

    public abstract T copyRange(T src, int srcpos, T dest, int destpos, int length);

    public static class AttributeKeyRegistry {

        private static final Map<String, AttributeKey<?>> nameMap = new HashMap<>();
        private static final List<AttributeKey<?>> attributeKeys = new ArrayList<>();

        private static synchronized int registerAttributeKey(AttributeKey<?> attr) {
            if (nameMap.containsKey(attr.name)) {
                throw new IllegalArgumentException("Duplicate registration of attribute with name: " + attr.name);
            }
            nameMap.put(attr.name, attr);
            attributeKeys.add(attr);
            return attributeKeys.size() - 1;
        }

        public static <T> AttributeKey<T> getAttributeKey(int index) {
            return unsafeCast(attributeKeys.get(index));
        }

        public static int numAttributes() {
            return attributeKeys.size();
        }
    }
}