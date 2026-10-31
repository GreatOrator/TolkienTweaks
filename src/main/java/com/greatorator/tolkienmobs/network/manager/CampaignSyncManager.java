package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.faction.Campaign;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Carries a player's three campaign stages from the server to that player alone. The mark over a hall
 * herald is drawn on the client, and it has to know when a front has nothing left to give, so the stage
 * travels with the same syncs the quest book does.
 */
public record CampaignSyncManager(int west, int east, int harad) implements CustomPacketPayload {
    public static final Type<CampaignSyncManager> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", "campaign_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CampaignSyncManager> STREAM_CODEC =
            CustomPacketPayload.codec(CampaignSyncManager::write, CampaignSyncManager::new);

    public CampaignSyncManager(RegistryFriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(west);
        buf.writeVarInt(east);
        buf.writeVarInt(harad);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CampaignSyncManager payload, IPayloadContext context) {
        Campaign.acceptClient(new int[]{payload.west(), payload.east(), payload.harad()});
    }
}
