package com.greatorator.tolkienmobs.event.entity;

import com.greatorator.tolkienmobs.entity.boss.GoblinKingEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

public class ServerEvents {

    public static GoblinEvent.SummonAidEvent fireGoblinSummonAid(GoblinKingEntity goblin, Level world, int x, int y, int z, LivingEntity attacker, double summonChance) {
        GoblinEvent.SummonAidEvent summonEvent = new GoblinEvent.SummonAidEvent(goblin, world, x, y, z, attacker, summonChance);
        NeoForge.EVENT_BUS.post(summonEvent);
        return summonEvent;
    }

//    public static SpiderEvent.SummonAidEvent fireSpiderSummonAid(ShelobEntity spider, Level world, int x, int y, int z, LivingEntity attacker, double summonChance)
//    {
//        SpiderEvent.SummonAidEvent summonEvent = new SpiderEvent.SummonAidEvent(spider, world, x, y, z, attacker, summonChance);
//        MinecraftForge.EVENT_BUS.post(summonEvent);
//        return summonEvent;
//    }
}
