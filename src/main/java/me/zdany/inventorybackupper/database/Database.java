package me.zdany.inventorybackupper.database;

import me.zdany.inventorybackupper.InventoryBackupper;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class Database {

    private final InventoryBackupper instance;
    private final String path;
    private final File file;
    private Connection connection;

    public Database(InventoryBackupper instance, String path) {
        this.instance = instance;
        this.path = path;
        this.file = new File(instance.getDataFolder(), path + ".db");
    }

    public void connect() {
        generateFile();
        this.instance.getPluginLogger().info("Connecting database \"" + this.path + "\"...");
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
        }catch(SQLException e) {
            this.instance.getPluginLogger().error("Cannot connect database \"" + this.path + "\": " + e.getMessage());
        }
    }

    public boolean execute(String query, QueryParameter<?>... parameters) {
        try(PreparedStatement stmt = this.connection.prepareStatement(query)) {
            setParameters(stmt, parameters);
            stmt.execute();
            return true;
        }catch(SQLException e) {
            this.instance.getPluginLogger().error("Cannot execute SQL query \"" + query + "\": " + e.getMessage());
            return false;
        }
    }

    public ResultSet resultExecute(String query, QueryParameter<?>... parameters) {
        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            setParameters(stmt, parameters);
            return stmt.executeQuery();
        }catch(SQLException e) {
            this.instance.getPluginLogger().error("Cannot execute SQL query \"" + query + "\": " + e.getMessage());
            return null;
        }
    }

    public void disconnect() {
        this.instance.getPluginLogger().info("Disconnecting database \"" + this.path + "\"...");
        try {
            this.connection.close();
            this.connection = null;
        }catch(SQLException e) {
            this.instance.getPluginLogger().error("Cannot disconnect database \"" + this.path + "\": " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return this.connection != null;
    }

    private void generateFile() {
        if(file.exists()) return;
        String errorMessage = "Cannot generate file for database \"" + this.path + "\"";
        try {
            if(!file.createNewFile()) this.instance.getPluginLogger().error(errorMessage + ".");
        }catch(IOException e) {
            this.instance.getPluginLogger().error(errorMessage + ": " + e.getMessage());
        }
    }

    private void setParameters(PreparedStatement stmt, QueryParameter<?>... parameters) throws SQLException {
        for(QueryParameter<?> parameter : parameters) {
            int index = parameter.index();
            if(parameter.value() instanceof Short value) {
                stmt.setShort(index, value);
            } else if(parameter.value() instanceof Integer value) {
                stmt.setInt(index, value);
            }else if(parameter.value() instanceof Long value) {
                stmt.setLong(index, value);
            }else if(parameter.value() instanceof String value) {
                stmt.setString(index, value);
            }
        }
    }
}
