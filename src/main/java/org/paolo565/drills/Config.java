package org.paolo565.drills;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {
    private final List<Material> disabledBlocks;
    private final int maxDrillDistance;
    private final HashMap<Material, Integer> fuels;
    private final HashMap<Material, Integer> bits;

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

        fuels = new HashMap<>();
        ConfigurationSection fuelSection = config.getConfigurationSection("fuels");
        for (String key : fuelSection.getKeys(false)) {
            Material fuel = Material.valueOf(key);
            int brokenBlocks = fuelSection.getInt(key);

            fuels.put(fuel, brokenBlocks);
        }

        bits = new HashMap<>();
        ConfigurationSection bitsSection = config.getConfigurationSection("bits");
        for (String key : bitsSection.getKeys(false)) {
            Material bit = Material.valueOf(key);
            int modifier = bitsSection.getInt(key);

            bits.put(bit, modifier);
        }
    }

    public boolean isMaterialDisabled(Material material) {
        return disabledBlocks.contains(material);
    }

    public int getMaxDrillDistance() {
        return maxDrillDistance;
    }

    public boolean isFuel(Material material) {
        return fuels.containsKey(material);
    }

    public int getBrokenBlocksByFuel(Material material) {
        return fuels.getOrDefault(material, 0);
    }

    public boolean isDrillBit(Material material) {
        return bits.containsKey(material);
    }

    public int getDrillBitModifier(Material material) {
        return bits.getOrDefault(material, 0);
    }
}
