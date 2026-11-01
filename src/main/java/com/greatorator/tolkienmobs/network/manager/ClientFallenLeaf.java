package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

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
            // The plain maple leaf, for the reason the block itself gives: a tinted blob cannot be
            // handed to a particle type that was registered without one.
            level.addParticle(TolkienParticleTypes.DWARVEN_MAPLE_LEAVES.get(),
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
