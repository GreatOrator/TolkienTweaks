package com.greatorator.tolkientweaks.datagen;

import com.greatorator.tolkientweaks.TolkienTweaks;
import com.greatorator.tolkientweaks.datagen.loot.LootGenerator;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

/**
 * Created by brandon3055 on 26/2/20.
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEventHandler {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            gen.addProvider(new LangGenerator(gen));
            gen.addProvider(new BlockStateGenerator(gen, event.getExistingFileHelper()));
            gen.addProvider(new ItemModelGenerator(gen, event.getExistingFileHelper()));
        }

        if (event.includeServer()) {
            gen.addProvider(new RecipeGenerator(gen));
            gen.addProvider(new LootGenerator(gen));

            //TODO
            BlockTagGenerator blockGenerator = new BlockTagGenerator(gen, TolkienTweaks.MODID, event.getExistingFileHelper());
            gen.addProvider(blockGenerator);
            gen.addProvider(new ItemTagGenerator(gen, blockGenerator, TolkienTweaks.MODID, event.getExistingFileHelper()));
        }
    }
}
