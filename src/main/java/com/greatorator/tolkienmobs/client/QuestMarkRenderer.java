package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.faction.QuestLedger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.TriState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The question mark over a giver with work.
 * <p>
 * It is drawn as part of the nameplate rather than as a billboard of its own: a nameplate already sits
 * over the head, already turns to face the player and is already hidden behind walls and at distance,
 * so a mark written onto it inherits all of that without a texture or a second render pass. The mark
 * is the first thing on the plate, which reads as a question mark above the name.
 * <p>
 * The client knows which givers to mark from the quest sync, so this is drawn from the same cache the
 * book is.
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class QuestMarkRenderer {

    private QuestMarkRenderer() {
    }

    @SubscribeEvent
    public static void onNameTag(RenderNameTagEvent.CanRender event) {
        if (!QuestLedger.clientHasOffer(event.getEntity().getUUID())) return;
        Component name = event.getContent();
        Component mark = Component.literal("? ").withStyle(ChatFormatting.GOLD);
        event.setContent(name == null ? mark : mark.copy().append(name));
        // A giver's plate is normally shown because its name is visible; the mark is worth showing even
        // where the name would be hidden, since the whole point is to be noticed.
        event.setCanRender(TriState.TRUE);
    }
}
