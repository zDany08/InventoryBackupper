package me.zdany.inventorybackupper;

import me.zdany.inventorybackupper.backup.BackupManager;
import me.zdany.inventorybackupper.command.IBCommand;
import me.zdany.inventorybackupper.config.ConfigManager;
import me.zdany.inventorybackupper.database.DatabaseManager;
import me.zdany.inventorybackupper.listener.BackupListener;
import me.zdany.inventorybackupper.util.Logger;
import me.zdany.inventorybackupper.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class InventoryBackupper extends JavaPlugin {

    private final Logger logger = new Logger(this);
    private final ConfigManager configManager = new ConfigManager(this);
    private final DatabaseManager databaseManager = new DatabaseManager(this);
    private final BackupManager backupManager = new BackupManager(this);

    @Override
    public void onEnable() {
        Utils.generateDataFolder(this);
        this.configManager.load();
        if(this.isUsingDatabase()) {
            this.databaseManager.getDatabase().connect();
            if(!this.databaseManager.getDatabase().isConnected()) {
                this.logger.error("Database is not connected, disabling...");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
            if(!this.databaseManager.getDatabase().execute(this.databaseManager.getCreateTableQuery())) {
                this.logger.error("Cannot create database table, disabling...");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
        this.initCommand("inventorybackupper", new IBCommand(this));
        this.initListeners(new BackupListener(this));
        this.logger.info("Enabled successfully!");
    }

    @Override
    public void onDisable() {
        this.configManager.save();
        if(this.databaseManager.getDatabase().isConnected()) this.databaseManager.getDatabase().disconnect();
        this.logger.info("Disabled successfully!");
    }

    public Logger getPluginLogger() {
        return this.logger;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public boolean isUsingDatabase() {
        return this.configManager.getSettings().get().getBoolean("use-database", false);
    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public BackupManager getBackupManager() {
        return this.backupManager;
    }

    private <M extends CommandExecutor> void initCommand(String name, M manager) {
        PluginCommand command = this.getCommand(name);
        if(command == null) {
            Bukkit.getLogger().severe("Command \"" + name + "\" not found.");
            return;
        }
        command.setExecutor(manager);
        if(!(manager instanceof TabCompleter completer)) return;
        command.setTabCompleter(completer);
    }

    private void initListeners(Listener... listeners) {
        for(Listener listener : listeners) this.getServer().getPluginManager().registerEvents(listener, this);
    }
}
