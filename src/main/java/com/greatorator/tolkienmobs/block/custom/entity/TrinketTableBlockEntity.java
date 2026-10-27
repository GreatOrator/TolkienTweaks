package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.block.custom.TrinketTableBlock;
import com.greatorator.tolkienmobs.containers.TrinketTableContainer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienRecipesTypes;
import com.greatorator.tolkienmobs.item.custom.TrinketItem;
import com.greatorator.tolkienmobs.recipe.TrinketRecipe;
import com.greatorator.tolkienmobs.recipe.TrinketRecipeInput;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.greatorator.tolkienmobs.handler.capability.TolkienItemStackHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TrinketTableBlockEntity extends TolkienBlockEntity implements MenuProvider {
    public static final int TRINKET_SLOT = 0;
    public static final int POTION_SLOT = 1;
    public static final int GEM_SLOT = 2;
    public static final int OUTPUT_SLOT = 3;


    public final TolkienItemStackHandler itemHandler = new TolkienItemStackHandler(4) {
        @Override
        public boolean isValid(int slot, ItemResource resource) {
            if (resource.isEmpty()) return true;
            return switch (slot) {
                case TRINKET_SLOT -> resource.test(stack -> stack.getItem() instanceof TrinketItem || GeneralUtility.isRepairTarget(stack));
                case POTION_SLOT -> resource.test(stack -> stack.getItem() instanceof PotionItem
                        || GeneralUtility.isRepairMaterialFor(level, getStackInSlot(TRINKET_SLOT), stack));
                case GEM_SLOT -> resource.test(stack -> stack.is(TolkienItems.GEM_AMMOLITE.get()));
                // The output only ever changes through set, and its menu slot refuses placement on
                // its own, but 26.1 reads a capacity through isValid: a slot that refused every
                // resource reported a capacity of 0, so a finished trinket never landed here.
                case OUTPUT_SLOT -> true;
                default -> false;
            };
        }

        @Override
        protected int getCapacity(int slot, ItemResource resource) {
            return slot == GEM_SLOT ? 64 : 1;
        }

        /** The slot's stack, for the checks above. */
        private ItemStack getStackInSlot(int slot) {
            return ItemUtil.getStack(this, slot);
        }

        @Override
        protected void onContentsChanged(int slot, ItemStack stack) {
            if (slot != OUTPUT_SLOT) {
                progress = 0;
            }
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = TolkienMobsConfig.trinketCraftTime();
    private float rotation;

    public TrinketTableBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(TolkienBlocks.TRINKET_TABLE_BLOCK_ENTITY.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> TrinketTableBlockEntity.this.progress;
                    case 1 -> TrinketTableBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> TrinketTableBlockEntity.this.progress = pValue;
                    case 1 -> TrinketTableBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.tolkienmobs.block.tolkienmobs.trinket_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new TrinketTableContainer(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        itemHandler.serialize(output.child("inventory"));
        output.putInt("progress", progress);
        output.putInt("max_progress", maxProgress);

        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        itemHandler.deserialize(input.childOrEmpty("inventory"));
        progress = input.getIntOr("progress", 0);
        maxProgress = input.getInt("max_progress").isPresent() ? Math.max(1, input.getIntOr("max_progress", 0)) : TolkienMobsConfig.trinketCraftTime();
    }

    /**
     * 26.1 calls this instead of the block's removed {@code onRemove}, which is where the table's
     * four slots are dropped.
     */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        this.drops();
        super.preRemoveSideEffects(pos, state);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.size());
        for(int i = 0; i < itemHandler.size(); i++) {
            inv.setItem(i, ItemUtil.getStack(itemHandler, i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }
        TrinketRecipeInput input = getRecipeInput();
        Optional<RecipeHolder<TrinketRecipe>> recipe = level.getServer().getRecipeManager()
                .getRecipeFor(TolkienRecipesTypes.TRINKET_TABLE_TYPE.get(), input, level);
        ItemStack result = recipe.map(holder -> holder.value().assemble(input))
                .orElse(ItemStack.EMPTY);
        // With no trinket recipe to run, a damaged tool, its repair material and a gem repair the
        // tool where it lies; the trinket recipe hands its result over to the output slot instead.
        boolean repairing = result.isEmpty() && !repairedTool().isEmpty();
        boolean working = repairing || (!result.isEmpty() && canAcceptOutput(result));
        int previousProgress = progress;

        if (working) {
            progress++;
            if (progress >= maxProgress) {
                if (repairing) {
                    repairTool();
                } else {
                    craftItem(recipe.orElseThrow().value(), result);
                }
                progress = 0;
            }
        } else {
            progress = 0;
        }
        if (previousProgress != progress) {
            setChanged();
        }
        if (state.getValue(TrinketTableBlock.LIT) != working) {
            level.setBlockAndUpdate(pos, state.setValue(TrinketTableBlock.LIT, working));
        }
    }

    /** The slot's stack, which is a copy: writes have to go back through the handler. */
    private ItemStack stackIn(int slot) {
        return ItemUtil.getStack(itemHandler, slot);
    }

    private void putIn(int slot, ItemStack stack) {
        itemHandler.set(slot, stack.isEmpty() ? ItemResource.EMPTY : ItemResource.of(stack), stack.getCount());
    }

    private void takeFrom(int slot, int amount) {
        ItemStack stack = stackIn(slot);
        if (amount <= 0 || stack.isEmpty()) return;
        // A recipe can ask for more than the slot holds; the old extractItem took what was there.
        putIn(slot, stack.copyWithCount(stack.getCount() - Math.min(amount, stack.getCount())));
    }

    private TrinketRecipeInput getRecipeInput() {
        return new TrinketRecipeInput(stackIn(TRINKET_SLOT), stackIn(POTION_SLOT), stackIn(GEM_SLOT));
    }

    private boolean canAcceptOutput(ItemStack result) {
        ItemStack existing = stackIn(OUTPUT_SLOT);
        return (existing.isEmpty() || ItemStack.isSameItemSameComponents(existing, result))
                && existing.getCount() + result.getCount()
                <= Math.min(itemHandler.getCapacityAsInt(OUTPUT_SLOT, ItemResource.of(result)), result.getMaxStackSize());
    }

    private void craftItem(TrinketRecipe recipe, ItemStack result) {
        ItemStack potion = stackIn(POTION_SLOT);
        // 26.1 hands the remainder out as a template; the stack is only built when there is one.
        ItemStackTemplate remainderTemplate = potion.getCraftingRemainder();
        ItemStack remainder = remainderTemplate == null ? ItemStack.EMPTY : remainderTemplate.create();
        // Vanilla potions do not declare a crafting remainder, but leave a bottle when used.
        if (remainder.isEmpty() && potion.is(Items.POTION)) {
            remainder = new ItemStack(Items.GLASS_BOTTLE);
        }
        takeFrom(TRINKET_SLOT, 1);
        takeFrom(POTION_SLOT, 1);
        takeFrom(GEM_SLOT, recipe.getFuel().count());
        putIn(POTION_SLOT, remainder);

        ItemStack output = result.copyWithCount(result.getCount() + stackIn(OUTPUT_SLOT).getCount());
        putIn(OUTPUT_SLOT, output);
    }

    /** The tool in the trinket slot as the table would repair it, empty when it would not. */
    private ItemStack repairedTool() {
        ItemStack tool = stackIn(TRINKET_SLOT);
        if (!GeneralUtility.isRepairMaterialFor(level, tool, stackIn(POTION_SLOT))) {
            return ItemStack.EMPTY;
        }
        if (!stackIn(GEM_SLOT).is(TolkienItems.GEM_AMMOLITE.get())) {
            return ItemStack.EMPTY;
        }
        // One gem and one repair material undo this much damage; anything past it needs another pair.
        int perOperation = Math.max(1, TolkienMobsConfig.trinketRepairAmount);
        ItemStack repaired = tool.copyWithCount(1);
        repaired.setDamageValue(tool.getDamageValue() - Math.min(tool.getDamageValue(), perOperation));
        return repaired;
    }

    /** Spends one gem and one repair material on the tool in its slot. */
    private void repairTool() {
        ItemStack repaired = repairedTool();
        if (repaired.isEmpty()) {
            return;
        }
        takeFrom(POTION_SLOT, 1);
        takeFrom(GEM_SLOT, 1);
        putIn(TRINKET_SLOT, repaired);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if(rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }
}