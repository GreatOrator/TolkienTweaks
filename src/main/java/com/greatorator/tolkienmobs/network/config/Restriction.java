package com.greatorator.tolkienmobs.network.config;

import com.greatorator.tolkienmobs.util.MathUtility;

import java.util.function.Predicate;

public interface Restriction extends Predicate<ConfigValue> {

    @Override
    boolean test(ConfigValue configValue);

    String describe();

    static Restriction intRange(int min, int max) {
        return new IntRange(min, max);
    }

    static Restriction doubleRange(double min, double max) {
        return new DoubleRange(min, max);
    }

    record IntRange(int min, int max) implements Restriction {

        public IntRange {
            if (min > max) {
                throw new IllegalArgumentException("Min cannot be larger than max.");
            }
        }

        @Override
        public boolean test(ConfigValue configValue) {
            return MathUtility.between(min, configValue.getInt(), max);
        }

        @Override
        public String describe() {
            return "[ " + min + " ~ " + max + " ]";
        }
    }

    record DoubleRange(double min, double max) implements Restriction {

        public DoubleRange {
            if (min > max) {
                throw new IllegalArgumentException("Min cannot be larger than max.");
            }
        }

        @Override
        public boolean test(ConfigValue configValue) {
            return MathUtility.between(min, configValue.getDouble(), max);
        }

        @Override
        public String describe() {
            return "[ " + min + " ~ " + max + " ]";
        }
    }
}