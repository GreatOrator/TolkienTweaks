package com.greatorator.tolkienmobs.network.manager;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.particle.data.LeafParticleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Random;

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
		ctx.enqueueWork(() -> {
			Level level = ctx.player().level();
			Random rand = new Random();
			// 26.1 hands out a block's tint as a source rather than a colour: ask it for the leaf
			// tint it would draw, in this world, at this position. A block with no tint is -1, the
			// same default the old call gave, and the caller's fuzzing copes with it as before.
			BlockState leaves = Blocks.OAK_LEAVES.defaultBlockState();
			BlockTintSource tint = Minecraft.getInstance().getBlockColors().getTintSource(leaves, 0);
			int color = tint != null && level instanceof ClientLevel clientLevel
					? tint.colorInWorld(leaves, clientLevel, message.pos())
					: -1;
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
