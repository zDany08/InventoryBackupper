package me.zdany.inventorybackupper.database;

import me.zdany.inventorybackupper.InventoryBackupper;

public class DatabaseManager {

    private final Database database;

    public DatabaseManager(InventoryBackupper instance) {
        this.database = new Database(instance, "data");
    }

    public Database getDatabase() {
        return this.database;
    }

    public String getCreateTableQuery() {
        return "CREATE TABLE IF NOT EXISTS backups (id TEXT NOT NULL PRIMARY KEY, owner TEXT NOT NULL, created_at INTEGER NOT NULL, trigger TEXT NOT NULL, contents TEXT NOT NULL);";
    }

    public String getFetchBackupsQuery() {
        return "SELECT * FROM backups WHERE owner = ?;";
    }

    public String getCreateBackupQuery() {
        return "INSERT INTO backups (id, owner, created_at, trigger, contents) VALUES (?, ?, ?, ?, ?);";
    }
}
