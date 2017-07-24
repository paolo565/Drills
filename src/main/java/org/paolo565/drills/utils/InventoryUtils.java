package org.paolo565.drills.utils;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    public static void dropContents(Block block) {
        for (ItemStack drop : block.getDrops()) {
            block.getLocation().getWorld().dropItemNaturally(block.getLocation(), drop);
        }
    }
}
