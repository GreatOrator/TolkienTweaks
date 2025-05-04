package com.greatorator.tolkienmobs.network.config;

import com.greatorator.tolkienmobs.util.MathUtility;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;

import java.util.Optional;

public interface ListRestriction {
    Optional<Failure> test(ConfigValueList values);

    String describe();

    static ListRestriction intRange(int min, int max) {
        return new IntRange(min, max);
    }

    static ListRestriction doubleRange(double min, double max) {
        return new DoubleRange(min, max);
    }

    record IntRange(int min, int max) implements ListRestriction {

        public IntRange {
            if (min > max) {
                throw new IllegalArgumentException("Min cannot be larger than max.");
            }
        }

        @Override
        public Optional<Failure> test(ConfigValueList configValue) {
            IntIterator iter = configValue.getInts().intIterator();
            int i = 0;
            while (iter.hasNext()) {
                int val = iter.nextInt();
                if (!MathUtility.between(min, val, max)) {
                    return Optional.of(new Failure(i, val));
                }
                i++;
            }
            return Optional.empty();
        }

        @Override
        public String describe() {
            return "[ " + min + " ~ " + max + " ]";
        }
    }

    record DoubleRange(double min, double max) implements ListRestriction {

        public DoubleRange {
            if (min > max) {
                throw new IllegalArgumentException("Min cannot be larger than max.");
            }
        }

        @Override
        public Optional<Failure> test(ConfigValueList configValue) {
            DoubleIterator iter = configValue.getDoubles().doubleIterator();
            int i = 0;
            while (iter.hasNext()) {
                double val = iter.nextDouble();
                if (!MathUtility.between(min, val, max)) {
                    return Optional.of(new Failure(i, val));
                }
                i++;
            }
            return Optional.empty();
        }

        @Override
        public String describe() {
            return "[ " + min + " ~ " + max + " ]";
        }
    }

    record Failure(int index, Object value) {
    }
}
