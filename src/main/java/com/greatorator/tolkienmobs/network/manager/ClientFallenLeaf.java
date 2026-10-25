package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.handler.LeafPileTint;
import com.greatorator.tolkienmobs.particle.data.LeafParticleData;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Random;

/**
 * Draws the falling leaf a block sends to the client that walks through a leaf pile.
 * <p>
 * The packet lives in the common source set - a server registers it too - but the work is client
 * only: it needs the game's running client for the block colours and a client level for the tint.
 * Naming either from the packet class kept the whole mod from loading on a dedicated server, so the
 * body sits here, where only the client ever loads it.
 */
public final class ClientFallenLeaf {
    private ClientFallenLeaf() {
    }

    public static void spawn(SpawnFallenLeafFromPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Level level = ctx.player().level();
            Random rand = new Random();
            int color = LeafPileTint.colour(level, message.pos());
            int r = Mth.clamp(((color >> 16) & 0xFF) + rand.nextInt(0x22) - 0x11, 0x00, 0xFF);
            int g = Mth.clamp(((color >> 8) & 0xFF) + rand.nextInt(0x22) - 0x11, 0x00, 0xFF);
            int b = Mth.clamp((color & 0xFF) + rand.nextInt(0x22) - 0x11, 0x00, 0xFF);
            level.addParticle(new LeafParticleData(r, g, b),
                    message.pos().getX() + level.getRandom().nextFloat(),
                    message.pos().getY(),
                    message.pos().getZ() + level.getRandom().nextFloat(),
                    (level.getRandom().nextFloat() * -0.5F) * message.motion().x(),
                    level.getRandom().nextFloat() * 0.5F + 0.25F,
                    (level.getRandom().nextFloat() * -0.5F) * message.motion().z()
            );
        });
    }
}
