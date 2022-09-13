package com.greatorator.tolkientweaks.item;

import com.greatorator.tolkientweaks.container.CoinPouchContainer;
import com.greatorator.tolkientweaks.container.InventoryDynamicItemStack;
import com.greatorator.tolkientweaks.container.capability.CapabilityProviderCoinPouch;
import com.greatorator.tolkientweaks.handler.LoreItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.greatorator.tolkientweaks.TolkienTweaks.LOGGER;

public class CoinPouchItem extends LoreItem {
    private final String BASE_NBT_TAG = "base";
    private final String CAPABILITY_NBT_TAG = "cap";

    public CoinPouchItem(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
        ItemStack stack = playerIn.getItemInHand(hand);
        if (!worldIn.isClientSide) {
            INamedContainerProvider containerProviderFlowerBag = new ContainerProviderCoinPouch(this, stack);
            final int NUMBER_OF_FLOWER_SLOTS = 16;
            NetworkHooks.openGui((ServerPlayerEntity) playerIn,
                    containerProviderFlowerBag,
                    (packetBuffer)->{packetBuffer.writeInt(NUMBER_OF_FLOWER_SLOTS);});

        }
        return ActionResult.success(stack);
    }
    @Nonnull
    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
        World world = ctx.getLevel();
        if (world.isClientSide()) return ActionResultType.PASS;

        BlockPos pos = ctx.getClickedPos();
        Direction side = ctx.getClickedFace();
        ItemStack itemStack = ctx.getItemInHand();
        if (!(itemStack.getItem() instanceof CoinPouchItem)) throw new AssertionError("Unexpected ItemFlowerBag type");
        CoinPouchItem itemCoinPouch = (CoinPouchItem)itemStack.getItem();

        TileEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity == null) return ActionResultType.PASS;
        if (world.isClientSide()) return ActionResultType.SUCCESS; // always succeed on client side

        IItemHandler tileInventory;
        LazyOptional<IItemHandler> capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        if (capability.isPresent()) {
            tileInventory = capability.orElseThrow(AssertionError::new);
        } else if (tileEntity instanceof IInventory) {
            tileInventory = new InvWrapper((IInventory)tileEntity);
        } else {
            return ActionResultType.FAIL;
        }

        InventoryDynamicItemStack itemStackHandlerCoinPouch =  itemCoinPouch.getItemStackHandlerCoinPouch(itemStack);
        for (int i = 0; i < itemStackHandlerCoinPouch.getSlots(); i++) {
            ItemStack flower = itemStackHandlerCoinPouch.getStackInSlot(i);
            ItemStack flowersWhichDidNotFit = ItemHandlerHelper.insertItemStacked(tileInventory, flower, false);
            itemStackHandlerCoinPouch.setStackInSlot(i, flowersWhichDidNotFit);
        }
        tileEntity.setChanged();           // make sure that the tileEntity knows we have changed its contents

        CompoundNBT nbt = itemStack.getOrCreateTag();
        int dirtyCounter = nbt.getInt("dirtyCounter");
        nbt.putInt("dirtyCounter", dirtyCounter + 1);
        itemStack.setTag(nbt);

        return ActionResultType.SUCCESS;
    }

    private static class ContainerProviderCoinPouch implements INamedContainerProvider {
        public ContainerProviderCoinPouch(CoinPouchItem itemCoinPouch, ItemStack itemStackCoinPouch) {
            this.itemStackCoinPouch = itemStackCoinPouch;
            this.itemCoinPouch = itemCoinPouch;
        }

        @Override
        public ITextComponent getDisplayName() {
            return itemStackCoinPouch.getDisplayName();
        }

        @Override
        public CoinPouchContainer createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            CoinPouchContainer newContainerServerSide =
                    CoinPouchContainer.createContainerServerSide(windowID, playerInventory,
                            itemCoinPouch.getItemStackHandlerCoinPouch(itemStackCoinPouch),
                            itemStackCoinPouch);
            return newContainerServerSide;
        }

        private CoinPouchItem itemCoinPouch;
        private ItemStack itemStackCoinPouch;
    }

    @Nonnull
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT oldCapNbt) {

        return new CapabilityProviderCoinPouch();
    }

    private static InventoryDynamicItemStack getItemStackHandlerCoinPouch(ItemStack itemStack) {
        IItemHandler coinPouch = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (coinPouch == null || !(coinPouch instanceof InventoryDynamicItemStack)) {
            LOGGER.error("Coin Pouch did not have the expected ITEM_HANDLER_CAPABILITY");
            return new InventoryDynamicItemStack(1);
        }
        return (InventoryDynamicItemStack)coinPouch;
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT baseTag = stack.getTag();
        InventoryDynamicItemStack itemStackHandlerCoinPouch = getItemStackHandlerCoinPouch(stack);
        CompoundNBT capabilityTag = itemStackHandlerCoinPouch.serializeNBT();
        CompoundNBT combinedTag = new CompoundNBT();
        if (baseTag != null) {
            combinedTag.put(BASE_NBT_TAG, baseTag);
        }
        if (capabilityTag != null) {
            combinedTag.put(CAPABILITY_NBT_TAG, capabilityTag);
        }
        return combinedTag;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        if (nbt == null) {
            stack.setTag(null);
            return;
        }
        CompoundNBT baseTag = nbt.getCompound(BASE_NBT_TAG);              // empty if not found
        CompoundNBT capabilityTag = nbt.getCompound(CAPABILITY_NBT_TAG); // empty if not found
        stack.setTag(baseTag);
        InventoryDynamicItemStack itemStackHandlerFlowerBag = getItemStackHandlerCoinPouch(stack);
        itemStackHandlerFlowerBag.deserializeNBT(capabilityTag);
    }

    public static float getFullnessPropertyOverride(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        InventoryDynamicItemStack flowerBag = getItemStackHandlerCoinPouch(itemStack);
        float fractionEmpty = flowerBag.getNumberOfEmptySlots() / (float)flowerBag.getSlots();
        return 1.0F - fractionEmpty;
    }

}
