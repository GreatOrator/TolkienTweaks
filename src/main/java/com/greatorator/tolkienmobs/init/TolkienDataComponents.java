package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.handler.data.CoinPouchContents;
import com.greatorator.tolkienmobs.handler.data.TrinketComponent;
import com.greatorator.tolkienmobs.network.KeyCodeComponent;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;
import java.util.function.UnaryOperator;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> ITEMSTACK_HANDLER = COMPONENTS.register("itemstack_handler", () -> DataComponentType.<ItemContainerContents>builder().persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding().build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> HOBBIT_RING_ACTIVE = COMPONENTS.register("hobbit_ring_active", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> TRINKET_ACTIVE = COMPONENTS.register("trinket_active", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TrinketComponent>> POTION_CONTENTS = COMPONENTS.register("trinket_contents", () -> DataComponentType.<TrinketComponent>builder().persistent(TrinketComponent.CODEC).networkSynchronized(TrinketComponent.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> COIN_POUCH_UUID = COMPONENTS.register("coin_pouch_uuid", () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> COIN_POUCH_ACTIVE = COMPONENTS.register("coin_pouch_active", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CoinPouchContents>> COIN_POUCH_CONTENTS = COMPONENTS.register("coin_pouch_contents", () -> DataComponentType.<CoinPouchContents>builder().persistent(CoinPouchContents.CODEC).networkSynchronized(CoinPouchContents.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> KEY_RING_UUID = COMPONENTS.register("key_ring_uuid", () -> DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> KEY_RING_ACTIVE = COMPONENTS.register("key_ring_active", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<KeyCodeComponent>> KEY_CODE = register("key_code", builder -> builder.persistent(KeyCodeComponent.CODEC).networkSynchronized(KeyCodeComponent.STREAM_CODEC));

    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return COMPONENTS.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
        DATA_COMPONENTS.register(eventBus);
    }

//    private static @NotNull <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, final Codec<T> codec) {
//        return register(name, codec, null);
//    }

//    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
//        var builder = DataComponentType.<T>builder();
//        customizer.accept(builder);
//        var componentType = builder.build();
//        COMPONENTS.register(name, () -> componentType);
//        return componentType;
//    }

//    private static @NotNull <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, final Codec<T> codec, @Nullable final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
//        if (streamCodec == null) {
//            return COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).build());
//        } else {
//            return COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
//        }
//    }
}
