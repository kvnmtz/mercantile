package dev.kvnmtz.mercantile.common.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public abstract class InventoryUtils {

    public static int getAvailableSpace(ServerPlayer player, ItemStack itemType) {
        var inventoryItems = player.getInventory().items;
        var totalSpace = 0;

        for (var slotStack : inventoryItems) {
            if (slotStack.isEmpty()) {
                totalSpace += itemType.getMaxStackSize();
            } else if (ItemStack.isSameItemSameTags(slotStack, itemType)) {
                totalSpace += slotStack.getMaxStackSize() - slotStack.getCount();
            }
        }
        return totalSpace;
    }

    public static boolean canAddItem(ServerPlayer player, ItemStack stack) {
        var canAdd = false;

        for (var slotStack : player.getInventory().items) {
            if (slotStack.isEmpty() || ItemStack.isSameItemSameTags(slotStack, stack)
                    && slotStack.getCount() < slotStack.getMaxStackSize()) {
                canAdd = true;
                break;
            }
        }

        return canAdd;
    }
}
