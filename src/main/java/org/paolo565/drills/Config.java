package org.paolo565.drills;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private final List<Material> disabledBlocks;
    private final int maxDrillDistance;

    public Config(Drills instance) {
        File configFile = new File(instance.getDataFolder(), "config.yml");
        instance.saveDefaultConfig();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        disabledBlocks = new ArrayList<>();
        List<String> disabledBlocks = config.getStringList("disabled_blocks");
        for(String disabledBlock : disabledBlocks) {
            try {
                this.disabledBlocks.add(Material.valueOf(disabledBlock));
            } catch(IllegalArgumentException ex) {
            }
        }

        maxDrillDistance = config.getInt("max_distance");
    }

    public boolean isMaterialDisabled(Material material) {
        return disabledBlocks.contains(material);
    }

    public int getMaxDrillDistance() {
        return maxDrillDistance;
    }
}
