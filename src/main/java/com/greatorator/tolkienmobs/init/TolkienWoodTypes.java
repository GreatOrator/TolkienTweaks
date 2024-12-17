package com.greatorator.tolkienmobs.init;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienWoodTypes {
    public static final BlockSetType MALLORN_WOOD_SET_TYPE = BlockSetType.register(new BlockSetType(MODID + ":mallorn"));
    public static final BlockSetType MIRKWOOD_WOOD_SET_TYPE = BlockSetType.register(new BlockSetType(MODID + ":mirkwood"));
    public static final BlockSetType CULUMALDA_WOOD_SET_TYPE = BlockSetType.register(new BlockSetType(MODID + ":culumalda"));
    public static final BlockSetType LEBETHRON_WOOD_SET_TYPE = BlockSetType.register(new BlockSetType(MODID + ":lebethron"));
    public static final BlockSetType FANGORNOAK_WOOD_SET_TYPE = BlockSetType.register(new BlockSetType(MODID + ":fangornoak"));
    public static final BlockSetType DEADWOOD_WOOD_SET_TYPE = BlockSetType.register(new BlockSetType(MODID + ":deadwood"));
    public static final BlockSetType DWARVEN_MAPLE_WOOD_SET_TYPE = BlockSetType.register(new BlockSetType(MODID + ":dwarven_maple"));

    public static final WoodType MALLORN = WoodType.register(new WoodType(MODID + ":mallorn", MALLORN_WOOD_SET_TYPE));
    public static final WoodType MIRKWOOD = WoodType.register(new WoodType(MODID + ":mirkwood", MIRKWOOD_WOOD_SET_TYPE));
    public static final WoodType CULUMALDA = WoodType.register(new WoodType(MODID + ":culumalda", CULUMALDA_WOOD_SET_TYPE));
    public static final WoodType LEBETHRON = WoodType.register(new WoodType(MODID + ":lebethron", LEBETHRON_WOOD_SET_TYPE));
    public static final WoodType FANGORNOAK = WoodType.register(new WoodType(MODID + ":fangornoak", FANGORNOAK_WOOD_SET_TYPE));
    public static final WoodType DEADWOOD = WoodType.register(new WoodType(MODID + ":deadwood", DEADWOOD_WOOD_SET_TYPE));
    public static final WoodType DWARVEN_MAPLE = WoodType.register(new WoodType(MODID + ":dwarven_maple", DWARVEN_MAPLE_WOOD_SET_TYPE));
}
