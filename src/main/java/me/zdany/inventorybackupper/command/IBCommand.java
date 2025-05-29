package me.zdany.inventorybackupper.command;

import me.zdany.inventorybackupper.InventoryBackupper;
import me.zdany.inventorybackupper.backup.Backup;
import me.zdany.inventorybackupper.backup.BackupTrigger;
import me.zdany.inventorybackupper.util.BackupsMenu;
import me.zdany.inventorybackupper.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IBCommand implements CommandExecutor, TabCompleter {

    private final InventoryBackupper instance;

    public IBCommand(InventoryBackupper instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            Utils.sendMessage(sender, Utils.getColor(Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("only-players", ""))));
            return false;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("info")) {
            Map<String, Backup> backups = this.instance.getBackupManager().getBackupsOf(player, List.of(BackupTrigger.JOIN, BackupTrigger.QUIT, BackupTrigger.DEATH, BackupTrigger.DAMAGE, BackupTrigger.KILL));
            if(backups == null || backups.isEmpty()) {
                Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("no-backups", "")));
                return false;
            }
            Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("backup-count", "").replaceAll("\\{count}", String.valueOf(backups.size()))));
            return true;
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("loadinv")) {
            if(!player.hasPermission("inventorybackupper.loadinv")) {
                Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("no-permission", "")));
                return false;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if(target == null) {
                Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("player-not-online", "")).replaceAll("\\{player}", args[1]));
                this.instance.getConfigManager().reload();
                return false;
            }
            new BackupsMenu(this.instance, player, target).open();
            return true;
        }
        Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("commands-usage.inventorybackupper", "")));
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return List.of("info", "reload", "loadinv");
        if(args.length == 2 && args[0].equalsIgnoreCase("loadinv")) {
            List<String> suggestions = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> suggestions.add(player.getName()));
            return suggestions;
        }
        return List.of();
    }
}
