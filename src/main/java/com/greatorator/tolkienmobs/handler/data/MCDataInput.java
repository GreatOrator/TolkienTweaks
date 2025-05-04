package com.greatorator.tolkienmobs.handler.data;

import com.greatorator.tolkienmobs.handler.vec.Cuboid6;
import com.greatorator.tolkienmobs.handler.vec.Vector3;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.greatorator.tolkienmobs.util.SneakyUtils.unsafeCast;
import static java.util.Objects.requireNonNull;

public interface MCDataInput {

    //region Primitives

    byte readByte();

    short readUByte();

    char readChar();

    short readShort();

    int readUShort();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    boolean readBoolean();

    default byte[] readBytes() {
        int len = readVarInt();
        byte[] arr = new byte[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readByte();
        }
        return arr;
    }

    default char[] readChars() {
        int len = readVarInt();
        char[] arr = new char[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readChar();
        }
        return arr;
    }

    default short[] readShorts() {
        int len = readVarInt();
        short[] arr = new short[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readShort();
        }
        return arr;
    }

    default int[] readInts() {
        int len = readVarInt();
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readInt();
        }
        return arr;
    }

    default long[] readLongs() {
        int len = readVarInt();
        long[] arr = new long[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readLong();
        }
        return arr;
    }

    default float[] readFloats() {
        int len = readVarInt();
        float[] arr = new float[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readFloat();
        }
        return arr;
    }

    default double[] readDoubles() {
        int len = readVarInt();
        double[] arr = new double[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readDouble();
        }
        return arr;
    }

    default boolean[] readBooleans() {
        int len = readVarInt();
        boolean[] arr = new boolean[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readBoolean();
        }
        return arr;
    }

    default int readVarInt() {
        int i = 0;
        int j = 0;
        byte b0;

        do {
            b0 = readByte();
            i |= (b0 & 0x7f) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        }
        while ((b0 & 0x80) == 0x80);

        return i;
    }

    default long readVarLong() {
        long i = 0L;
        int j = 0;
        byte b0;

        do {
            b0 = readByte();
            i |= (long) (b0 & 0x7f) << j++ * 7;
            if (j > 10) {
                throw new RuntimeException("VarLong too big");
            }

        }
        while ((b0 & 0x80) == 0x80);

        return i;
    }

    default int readSignedVarInt() {
        int i = readVarInt();
        return (i & 1) == 0 ? i >>> 1 : -(i >>> 1) - 1;
    }

    default long readSignedVarLong() {
        long i = readVarLong();
        return (i & 1) == 0 ? i >>> 1 : -(i >>> 1) - 1;
    }

    default int[] readVarInts() {
        int len = readVarInt();
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readVarInt();
        }
        return arr;
    }

    default long[] readVarLongs() {
        int len = readVarInt();
        long[] arr = new long[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readVarLong();
        }
        return arr;
    }

    default int[] readSignedVarInts() {
        int len = readVarInt();
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readSignedVarInt();
        }
        return arr;
    }

    default long[] readSignedVarLongs() {
        int len = readVarInt();
        long[] arr = new long[len];
        for (int i = 0; i < len; i++) {
            arr[i] = readSignedVarLong();
        }
        return arr;
    }

    default String readString() {
        return new String(readBytes(), StandardCharsets.UTF_8);
    }

    default UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    default <T extends Enum<T>> T readEnum(Class<T> clazz) {
        return clazz.getEnumConstants()[readVarInt()];
    }

    default ByteBuffer readByteBuffer() {
        return ByteBuffer.wrap(readBytes());
    }

    default CharBuffer readCharBuffer() {
        return CharBuffer.wrap(readChars());
    }

    default ShortBuffer readShortBuffer() {
        return ShortBuffer.wrap(readShorts());
    }

    default IntBuffer readIntBuffer() {
        return IntBuffer.wrap(readInts());
    }

    default LongBuffer readLongBuffer() {
        return LongBuffer.wrap(readLongs());
    }

    default FloatBuffer readFloatBuffer() {
        return FloatBuffer.wrap(readFloats());
    }

    default DoubleBuffer readDoubleBuffer() {
        return DoubleBuffer.wrap(readDoubles());
    }

    default Vector3 readVector() {
        return new Vector3(readDouble(), readDouble(), readDouble());
    }

    default Cuboid6 readCuboid() {
        return new Cuboid6(readVector(), readVector());
    }

    default ResourceLocation readResourceLocation() {
        return ResourceLocation.parse(readString());
    }

    default Direction readDirection() {
        return readEnum(Direction.class);
    }

    default BlockPos readPos() {
        return new BlockPos(readSignedVarInt(), readSignedVarInt(), readSignedVarInt());
    }

    default Vec3i readVec3i() {
        return readPos();
    }

    default Vec3 readVec3d() {
        return new Vec3(readDouble(), readDouble(), readDouble());
    }

    default Vector3f readVec3f() {
        return new Vector3f(readFloat(), readFloat(), readFloat());
    }

    default CompoundTag readCompoundNBT() {
        try {
            return NbtIo.read(toDataInput(), NbtAccounter.create(2097152L));
        } catch (IOException e) {
            throw new EncoderException("Failed to read CompoundNBT from stream.", e);
        }
    }

    default @Nullable CompoundTag readNullableCompoundNBT() {
        if (!readBoolean()) return null;

        return readCompoundNBT();
    }

    default ItemStack readItemStack() {
        return readWithRegistryCodec(ItemStack.OPTIONAL_STREAM_CODEC);
    }

    default FluidStack readFluidStack() {
        return readWithRegistryCodec(FluidStack.OPTIONAL_STREAM_CODEC);
    }

    default MutableComponent readTextComponent() {
        return requireNonNull(Component.Serializer.fromJson(readString(), RegistryAccess.EMPTY));
    }

    default <T> T readRegistryIdDirect(Registry<T> registry) {
        return requireNonNull(registry.byId(readVarInt()), "Registry entry did not exist.");
    }

    default <T> T readRegistryId() {
        ResourceLocation rName = readResourceLocation();
        return readRegistryIdDirect(unsafeCast(requireNonNull(BuiltInRegistries.REGISTRY.get(rName), "Registry " + rName + " not found.")));
    }

    default <T> T readWithCodec(StreamDecoder<? super FriendlyByteBuf, T> codec) {
        throw new RuntimeException("Only able to use StreamCodec's with PacketCustom instances.");
    }

    default <T> T readWithRegistryCodec(StreamDecoder<? super RegistryFriendlyByteBuf, T> codec) {
        throw new RuntimeException("Only able to use StreamCodec's with PacketCustom instances.");
    }

    default ByteBuf readByteBuf() {
        return Unpooled.wrappedBuffer(readBytes());
    }

    default DataInput toDataInput() {
        return new DataInputStream(toInputStream());
    }

    default InputStream toInputStream() {
        return new InputStreamWrapper(this);
    }

    final class InputStreamWrapper extends InputStream {

        private final MCDataInput in;

        public InputStreamWrapper(MCDataInput in) {
            this.in = in;
        }

        @Override
        public int read() {
            return in.readByte() & 0xFF;
        }
    }
}