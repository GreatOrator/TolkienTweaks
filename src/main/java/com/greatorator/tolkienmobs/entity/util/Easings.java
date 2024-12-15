package com.greatorator.tolkienmobs.entity.util;

import net.minecraft.util.Mth;

/** Borrowed From TropiCraft */
public final class Easings {
    public static final float PI = (float) Math.PI;

    // https://easings.net/#easeInOutSine
    public static float inOutSine(float x) {
        return -(Mth.cos(PI * x) - 1.0f) / 2.0f;
    }
}
