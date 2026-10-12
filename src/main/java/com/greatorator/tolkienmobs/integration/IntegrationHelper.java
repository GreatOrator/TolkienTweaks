package com.greatorator.tolkienmobs.integration;

import net.neoforged.fml.ModList;

public class IntegrationHelper {
    public static boolean isCuriosInstalled;
    public static boolean isJEIInstalled;

    public static void init(){
        isCuriosInstalled = ModList.get().isLoaded("curios");
        isJEIInstalled = ModList.get().isLoaded("jei");
    }
}