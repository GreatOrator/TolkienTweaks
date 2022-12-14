package com.greatorator.tolkientweaks.item;

import com.brandon3055.brandonscore.items.ItemBCore;

public class KeyItem extends ItemBCore {
    public KeyItem(Properties properties) {
        super(properties);
    }

//    @Override
//    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
//        ItemStack stack = player.getItemInHand(hand);
//        if (player.isCreative() && world.isClientSide && !player.isCrouching()) {
//            openGUI(player);
//        }
//        else if (!world.isClientSide && player.isCreative() && player.isCrouching()) {
//            stack.getTagCompound().removeTag("playerUUID");
//        }
//
//        return super.use(world, player, hand);
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    private void openGUI(PlayerEntity player) {
//        Minecraft.getInstance().displayGuiScreen(new BronzeKeyAccessScreen(player, null));
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
//        PlayerEntity player = TolkienTweaks.proxy.getPlayer();
//        if (stack.getItemDamage() != 1) {
//            if (getShown(stack)) {
//                if (player.isCreative()) {
//                    tooltip.add(TextFormatting.GREEN + "Key visible to non-creative players");
//                }
//                tooltip.add(TextFormatting.GOLD + getKey(stack));
//            }
//            else if (player != null && player.isCreative()) {
//                tooltip.add(TextFormatting.RED + "Key hidden from non-creative players");
//                tooltip.add(TextFormatting.RED + getKey(stack));
//            }
//            if (ItemNBTUtils.hasKey(stack, "playerUUID") && player != null && player.isCreative()) {
//                tooltip.add(TextFormatting.GOLD + "OwnerID: " + ItemNBTUtils.getString(stack, "playerUUID"));
//            }
//        }
//        if (stack.getItemDamage() == 1) {
//            tooltip.add(TextFormatting.GOLD + "Master Key");
//        }
//
//
//        super.addInformation(stack, worldIn, tooltip, flagIn);
//    }
//
//    public void setKey(ItemStack stack, String key) {
//        ItemNBTUtils.setString(stack, "KeyCode", key);
//    }
//
//    public String getKey(ItemStack stack) {
//        return ItemNBTUtils.getString(stack, "KeyCode");
//    }
//
//    public void setShown(ItemStack stack, boolean show) {
//        ItemNBTUtils.setBoolean(stack, "ShowCode", show);
//    }
//
//    public boolean getShown(ItemStack stack) {
//        return ItemNBTUtils.getBoolean(stack, "ShowCode");
//    }
}
