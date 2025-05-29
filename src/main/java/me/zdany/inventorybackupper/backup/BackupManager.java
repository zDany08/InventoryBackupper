package me.zdany.inventorybackupper.backup;

import me.zdany.inventorybackupper.InventoryBackupper;
import me.zdany.inventorybackupper.config.JsonConfig;
import me.zdany.inventorybackupper.database.DatabaseManager;
import me.zdany.inventorybackupper.database.QueryParameter;
import me.zdany.inventorybackupper.util.Logger;
import me.zdany.inventorybackupper.util.Utils;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BackupManager {

    private final InventoryBackupper instance;

    public BackupManager(InventoryBackupper instance) {
        this.instance = instance;
    }

    public Map<String, Backup> getBackupsOf(Player player, List<BackupTrigger> filters) {
        if(this.instance.isUsingDatabase()) return getDatabaseBackups(player, filters);
        return getConfigBackups(player, filters);
    }

    public boolean triggerBackupBy(InventoryBackupper instance, Player player, BackupTrigger trigger) {
        if(Utils.getBackups(this.instance, player, trigger) >= instance.getConfigManager().getSettings().get().getInt("triggers." + trigger.toString().toLowerCase() + ".max-backups", 1)) return false;
        boolean created = generateBackup(new Backup(this.instance, player.getUniqueId(), trigger, player.getInventory().getContents()));
        if(!created) return false;
        Utils.incrementBackups(this.instance, player, trigger);
        return true;
    }

    public boolean generateBackup(Backup backup) {
        if(this.instance.isUsingDatabase()) return generateDatabaseBackup(backup);
        return generateConfigBackup(backup);
    }

    private Map<String, Backup> getDatabaseBackups(Player player, List<BackupTrigger> filters) {
        DatabaseManager manager = this.instance.getDatabaseManager();
        Map<String, Backup> backups = new HashMap<>();
        try(ResultSet result = manager.getDatabase().resultExecute(manager.getFetchBackupsQuery(), new QueryParameter<>(1, player.getUniqueId().toString()))) {
            while(result != null && result.next()) {
                String triggerStr = result.getString("trigger");
                if(!filters.contains(BackupTrigger.valueOf(triggerStr))) continue;
                backups.put(result.getString("id"), Backup.deserialize(
                        this.instance,
                        new JSONObject()
                                .put("owner", result.getString("owner"))
                                .put("createdAt", result.getLong("created_at"))
                                .put("trigger", triggerStr)
                                .put("contents", new JSONArray(result.getString("contents")))
                        )
                );
            }
        }catch(SQLException e) {
            this.instance.getPluginLogger().error("Cannot fetch backups from database: " + e.getMessage());
        }
        return backups;
    }

    private Map<String, Backup> getConfigBackups(Player player, List<BackupTrigger> filters) {
        JSONObject backupsObj = this.instance.getConfigManager().getData().get().getJSONObject("backups");
        Map<String, Backup> backups = new HashMap<>();
        for(String key : backupsObj.keySet()) {
            Backup backup = Backup.deserialize(this.instance, backupsObj.getJSONObject(key));
            if(!filters.contains(backup.getTrigger())) continue;
            if(!backup.getOwner().equals(player.getUniqueId())) continue;
            backups.put(key, backup);
        }
        return backups;
    }

    private boolean generateDatabaseBackup(Backup backup) {
        DatabaseManager manager = this.instance.getDatabaseManager();
        JSONObject backupObj = backup.serialize();
        return manager.getDatabase().execute(
                manager.getCreateBackupQuery(),
                new QueryParameter<>(1, UUID.randomUUID().toString()),
                new QueryParameter<>(2, backupObj.getString("owner")),
                new QueryParameter<>(3, backupObj.getLong("createdAt")),
                new QueryParameter<>(4, backupObj.getString("trigger")),
                new QueryParameter<>(5, backupObj.getJSONArray("contents").toString())
        );
    }

    private boolean generateConfigBackup(Backup backup) {
        JsonConfig dataConfig = this.instance.getConfigManager().getData();
        String id = UUID.randomUUID().toString();
        try {
            dataConfig.get().getJSONObject("backups").put(id, backup.serialize());
        }catch(Exception e) {
            this.instance.getPluginLogger().error("Cannot generate backup \"" + id + "\": " + e.getMessage());
            return false;
        }finally {
            dataConfig.save();
        }
        return dataConfig.get().getJSONObject("backups").has(id);
    }
}
