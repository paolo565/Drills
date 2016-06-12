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
        initializeDatabase();
    }

    public Connection getConnection() {
        try {
            if(connection != null && !connection.isClosed()) {
                return connection;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if(!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
            return connection;
        } catch(ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void initializeDatabase() {
        try {
            getConnection().createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS drills (owner VARCHAR(36) NULL, furnace_world VARCHAR(32) NULL, furnace_x INT(11) NULL, furnace_y INT(11) NULL, furnace_z INT(11) NULL)");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
