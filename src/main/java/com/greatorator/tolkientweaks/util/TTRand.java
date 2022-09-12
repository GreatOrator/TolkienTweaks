package com.greatorator.tolkientweaks.util;

public class TTRand {
    public static int getRandom(int max){
        return (int) (Math.random()*max);
    }

    public static int getRandomInteger(int maximum, int minimum){
        return ((int) (Math.random()*(maximum - minimum))) + minimum;
    }

    public static float degToRad(float degrees)
    {
        return degrees * (float)Math.PI / 180 ;
    }
}
