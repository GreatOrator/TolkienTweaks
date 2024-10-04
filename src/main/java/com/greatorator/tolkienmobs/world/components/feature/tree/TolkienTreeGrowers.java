package com.greatorator.tolkienmobs.world.components.feature.tree;

import com.greatorator.tolkienmobs.datagen.world.TolkienWorldConfigurationProvider;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienTreeGrowers {
    public static final TreeGrower MALLORN = new TreeGrower(MODID + ":mallorn",
            Optional.empty(), Optional.of(TolkienWorldConfigurationProvider.MALLORN_KEY), Optional.empty());
    public static final TreeGrower MIRKWOOD = new TreeGrower(MODID + ":mirkwood",
            Optional.empty(), Optional.of(TolkienWorldConfigurationProvider.MIRKWOOD_KEY), Optional.empty());
    public static final TreeGrower CULUMALDA = new TreeGrower(MODID + ":culumalda",
            Optional.empty(), Optional.of(TolkienWorldConfigurationProvider.CULUMALDA_KEY), Optional.empty());
    public static final TreeGrower LEBETHRON = new TreeGrower(MODID + ":lebethron",
            Optional.empty(), Optional.of(TolkienWorldConfigurationProvider.LEBETHRON_KEY), Optional.empty());
    public static final TreeGrower FANGORNOAK = new TreeGrower(MODID + ":fangornoak",
            Optional.empty(), Optional.of(TolkienWorldConfigurationProvider.FANGORNOAK_KEY), Optional.empty());
    public static final TreeGrower DEADWOOD = new TreeGrower(MODID + ":deadwood",
            Optional.empty(), Optional.of(TolkienWorldConfigurationProvider.DEADWOOD_KEY), Optional.empty());
    public static final TreeGrower DWARVEN_MAPLE = new TreeGrower(MODID + ":dwarven_maple",
            Optional.empty(), Optional.of(TolkienWorldConfigurationProvider.DWARVEN_MAPLE_KEY), Optional.empty());
    public static final TreeGrower MUSHROOM_DECAY_BLOOM = new TreeGrower(MODID + ":bloomdecay",
            Optional.empty(), Optional.of(TolkienWorldConfigurationProvider.MUSHROOM_DECAY_BLOOM_KEY), Optional.empty());
    public static final TreeGrower MUSHROOM_BLOOM_DECAY = new TreeGrower(MODID + ":decaybloom",
            Optional.empty(), Optional.of(TolkienWorldConfigurationProvider.MUSHROOM_BLOOM_DECAY_KEY), Optional.empty());
}