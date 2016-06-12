package org.paolo565.drills.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerInventoryEvent(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        InventoryView openInventory = player.getOpenInventory();
        ItemStack clickedStack = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction action = event.getAction();
        if(clickedInventory == null || openInventory.getType() != InventoryType.FURNACE) {
            return;
        }

        if(clickedInventory.getType() == InventoryType.FURNACE && event.getSlot() == 1
                && player.getItemOnCursor().getType() == Material.SUGAR_CANE && action == InventoryAction.NOTHING) {
            event.setCancelled(true);
            ItemStack cursorStack = player.getItemOnCursor();
            player.setItemOnCursor(openInventory.getItem(1));
            openInventory.setItem(1, cursorStack);
        } else if(clickedInventory.getType() == InventoryType.FURNACE && event.getSlot() == 1
                && player.getItemOnCursor().getType() == Material.SUGAR_CANE && (action == InventoryAction.PLACE_ALL
                || action == InventoryAction.PICKUP_ALL || action == InventoryAction.PLACE_ONE)) {
            event.setCancelled(true);
            ItemStack furnaceSlot = openInventory.getItem(1);
            int space = 0;

            if(furnaceSlot == null || furnaceSlot.getType() == Material.AIR) {
                space = 64;
            } else if(furnaceSlot.getType() == Material.SUGAR_CANE) {
                space = 64 - furnaceSlot.getAmount();
            }

            if(space > 0) {
                int toPlace = event.getClick() == ClickType.RIGHT ? 1 : player.getItemOnCursor().getAmount();
                if(space < toPlace) {
                    toPlace = space;
                }

                openInventory.setItem(1, new ItemStack(Material.SUGAR_CANE, furnaceSlot.getAmount() + toPlace));
                if(toPlace == player.getItemOnCursor().getAmount()) {
                    player.setItemOnCursor(new ItemStack(Material.AIR));
                } else {
                    player.getItemOnCursor().setAmount(player.getItemOnCursor().getAmount() - toPlace);
                }
            }
        } else if(clickedInventory.getType() != InventoryType.FURNACE
                && action == InventoryAction.MOVE_TO_OTHER_INVENTORY && clickedStack.getType() == Material.SUGAR_CANE) {
            ItemStack furnaceSlot = openInventory.getItem(1);
            int count;

            if(furnaceSlot == null || furnaceSlot.getType() == Material.AIR) {
                count = 0;
            } else if(furnaceSlot.getType() == Material.SUGAR_CANE) {
                count = furnaceSlot.getAmount();
            } else {
                return;
            }
            event.setCancelled(true);

            int insert = 64 - count;
            insert = clickedStack.getAmount() > insert ? insert : clickedStack.getAmount();
            furnaceSlot.setType(Material.SUGAR_CANE);
            furnaceSlot.setAmount(count + insert);
            openInventory.setItem(1, furnaceSlot);

            if(insert == clickedStack.getAmount()) {
                clickedStack.setType(Material.AIR);
            } else {
                clickedStack.setAmount(clickedStack.getAmount() - insert);
            }
            clickedInventory.setItem(event.getSlot(), clickedStack);
        }
    }
}
