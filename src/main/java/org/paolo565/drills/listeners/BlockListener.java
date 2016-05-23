package org.paolo565.drills.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.paolo565.drills.Drills;
import org.paolo565.drills.utils.BlockUtils;

public class BlockListener implements Listener {
    private Drills plugin;
    public BlockListener(Drills plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Block placed = event.getBlock();
        if(placed.getType() == Material.IRON_BLOCK || placed.getType() == Material.DIAMOND_BLOCK) {
            Player player = event.getPlayer();
            Block against = BlockUtils.getFurnaceNearDriller(placed.getLocation());

            if (against != null) {
                Material before = placed.getType();
                placed.setType(Material.AIR);
                if (BlockUtils.getDrillerNearFurnace(against.getLocation()) != null) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "There is already a driller attached to this furnace.");
                } else if (plugin.addDrill(player, against.getLocation())) {
                    placed.setType(before);
                    player.sendMessage(ChatColor.DARK_GREEN + "Drill created successfully.");
                } else {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Error while creating the drill.");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block broken = event.getBlock();

        if(broken.getType() == Material.DIAMOND_BLOCK || broken.getType() == Material.IRON_BLOCK) {
            Block furnace = BlockUtils.getFurnaceNearDriller(broken.getLocation());
            if(furnace != null) {
                if(plugin.removeDrill(furnace.getLocation())) {
                    furnace.breakNaturally();
                    player.sendMessage(ChatColor.DARK_GREEN + "Drill removed successfully.");
                } else {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Error while removing the drill.");
                }
            }
        } else if(broken.getType() == Material.FURNACE) {
            Block driller = BlockUtils.getDrillerNearFurnace(broken.getLocation());
            if(driller != null) {
                if(plugin.removeDrill(broken.getLocation())) {
                    driller.breakNaturally();
                    player.sendMessage(ChatColor.DARK_GREEN + "Drill removed successfully.");
                } else {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "ChatError while removing the drill.");
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockRedstoneEvent(BlockRedstoneEvent event) {
        if(event.getOldCurrent() == 0 && event.getNewCurrent() > 0) {
            Location location = event.getBlock().getLocation();
            World world = location.getWorld();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();

            drill(world.getBlockAt(x + 1, y, z));
            drill(world.getBlockAt(x - 1, y, z));
            drill(world.getBlockAt(x, y + 1, z));
            drill(world.getBlockAt(x, y - 1, z));
            drill(world.getBlockAt(x, y, z + 1));
            drill(world.getBlockAt(x, y, z - 1));
        }
    }

    private void drill(Block furnaceBlock) {
        if(furnaceBlock.getType() == Material.FURNACE) {
            Block driller = BlockUtils.getDrillerNearFurnace(furnaceBlock.getLocation());
            if(driller != null) {
                Furnace furnace = (Furnace) furnaceBlock.getState();
                ItemStack fuel = furnace.getInventory().getFuel();
                ItemStack smelting = furnace.getInventory().getSmelting();
                int usedFuel = driller.getType() == Material.IRON_BLOCK ? 2 : 1;
                ItemStack fuelSlot = (fuel != null && fuel.getType() == Material.SUGAR_CANE && fuel.getAmount() >= usedFuel) ?
                        fuel : (smelting != null && smelting.getType() == Material.SUGAR_CANE && smelting.getAmount() >= usedFuel) ?
                        smelting : null;

                if(fuelSlot != null) {
                    Player owner = plugin.getDrillOwner(furnaceBlock.getLocation());
                    if (owner != null) {
                        BlockFace face = furnaceBlock.getFace(driller);
                        int x_m = face == BlockFace.EAST ? 1 :(face == BlockFace.WEST ? -1 : 0);
                        int y_m = face == BlockFace.UP ? 1 : (face == BlockFace.DOWN ? -1 : 0);
                        int z_m = face == BlockFace.SOUTH ? 1 : (face == BlockFace.NORTH ? -1 : 0);

                        Block toBreak = null;
                        Location drillerLocation = driller.getLocation();
                        World world = driller.getWorld();
                        int x = drillerLocation.getBlockX();
                        int y = drillerLocation.getBlockY();
                        int z = drillerLocation.getBlockZ();
                        for (int i = 1; i <= plugin.getConfiguration().maxDistance; i++) {
                            toBreak = world.getBlockAt(x + (i * x_m), y + (i * y_m), z + (i * z_m));
                            if (toBreak.getType() != Material.AIR) {
                                if (toBreak.isLiquid() || plugin.getConfiguration().disabledBlocks.contains(toBreak.getType())) {
                                    return;
                                }

                                break;
                            }
                        }

                        if (toBreak != null) {
                            BlockBreakEvent event = new BlockBreakEvent(toBreak, owner);
                            plugin.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                toBreak.breakNaturally();
                                if (usedFuel == fuelSlot.getAmount()) {
                                    fuelSlot.setType(Material.AIR);
                                } else {
                                    fuelSlot.setAmount(fuelSlot.getAmount() - usedFuel);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
