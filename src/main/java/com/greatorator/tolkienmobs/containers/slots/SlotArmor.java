package com.greatorator.tolkienmobs.containers.slots;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Slot that is used to hold armor.
 * @author rubensworks
 *
 */
public class SlotArmor extends Slot {

    private final EquipmentSlot armorType;
    private final Player player;

    /**
     * Make a new instance.
     * @param inventory The inventory this slot will be in.
     * @param index The index of this slot.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param player The player entity.
     * @param armorType The armor type.
     */
    public SlotArmor(Container inventory, int index, int x,
                     int y, Player player, EquipmentSlot armorType) {
        super(inventory, index, x, y);
        this.armorType = armorType;
        this.player = player;
        setBackground(InventoryMenu.BLOCK_ATLAS, InventoryMenu.TEXTURE_EMPTY_SLOTS.get(armorType));
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public @NotNull SlotArmor setBackground(@NotNull ResourceLocation atlas, @NotNull ResourceLocation sprite) {
        super.setBackground(atlas, sprite);
        return this;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return itemStack.getEquipmentSlot() == armorType
                || (itemStack.getItem() instanceof ArmorItem && ((ArmorItem) itemStack.getItem()).getEquipmentSlot() == armorType);
    }

}
