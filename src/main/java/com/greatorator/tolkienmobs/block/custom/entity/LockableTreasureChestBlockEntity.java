package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.block.custom.LockableTreasureChestBlock;
import com.greatorator.tolkienmobs.containers.KeyItemContainer;
import com.greatorator.tolkienmobs.containers.LockableTreasureChestContainer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienDataComponents;
import com.greatorator.tolkienmobs.item.custom.KeyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.keyframe.event.builtin.AutoPlayingSoundKeyframeHandler;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LockableTreasureChestBlockEntity extends BlockEntity implements MenuProvider, GeoBlockEntity {
    private static final RawAnimation CHEST_OPEN = RawAnimation.begin().thenPlay("open");
    private AnimationController<LockableTreasureChestBlockEntity> animationController;
    private static String keyCode;


    public LockableTreasureChestBlockEntity(BlockPos pos, BlockState blockState) {
        super(TolkienBlocks.LOCKABLE_TREASURE_CHEST_BLOCK_ENTITY.get(), pos, blockState);
        keyCode = this.getData(TolkienDataComponents.CHEST_CODE);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.tolkienmobs.block.tolkienmobs.lockable_treasure_chest_block");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new LockableTreasureChestContainer(pContainerId, pPlayerInventory, this);
    }

    public static void setKeyCode(LockableTreasureChestBlockEntity entity, String text) {
        keyCode = entity.setData(TolkienDataComponents.CHEST_CODE, text);
    }

    public String getKeyCode() {
        return this.getData(TolkienDataComponents.CHEST_CODE);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag nbt = this.saveWithoutMetadata(pRegistries);
        nbt.putString("chest_code", keyCode);

        return nbt;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.putString("chest_code", keyCode);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        keyCode = String.valueOf(pTag.get("chest_code"));
    }

    public void onRightClick(BlockState state, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        updateBlockState(state, Boolean.FALSE);

        if (stack.getItem() instanceof KeyItem && (KeyItem.getKeyCode(stack).equals(getKeyCode()))) {
            int uses = KeyItem.getUses(stack);
            boolean mode = KeyItem.getMode(stack);

            if (mode) {
                if (KeyItem.getUses(stack) >= 0) {
                    this.level.sendBlockUpdated(worldPosition, state, state, 3);

                    if (uses == 0) {
                        stack.shrink(1);
                        this.level.playSound((Player) null, worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.3F, 0.6F);
                        player.sendSystemMessage(Component.translatable("tolkienmobs.msg.key_used").withStyle(ChatFormatting.RED));
                    }
                    KeyItem.setKeyData(stack, KeyItem.getKeyCode(stack), uses - 1, KeyItem.getMode(stack));
                }
            }
            triggerAnim("popup_controller", "box_open");

            player.openMenu(new SimpleMenuProvider(
                    (windowId, playerInventory, playerEntity) -> new KeyItemContainer(windowId, playerInventory, player, stack), Component.translatable("screen.tolkienmobs." + stack.getDescriptionId())), (buf -> {
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
            }));
        } else {
            player.sendSystemMessage(Component.translatable("tolkienmobs.msg.wrong_key").withStyle(ChatFormatting.RED));
        }
    }

    /** Animations */
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        animationController = getController();
        controllers.add(animationController.setSoundKeyframeHandler(new AutoPlayingSoundKeyframeHandler<>()));
    }

    private AnimationController<LockableTreasureChestBlockEntity> getController() {
        return new AnimationController<>(this, "popup_controller", 0, state -> PlayState.STOP)
                .triggerableAnim("box_open", CHEST_OPEN);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    void updateBlockState(BlockState state, boolean open) {
        this.level.setBlock(this.getBlockPos(), (BlockState)state.setValue(LockableTreasureChestBlock.OPEN, open), 3);
    }

    public void markDirtyClient() {
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 3);
        }
    }
}