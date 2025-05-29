package me.zdany.inventorybackupper.listener;

import me.zdany.inventorybackupper.InventoryBackupper;
import me.zdany.inventorybackupper.backup.BackupTrigger;
import me.zdany.inventorybackupper.config.YamlConfig;
import me.zdany.inventorybackupper.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BackupListener implements Listener {

    private final InventoryBackupper instance;

    public BackupListener(InventoryBackupper instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        initPlayer(player);
        if(!this.instance.getConfigManager().getSettings().get().getBoolean("triggers.join.enabled", false)) return;
        if(this.instance.getBackupManager().triggerBackupBy(this.instance, player, BackupTrigger.JOIN)) {
            Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("backup-created", "")));
            return;
        }
        Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("backup-not-created", "")));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(!this.instance.getConfigManager().getSettings().get().getBoolean("triggers.quit.enabled", false)) return;
        if(this.instance.getBackupManager().triggerBackupBy(this.instance, player, BackupTrigger.QUIT)) {
            Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("backup-created", "")));
            return;
        }
        Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("backup-not-created", "")));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if(!this.instance.getConfigManager().getSettings().get().getBoolean("triggers.death.enabled", false)) return;
        if(this.instance.getBackupManager().triggerBackupBy(this.instance, player, BackupTrigger.DEATH)) {
            Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("backup-created", "")));
            return;
        }
        Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("backup-not-created", "")));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        if(!this.instance.getConfigManager().getSettings().get().getBoolean("triggers.join.enabled", false)) return;
        if(this.instance.getBackupManager().triggerBackupBy(this.instance, player, BackupTrigger.DAMAGE)) {
            Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("backup-created", "")));
            return;
        }
        Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("backup-not-created", "")));
    }

    @EventHandler
    public void onKill(EntityDamageByEntityEvent event) {
        if(!event.getEntity().isDead()) return;
        if(!(event.getDamager() instanceof Player player)) return;
        if(!this.instance.getConfigManager().getSettings().get().getBoolean("triggers.kill.enabled", false)) return;
        if(this.instance.getBackupManager().triggerBackupBy(this.instance, player, BackupTrigger.KILL)) {
            Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("backup-created", "")));
            return;
        }
        Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("backup-not-created", "")));
    }

    private void initPlayer(Player player) {
        String basePath = "players.";
        YamlConfig playerData = this.instance.getConfigManager().getPlayerData();
        if(playerData.get().contains(basePath + player.getUniqueId())) return;
        playerData.get().set(basePath + player.getUniqueId() + ".join-backups", 0);
        playerData.get().set(basePath + player.getUniqueId() + ".quit-backups", 0);
        playerData.get().set(basePath + player.getUniqueId() + ".death-backups", 0);
        playerData.get().set(basePath + player.getUniqueId() + ".damage-backups", 0);
        playerData.get().set(basePath + player.getUniqueId() + ".kill-backups", 0);
        playerData.save();
    }
}
