package com.greatorator.tolkienmobs.world.tree;

import com.greatorator.tolkienmobs.world.TolkienConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienTreeGrowers {
    public static final TreeGrower MALLORN = new TreeGrower(MODID + ":mallorn",
            Optional.empty(), Optional.of(TolkienConfiguredFeatures.MALLORN_KEY), Optional.empty());
    public static final TreeGrower MIRKWOOD = new TreeGrower(MODID + ":mirkwood",
            Optional.empty(), Optional.of(TolkienConfiguredFeatures.MIRKWOOD_KEY), Optional.empty());
}