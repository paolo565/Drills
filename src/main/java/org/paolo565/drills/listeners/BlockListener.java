package org.paolo565.drills.listeners;

import org.bukkit.*;
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
import org.paolo565.drills.Config;
import org.paolo565.drills.Drill;
import org.paolo565.drills.Drills;
import org.paolo565.drills.utils.BlockUtils;
import org.paolo565.drills.utils.InventoryUtils;

public class BlockListener implements Listener {
    private Drills plugin;
    public BlockListener(Drills plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrillHeadPlaceEvent(BlockPlaceEvent event) {
        Block placed = event.getBlock();
        Config config = Drills.getInstance().getConfiguration();

        if (config.isDrillBit(placed.getType())) {
            return;
        }

        Block against = BlockUtils.getFurnaceNearDriller(placed.getLocation());
        if (against == null) {
            return;
        }

        Player player = event.getPlayer();
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

    @EventHandler(ignoreCancelled = true)
    public void onDrillBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block broken = event.getBlock();
        Config config = Drills.getInstance().getConfiguration();

        if (config.isDrillBit(broken.getType())) {
            Block furnace = BlockUtils.getFurnaceNearDriller(broken.getLocation());
            if (furnace == null || plugin.getDrill(furnace.getLocation()) == null) {
                return;
            }

            if (plugin.removeDrill(furnace.getLocation())) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    InventoryUtils.dropContents(furnace);

                    furnace.setType(Material.AIR);
                } else {
                    furnace.breakNaturally();
                }
                player.sendMessage(ChatColor.DARK_GREEN + "Drill removed successfully.");
            } else {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Error while removing the drill.");
            }
        } else if (broken.getType() == Material.FURNACE && plugin.getDrill(broken.getLocation()) != null) {
            Block driller = BlockUtils.getDrillerNearFurnace(broken.getLocation());
            if (driller == null) {
                return;
            }

            if (plugin.removeDrill(broken.getLocation())) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    driller.setType(Material.AIR);
                } else {
                    driller.breakNaturally();
                }
                player.sendMessage(ChatColor.DARK_GREEN + "Drill removed successfully.");
            } else {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "ChatError while removing the drill.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRedstoneTurnOnEvent(BlockRedstoneEvent event) {
        if (event.getOldCurrent() > 0 && event.getNewCurrent() == 0) {
            return;
        }

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

    private void drill(Block furnaceBlock) {
        if (furnaceBlock.getType() != Material.FURNACE) {
            return;
        }

        Block driller = BlockUtils.getDrillerNearFurnace(furnaceBlock.getLocation());
        if (driller == null) {
            return;
        }

        Config config = Drills.getInstance().getConfiguration();

        Furnace furnace = (Furnace) furnaceBlock.getState();
        ItemStack fuel = furnace.getInventory().getFuel();
        if (!config.isFuel(fuel.getType())) {
            return;
        }

        int consumedFuel = config.getDrillBitModifier(driller.getType());
        if (fuel.getAmount() < consumedFuel) {
            return;
        }

        Drill drill = plugin.getDrill(furnaceBlock.getLocation());
        if (drill == null || drill.getOwner() == null) {
            return;
        }

        int destructCount = config.getBrokenBlocksByFuel(fuel.getType());

        BlockFace face = furnaceBlock.getFace(driller);
        int x_m = face == BlockFace.EAST ? 1 : (face == BlockFace.WEST ? -1 : 0);
        int y_m = face == BlockFace.UP ? 1 : (face == BlockFace.DOWN ? -1 : 0);
        int z_m = face == BlockFace.SOUTH ? 1 : (face == BlockFace.NORTH ? -1 : 0);

        int drilledBlocks = 0;
        Location drillerLocation = driller.getLocation();
        World world = driller.getWorld();
        int x = drillerLocation.getBlockX();
        int y = drillerLocation.getBlockY();
        int z = drillerLocation.getBlockZ();
        for (int i = 1; i <= plugin.getConfiguration().getMaxDrillDistance(); i++) {
            Block toBreak = world.getBlockAt(x + (i * x_m), y + (i * y_m), z + (i * z_m));
            if (toBreak.getType() == Material.AIR) {
                continue;
            }

            if (toBreak.isLiquid() || plugin.getConfiguration().isMaterialDisabled(toBreak.getType())) {
                return;
            }

            BlockBreakEvent event = new BlockBreakEvent(toBreak, drill.getOwner());
            plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            toBreak.breakNaturally();
            drilledBlocks++;

            if (drilledBlocks >= destructCount) {
                break;
            }
        }

        if (drilledBlocks > 0) {
            if (fuel.getAmount() > consumedFuel) {
                fuel.setAmount(fuel.getAmount() - consumedFuel);
                furnace.getInventory().setFuel(fuel);
            } else {
                furnace.getInventory().setFuel(new ItemStack(Material.AIR));
            }
        }
    }
}
