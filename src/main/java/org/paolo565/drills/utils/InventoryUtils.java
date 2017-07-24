package org.paolo565.drills.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    public static int countItemsInInventory(Inventory inventory, Material material) {
        int count = 0;

        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack == null || stack.getType() != material) {
                continue;
            }

            count += stack.getAmount();
        }

        return count;
    }

    public static void removeItemsFromInventory(Inventory inventory, Material material, int amount) {
        for(int i = 0; i < inventory.getSize() && amount > 0; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack == null || stack.getType() != material) {
                continue;
            }

            int remove = stack.getAmount() > amount ? amount : stack.getAmount();
            amount -= remove;
            if (stack.getAmount() == remove) {
                stack.setType(Material.AIR);
            } else {
                stack.setAmount(stack.getAmount() - remove);
            }
        }
    }

    public static void dropContents(Block block) {
        for (ItemStack drop : block.getDrops()) {
            block.getLocation().getWorld().dropItemNaturally(block.getLocation(), drop);
        }
    }
}
