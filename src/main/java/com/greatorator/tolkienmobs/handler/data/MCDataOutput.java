package com.greatorator.tolkienmobs.handler.data;

import com.greatorator.tolkienmobs.handler.vec.Cuboid6;
import com.greatorator.tolkienmobs.handler.vec.Vector3;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static com.greatorator.tolkienmobs.handler.data.DataUtils.checkLen;

public interface MCDataOutput {
    MCDataOutput writeByte(int b);

    MCDataOutput writeChar(int c);

    MCDataOutput writeShort(int s);

    MCDataOutput writeInt(int i);

    MCDataOutput writeLong(long l);

    MCDataOutput writeFloat(float f);

    MCDataOutput writeDouble(double d);

    MCDataOutput writeBoolean(boolean b);

    default MCDataOutput writeBytes(byte[] b) {
        return writeBytes(b, 0, b.length);
    }

    default MCDataOutput writeBytes(byte[] b, int off, int len) {
        Objects.requireNonNull(b);
        checkLen(b.length, off, len);
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeByte(b[off + i]);
        }
        return this;
    }

    default MCDataOutput writeChars(char[] c) {
        return writeChars(c, 0, c.length);
    }

    default MCDataOutput writeChars(char[] c, int off, int len) {
        Objects.requireNonNull(c);
        checkLen(c.length, off, len);
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeChar(c[off + i]);
        }
        return this;
    }

    default MCDataOutput writeShorts(short[] s) {
        return writeShorts(s, 0, s.length);
    }

    default MCDataOutput writeShorts(short[] s, int off, int len) {
        Objects.requireNonNull(s);
        checkLen(s.length, off, len);
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeShort(s[off + i]);
        }
        return this;
    }

    default MCDataOutput writeInts(int[] i) {
        return writeInts(i, 0, i.length);
    }

    default MCDataOutput writeInts(int[] i, int off, int len) {
        Objects.requireNonNull(i);
        checkLen(i.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeInt(i[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeLongs(long[] l) {
        return writeLongs(l, 0, l.length);
    }

    default MCDataOutput writeLongs(long[] l, int off, int len) {
        Objects.requireNonNull(l);
        checkLen(l.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeLong(l[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeFloats(float[] f) {
        return writeFloats(f, 0, f.length);
    }

    default MCDataOutput writeFloats(float[] f, int off, int len) {
        Objects.requireNonNull(f);
        checkLen(f.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeFloat(f[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeDoubles(double[] d) {
        return writeDoubles(d, 0, d.length);
    }

    default MCDataOutput writeDoubles(double[] d, int off, int len) {
        Objects.requireNonNull(d);
        checkLen(d.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeDouble(d[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeBooleans(boolean[] b) {
        return writeBooleans(b, 0, b.length);
    }

    default MCDataOutput writeBooleans(boolean[] b, int off, int len) {
        Objects.requireNonNull(b);
        checkLen(b.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeBoolean(b[off + i2]);
        }
        return this;
    }

    default MCDataOutput append(byte[] bytes) {
        for (byte b : bytes) {
            writeByte(b);
        }
        return this;
    }

    default MCDataOutput writeVarInt(int i) {
        while ((i & 0xffffff80) != 0) {
            writeByte(i & 0x7f | 0x80);
            i >>>= 7;
        }

        writeByte(i);
        return this;
    }

    default MCDataOutput writeVarLong(long l) {
        while ((l & 0xffffffffffffff80L) != 0L) {
            writeByte((int) (l & 0x7fL) | 0x80);
            l >>>= 7;
        }
        writeByte((int) l);
        return this;
    }

    default MCDataOutput writeSignedVarInt(int i) {
        return writeVarInt(i >= 0 ? 2 * i : -2 * (i + 1) + 1);
    }

    default MCDataOutput writeSignedVarLong(long i) {
        return writeVarLong(i >= 0 ? 2 * i : -2 * (i + 1) + 1);
    }

    default MCDataOutput writeVarInts(int[] i) {
        return writeVarInts(i, 0, i.length);
    }

    default MCDataOutput writeVarInts(int[] i, int off, int len) {
        Objects.requireNonNull(i);
        checkLen(i.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeVarInt(i[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeVarLongs(long[] l) {
        return writeVarLongs(l, 0, l.length);
    }

    default MCDataOutput writeVarLongs(long[] l, int off, int len) {
        Objects.requireNonNull(l);
        checkLen(l.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeVarLong(l[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeSignedVarInts(int[] i) {
        return writeSignedVarInts(i, 0, i.length);
    }

    default MCDataOutput writeSignedVarInts(int[] i, int off, int len) {
        Objects.requireNonNull(i);
        checkLen(i.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeSignedVarInt(i[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeSignedVarLongs(long[] l) {
        return writeSignedVarLongs(l, 0, l.length);
    }

    default MCDataOutput writeSignedVarLongs(long[] l, int off, int len) {
        Objects.requireNonNull(l);
        checkLen(l.length, off, len);
        writeVarInt(len);
        for (int i2 = 0; i2 < len; i2++) {
            writeSignedVarLong(l[off + i2]);
        }
        return this;
    }

    default MCDataOutput writeString(String s) {
        return writeString(s, 32767);
    }

    default MCDataOutput writeString(String s, int maxLen) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > maxLen) {
            throw new EncoderException("String too big. Encoded: " + bytes.length + " Max: " + maxLen);
        }
        writeBytes(bytes);
        return this;
    }

    default MCDataOutput writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    default MCDataOutput writeEnum(Enum<?> value) {
        writeVarInt(value.ordinal());
        return this;
    }

    default MCDataOutput writeByteBuffer(ByteBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeByte(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeCharBuffer(CharBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeChar(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeShortBuffer(ShortBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeShort(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeIntBuffer(IntBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeInt(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeLongBuffer(LongBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeLong(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeFloatBuffer(FloatBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeFloat(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeDoubleBuffer(DoubleBuffer buffer) {
        int len = buffer.remaining();
        writeVarInt(len);
        for (int i = 0; i < len; i++) {
            writeDouble(buffer.get());
        }
        return this;
    }

    default MCDataOutput writeVector(Vector3 vec) {
        writeDouble(vec.x);
        writeDouble(vec.y);
        writeDouble(vec.z);
        return this;
    }

    default MCDataOutput writeCuboid(Cuboid6 cuboid) {
        writeVector(cuboid.min);
        writeVector(cuboid.max);
        return this;
    }

    default MCDataOutput writeResourceLocation(ResourceLocation loc) {
        return writeString(loc.toString());
    }

    default MCDataOutput writeDirection(Direction dir) {
        return writeEnum(dir);
    }

    default MCDataOutput writePos(BlockPos pos) {
        return writeVec3i(pos);
    }

    default MCDataOutput writeVec3i(Vec3i vec) {
        writeSignedVarInt(vec.getX());
        writeSignedVarInt(vec.getY());
        writeSignedVarInt(vec.getZ());
        return this;
    }

    default MCDataOutput writeVec3f(Vector3f vec) {
        writeFloat(vec.x());
        writeFloat(vec.y());
        writeFloat(vec.z());
        return this;
    }

    default MCDataOutput writeVec3d(Vec3 vec) {
        writeDouble(vec.x);
        writeDouble(vec.y);
        writeDouble(vec.z);
        return this;
    }

    default MCDataOutput writeCompoundNBT(CompoundTag tag) {
        try {
            NbtIo.write(tag, toDataOutput());
        } catch (IOException e) {
            throw new EncoderException("Failed to write CompoundNBT to stream.", e);
        }
        return this;
    }

    default MCDataOutput writeNullableCompoundNBT(@Nullable CompoundTag tag) {
        if (tag == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            writeCompoundNBT(tag);
        }
        return this;
    }

    default MCDataOutput writeItemStack(ItemStack stack) {
        return writeWithRegistryCodec(ItemStack.OPTIONAL_STREAM_CODEC, stack);
    }

    default MCDataOutput writeFluidStack(FluidStack stack) {
        return writeWithRegistryCodec(FluidStack.OPTIONAL_STREAM_CODEC, stack);
    }

    default MCDataOutput writeTextComponent(Component component) {
        return writeString(Component.Serializer.toJson(component, RegistryAccess.EMPTY), 262144);//32kb
    }

    default <T> MCDataOutput writeRegistryIdDirect(Registry<T> registry, T entry) {
        writeVarInt(registry.getId(entry));
        return this;
    }

    default <T> MCDataOutput writeRegistryIdDirect(Registry<T> registry, ResourceLocation entry) {
        writeVarInt(registry.getId(entry));
        return this;
    }

    default <T> MCDataOutput writeRegistryId(Registry<T> registry, T entry) {
        ResourceLocation rName = registry.key().location();
        if (!registry.containsValue(entry)) {
            throw new IllegalArgumentException(String.format("Registry '%s' does not contain entry '%s'", rName, entry));
        }
        writeResourceLocation(rName);
        writeRegistryIdDirect(registry, entry);
        return this;
    }

    default <T> MCDataOutput writeRegistryId(Registry<T> registry, ResourceLocation entry) {
        ResourceLocation rName = registry.key().location();
        if (!registry.containsKey(entry)) {
            throw new IllegalArgumentException(String.format("Registry '%s' does not contain entry '%s'", rName, entry));
        }
        writeResourceLocation(rName);
        writeRegistryIdDirect(registry, entry);
        return this;
    }

    default <T> MCDataOutput writeWithCodec(StreamEncoder<? super FriendlyByteBuf, T> codec, T thing) {
        throw new RuntimeException("Only able to use StreamCodec's with PacketCustom instances.");
    }

    default <T> MCDataOutput writeWithRegistryCodec(StreamEncoder<? super RegistryFriendlyByteBuf, T> codec, T thing) {
        throw new RuntimeException("Only able to use StreamCodec's with PacketCustom instances.");
    }

    default MCDataOutput writeByteBuf(ByteBuf buf) {
        byte[] arr = new byte[buf.readableBytes()];
        buf.readBytes(arr);
        return writeBytes(arr);
    }

    default MCDataOutput append(ByteBuf buf) {
        byte[] arr = new byte[buf.readableBytes()];
        buf.readBytes(arr);
        return append(arr);
    }

    default DataOutput toDataOutput() {
        return new DataOutputStream(toOutputStream());
    }

    default OutputStream toOutputStream() {
        return new OutputStreamWrapper(this);
    }

    final class OutputStreamWrapper extends OutputStream {

        private final MCDataOutput out;

        public OutputStreamWrapper(MCDataOutput out) {
            this.out = out;
        }

        @Override
        public void write(int b) {
            out.writeByte(b);
        }
    }
}