package com.greatorator.tolkienmobs.entity;

import java.util.Arrays;
import java.util.Comparator;

public enum TolkienVariant {
    DEFAULT(0),
    RED(1),
    ORANGE(2),
    YELLOW(3);

    private static final TolkienVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(TolkienVariant::getId)).toArray(TolkienVariant[]::new);
    private final int id;

    TolkienVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TolkienVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}