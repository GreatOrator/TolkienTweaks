package com.greatorator.tolkienmobs.event.client;

import com.greatorator.tolkienmobs.block.custom.entity.function.ChameleonBlockDynamicBakedModel;
import com.greatorator.tolkienmobs.handler.LeafPileTint;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.integration.TolkienSyncedRecipes;
import com.greatorator.tolkienmobs.item.custom.PipeItem;
import com.greatorator.tolkienmobs.util.pipeweed.PipeweedStrain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class TolkienClientEvents {

    /**
     * Hands the leaf piles their colour. The lookup belongs here, where client classes are allowed,
     * rather than in the block: a server has no block-colour registry, and a block that named one
     * could not load there at all.
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LeafPileTint.install(TolkienClientEvents::oakLeafColour);
    }

    @SuppressWarnings("deprecation")
    private static int oakLeafColour(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        BlockState oak = Blocks.OAK_LEAVES.defaultBlockState();
        BlockTintSource tint = Minecraft.getInstance().getBlockColors().getTintSource(oak, 0);
        return tint != null && level instanceof ClientLevel clientLevel ? tint.colorInWorld(oak, clientLevel, pos) : LeafPileTint.NO_TINT;
    }

    @SubscribeEvent
    public static void onComputeFovModifierEvent(ComputeFovModifierEvent event) {
        if(event.getPlayer().isUsingItem() && event.getPlayer().getUseItem().getItem() == TolkienItems.ELVEN_BOW.get()) {
            float fovModifier = 1f;
            int ticksUsingItem = event.getPlayer().getTicksUsingItem();
            float deltaTicks = (float)ticksUsingItem / 20f;
            if(deltaTicks > 1f) {
                deltaTicks = 1f;
            } else {
                deltaTicks *= deltaTicks;
            }
            fovModifier *= 1f - deltaTicks * 0.15f;
            event.setNewFovModifier(fovModifier);
        }
        if(event.getPlayer().isUsingItem() && event.getPlayer().getUseItem().getItem() == TolkienItems.URUK_BOW.get()) {
            float fovModifier = 1f;
            int ticksUsingItem = event.getPlayer().getTicksUsingItem();
            float deltaTicks = (float)ticksUsingItem / 20f;
            if(deltaTicks > 1f) {
                deltaTicks = 1f;
            } else {
                deltaTicks *= deltaTicks;
            }
            fovModifier *= 1f - deltaTicks * 0.15f;
            event.setNewFovModifier(fovModifier);
        }
    }

    /**
     * Camo blocks only rebuild when their section is dirtied, so the hands are watched here and a change of item
     * rebuilds the sections that hold one. See {@link ChameleonBlockDynamicBakedModel#refreshRevealedBlocks()}.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ChameleonBlockDynamicBakedModel.refreshRevealedBlocks();
    }

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        TolkienSyncedRecipes.accept(event.getRecipeMap());
    }

    /**
     * Names the leaf in a pipe's bowl, or says the bowl is empty.
     *
     * <p>The bowl is drawn in the colour of the strain the smoker is carrying, so what is in it is a
     * question about the player rather than about the stack, and an item's own tooltip hook is handed
     * a context with no player in it at all. This event is the one place a tooltip can ask.
     */
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!(event.getItemStack().getItem() instanceof PipeItem pipe)) {
            return;
        }
        Player player = event.getEntity();
        PipeweedStrain strain = player == null || PipeItem.isSpent(event.getItemStack())
                ? null
                : PipeweedStrain.find(player);
        event.getToolTip().add(strain == null
                ? Component.translatable(pipe.getDescriptionId() + ".bowl_empty")
                : Component.translatable(pipe.getDescriptionId() + ".bowl",
                        Component.translatable(strain.leafItem().getDescriptionId())));
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        TolkienSyncedRecipes.clear();
    }

}
