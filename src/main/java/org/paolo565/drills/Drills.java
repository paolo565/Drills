package org.paolo565.drills;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.paolo565.drills.listeners.BlockListener;
import org.paolo565.drills.listeners.PlayerListener;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Drills extends JavaPlugin {
    private static Drills instance;
    private Config config;
    private SQLiteDatabase database;

    public void onEnable() {
        instance = this;

        File pluginDirectory = getDataFolder();
        if(!pluginDirectory.exists()) {
            pluginDirectory.mkdirs();
        }

        database = new SQLiteDatabase(new File(pluginDirectory + File.separator + "database.db"));
        config = new Config(this);

        Server server = getServer();
        PluginManager pluginManager = server.getPluginManager();

        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new PlayerListener(), this);
    }

    public void onDisable() {
        database.close();
    }

    public static Drills getInstance() {
        return instance;
    }

    public Config getConfiguration() {
        return config;
    }

    public boolean addDrill(Player owner, Location furnace) {
        try {
            return database.getConnection().createStatement().executeUpdate("INSERT INTO drills (owner, furnace_world, furnace_x, furnace_y, furnace_z) VALUES ('" + owner.getUniqueId() + "', '" + furnace.getWorld().getName() + "', " + furnace.getBlockX() + ", " + furnace.getBlockY() + ", " + furnace.getBlockZ() + ")") == 1;
        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public Player getDrillOwner(Location furnace) {
        try {
            ResultSet resultSet = database.getConnection().createStatement().executeQuery("SELECT owner FROM drills WHERE furnace_world = '" + furnace.getWorld().getName() + "' AND furnace_x = " + furnace.getBlockX() + " AND furnace_y = " + furnace.getBlockY() + " AND furnace_z = " + furnace.getBlockZ());
            if(resultSet.next()) {
                return Bukkit.getPlayer(UUID.fromString(resultSet.getString("owner")));
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public boolean removeDrill(Location furnace) {
        try {
            return database.getConnection().createStatement().executeUpdate("DELETE FROM drills WHERE furnace_world = '" + furnace.getWorld().getName() + "' AND furnace_x = " + furnace.getBlockX() + " AND furnace_y = " + furnace.getBlockY() + " AND furnace_z = " + furnace.getBlockZ()) == 1;
        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
