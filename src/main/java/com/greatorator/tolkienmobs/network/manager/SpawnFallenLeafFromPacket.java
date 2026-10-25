package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Tells a client to drop one falling leaf where an entity walked through a leaf pile.
 * <p>
 * The drawing is client only and lives in {@link ClientFallenLeaf}; this class carries nothing but
 * the position and the motion, so a server can register and read it without ever loading the client.
 */
public record SpawnFallenLeafFromPacket(BlockPos pos, Vec3 motion) implements CustomPacketPayload {

	public static final Type<SpawnFallenLeafFromPacket> TYPE = new Type<>(TolkienMobsMain.prefix("spawn_fallen_leaf"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnFallenLeafFromPacket> STREAM_CODEC = CustomPacketPayload.codec(SpawnFallenLeafFromPacket::write, SpawnFallenLeafFromPacket::new);

	public SpawnFallenLeafFromPacket(FriendlyByteBuf buf) {
		this(buf.readBlockPos(), new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(this.pos());
		buf.writeDouble(this.motion().x());
		buf.writeDouble(this.motion().y());
		buf.writeDouble(this.motion().z());
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SpawnFallenLeafFromPacket message, IPayloadContext ctx) {
		ClientFallenLeaf.spawn(message, ctx);
	}
}
