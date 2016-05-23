package org.paolo565.drills;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {
    public final List<Material> disabledBlocks;
    public final int maxDistance;

    public Config(Drills instance) {
        File configFile = new File(instance.getDataFolder(), "config.yml");

        if(!configFile.exists()) {
            instance.saveDefaultConfig();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        disabledBlocks = new ArrayList<Material>();
        List<String> disabledBlocks = config.getStringList("disabled_blocks");
        for(String disabledBlock : disabledBlocks) {
            try {
                this.disabledBlocks.add(Material.valueOf(disabledBlock));
            } catch(IllegalArgumentException ex) {
            }
        }

        maxDistance = config.getInt("max_distance");
    }
}
