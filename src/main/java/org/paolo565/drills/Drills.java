package org.paolo565.drills;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.paolo565.drills.listeners.BlockListener;
import org.paolo565.drills.listeners.PlayerListener;

import java.io.File;
import java.sql.PreparedStatement;
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
        pluginDirectory.mkdirs();

        database = new SQLiteDatabase(new File(pluginDirectory, "database.db"));
        try {
            database.initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        config = new Config(this);

        Server server = getServer();
        PluginManager pluginManager = server.getPluginManager();

        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new PlayerListener(), this);
    }

    public void onDisable() {
        try {
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Drills getInstance() {
        return instance;
    }

    public Config getConfiguration() {
        return config;
    }

    public boolean addDrill(Player owner, Location furnace) {
        try (PreparedStatement statement = database.getConnection().prepareStatement("INSERT INTO drills (owner, furnace_world, furnace_x, furnace_y, furnace_z) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, owner.getUniqueId().toString());
            statement.setString(2, furnace.getWorld().getName());
            statement.setInt(3, furnace.getBlockX());
            statement.setInt(4, furnace.getBlockY());
            statement.setInt(5, furnace.getBlockZ());

            return statement.executeUpdate() == 1;
        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public Drill getDrill(Location furnace) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = database.getConnection().prepareStatement("SELECT * FROM drills WHERE furnace_world = ? AND furnace_x = ? AND furnace_y = ? AND furnace_z = ?");
            statement.setString(1, furnace.getWorld().getName());
            statement.setInt(2, furnace.getBlockX());
            statement.setInt(3, furnace.getBlockY());
            statement.setInt(4, furnace.getBlockZ());

            resultSet = statement.executeQuery();
            if(resultSet.next()) {
                UUID owner = UUID.fromString(resultSet.getString("owner"));
                World furnaceWorld = Bukkit.getWorld(resultSet.getString("furnace_world"));
                int furnaceX = resultSet.getInt("furnace_x");
                int furnaceY = resultSet.getInt("furnace_y");
                int furnaceZ = resultSet.getInt("furnace_z");

                return new Drill(owner, new Location(furnaceWorld, furnaceX, furnaceY, furnaceZ));
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public boolean removeDrill(Location furnace) {
        try (PreparedStatement statement = database.getConnection().prepareStatement("DELETE FROM drills WHERE furnace_world = ? AND furnace_x = ? AND furnace_y = ? AND furnace_z = ?")) {
            statement.setString(1, furnace.getWorld().getName());
            statement.setInt(2, furnace.getBlockX());
            statement.setInt(3, furnace.getBlockY());
            statement.setInt(4, furnace.getBlockZ());

            return statement.executeUpdate() == 1;
        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
