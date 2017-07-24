package org.paolo565.drills;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.paolo565.drills.utils.BlockUtils;

import java.util.UUID;

public class Drill {
    private UUID ownerUid;
    private Location furnaceLocation;

    public Drill(UUID ownerUid, Location furnaceLocation) {
        this.ownerUid = ownerUid;
        this.furnaceLocation = furnaceLocation;
    }

    public Player getOwner() {
        return Bukkit.getPlayer(ownerUid);
    }

    public Location getFurnaceLocation() {
        return furnaceLocation;
    }

    public Location getBitLocation() {
        Block block = BlockUtils.getDrillerNearFurnace(furnaceLocation);
        return block == null ? null : block.getLocation();
    }
}
