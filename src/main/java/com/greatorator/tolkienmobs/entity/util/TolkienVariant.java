package com.greatorator.tolkienmobs.entity.util;

import java.util.Arrays;
import java.util.Comparator;

public enum TolkienVariant {
    DEFAULT(0),
    RED(1),
    ORANGE(2),
    YELLOW(3),
    GREEN(4),
    BLUE(5),
    INDIGO(6),
    VIOLET(7),
    MAGENTA(8),
    PINK(9),
    GRAY(10),
    AQUA(11),
    BEIGE(12),
    BROWN(13),
    CORAL(14),
    CYAN(15),
    LAVENDER(16);

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