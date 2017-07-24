package org.paolo565.drills;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase {
    private File databaseFile;
    private Connection connection;

    public SQLiteDatabase(File databaseFile) {
        this.databaseFile = databaseFile;

        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
        return connection;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }

    public void initializeDatabase() throws SQLException {
        getConnection().createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS drills (owner VARCHAR(36) NULL, furnace_world VARCHAR(32) NULL, furnace_x INT(11) NULL, furnace_y INT(11) NULL, furnace_z INT(11) NULL)");
    }
}
