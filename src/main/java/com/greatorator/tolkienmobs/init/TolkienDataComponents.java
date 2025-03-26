package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.handler.data.TrinketComponent;
import com.greatorator.tolkienmobs.network.KeyCodeComponent;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.UnaryOperator;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> ITEMSTACK_HANDLER = COMPONENTS.register("itemstack_handler", () -> DataComponentType.<ItemContainerContents>builder().persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding().build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> HOBBIT_RING_ACTIVE = COMPONENTS.register("hobbit_ring_active", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> TRINKET_ACTIVE = COMPONENTS.register("trinket_active", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TrinketComponent>> POTION_CONTENTS = COMPONENTS.register("trinket_contents", () -> DataComponentType.<TrinketComponent>builder().persistent(TrinketComponent.CODEC).networkSynchronized(TrinketComponent.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> COIN_POUCH_ACTIVE = COMPONENTS.register("coin_pouch_active", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> KEY_RING_ACTIVE = COMPONENTS.register("key_ring_active", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<KeyCodeComponent>> KEY_CODE = register("key_code", builder -> builder.persistent(KeyCodeComponent.CODEC).networkSynchronized(KeyCodeComponent.STREAM_CODEC));

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<String>> CHEST_CODE = ATTACHMENT_TYPES.register(
            "chest_code", () -> AttachmentType.builder(() -> "Enter Code").serialize(Codec.STRING).build()
    );


    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return COMPONENTS.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
        DATA_COMPONENTS.register(eventBus);
        ATTACHMENT_TYPES.register(eventBus);
    }
}
