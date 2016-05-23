package org.paolo565.drills.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BlockUtils {
    public static Block getDrillerNearFurnace(Location furnace) {
        World world = furnace.getWorld();
        int x = furnace.getBlockX();
        int y = furnace.getBlockY();
        int z = furnace.getBlockZ();

        Block bottom = world.getBlockAt(x, y - 1, z);
        if(bottom.getType() == Material.IRON_BLOCK || bottom.getType() == Material.DIAMOND_BLOCK) {
            return bottom;
        }

        Block up = world.getBlockAt(x, y + 1, z);
        if(up.getType() == Material.IRON_BLOCK || up.getType() == Material.DIAMOND_BLOCK) {
            return up;
        }

        Block north = world.getBlockAt(x, y, z - 1);
        if(north.getType() == Material.IRON_BLOCK || north.getType() == Material.DIAMOND_BLOCK) {
            return north;
        }

        Block east = world.getBlockAt(x + 1, y, z);
        if(east.getType() == Material.IRON_BLOCK || east.getType() == Material.DIAMOND_BLOCK) {
            return east;
        }

        Block south = world.getBlockAt(x, y, z + 1);
        if(south.getType() == Material.IRON_BLOCK || south.getType() == Material.DIAMOND_BLOCK) {
            return south;
        }

        Block west = world.getBlockAt(x - 1, y, z);
        if(west.getType() == Material.IRON_BLOCK || west.getType() == Material.DIAMOND_BLOCK) {
            return west;
        }

        return null;
    }

    public static Block getFurnaceNearDriller(Location driller) {
        World world = driller.getWorld();
        int x = driller.getBlockX();
        int y = driller.getBlockY();
        int z = driller.getBlockZ();

        Block bottom = world.getBlockAt(x, y - 1, z);
        if(bottom.getType() == Material.FURNACE) {
            return bottom;
        }

        Block up = world.getBlockAt(x, y + 1, z);
        if(up.getType() == Material.FURNACE) {
            return up;
        }

        Block north = world.getBlockAt(x, y, z - 1);
        if(north.getType() == Material.FURNACE) {
            return north;
        }

        Block east = world.getBlockAt(x + 1, y, z);
        if(east.getType() == Material.FURNACE) {
            return east;
        }

        Block south = world.getBlockAt(x, y, z + 1);
        if(south.getType() == Material.FURNACE) {
            return south;
        }

        Block west = world.getBlockAt(x - 1, y, z);
        if(west.getType() == Material.FURNACE) {
            return west;
        }

        return null;
    }
}
