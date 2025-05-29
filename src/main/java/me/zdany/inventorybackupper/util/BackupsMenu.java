package me.zdany.inventorybackupper.util;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.zdany.inventorybackupper.InventoryBackupper;
import me.zdany.inventorybackupper.backup.Backup;
import me.zdany.inventorybackupper.backup.BackupTrigger;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class BackupsMenu {

    private final InventoryBackupper instance;
    private final Player player, target;
    private final Gui triggersGui, inventoryGui;
    private final PaginatedGui backupsGui;

    public BackupsMenu(InventoryBackupper instance, Player player, Player target) {
        String title = Utils.getColor(instance.getConfigManager().getLanguage().get().getString("backups-menu", "").replaceAll("\\{player}", target.getName()));
        this.instance = instance;
        this.player = player;
        this.target = target;
        this.triggersGui = Gui.gui().rows(1).title(Component.text(title)).create();
        this.backupsGui = Gui.paginated().rows(5).title(Component.text(title)).create();
        this.inventoryGui = Gui.gui().rows(5).title(Component.text(title)).create();
        initTriggersMenu();
    }

    public void open() {
        triggersGui.open(player);
    }

    private void initTriggersMenu() {
        if(isEnabledTrigger(BackupTrigger.JOIN)) this.triggersGui.addItem(this.getTriggerItemOf(BackupTrigger.JOIN));
        if(isEnabledTrigger(BackupTrigger.QUIT)) this.triggersGui.addItem(this.getTriggerItemOf(BackupTrigger.QUIT));
        if(isEnabledTrigger(BackupTrigger.DEATH)) this.triggersGui.addItem(this.getTriggerItemOf(BackupTrigger.DEATH));
        if(isEnabledTrigger(BackupTrigger.DAMAGE)) this.triggersGui.addItem(this.getTriggerItemOf(BackupTrigger.DAMAGE));
        if(isEnabledTrigger(BackupTrigger.KILL)) this.triggersGui.addItem(this.getTriggerItemOf(BackupTrigger.KILL));
    }

    private boolean isEnabledTrigger(BackupTrigger trigger) {
        FileConfiguration config = this.instance.getConfigManager().getSettings().get();
        return config.getBoolean("triggers." + trigger.toString().toLowerCase() + ".enabled", false);
    }

    private GuiItem getTriggerItemOf(BackupTrigger trigger) {
        String path = "triggers." + trigger.toString().toLowerCase() + ".trigger-material";
        Material material = Material.getMaterial(this.instance.getConfigManager().getSettings().get().getString(path, "STONE"));
        ItemStack stack = new ItemStack(material == null ? Material.STONE : material);
        ItemMeta meta = stack.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + Utils.capitalize(trigger.toString().toLowerCase()));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }
        stack.setItemMeta(meta);
        return ItemBuilder.from(stack).asGuiItem(event -> {
            this.select(trigger);
            event.setCancelled(true);
        });
    }

    private void select(BackupTrigger trigger) {
        for(int i = 36; i < 45; i++) this.backupsGui.setItem(i, ItemBuilder.from(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).asGuiItem(event -> event.setCancelled(true)));
        this.backupsGui.updateItem(37, ItemBuilder.from(getPageItem("Previous")).asGuiItem(event -> {
            this.backupsGui.previous();
            event.setCancelled(true);
        }));
        this.backupsGui.updateItem(43, ItemBuilder.from(getPageItem("Next")).asGuiItem(event -> {
            this.backupsGui.next();
            event.setCancelled(true);
        }));
        this.backupsGui.addItem(getBackupItemsOf(trigger));
        this.backupsGui.open(player);
    }

    private ItemStack getPageItem(String text) {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta meta = stack.getItemMeta();
        if(meta != null) meta.setDisplayName(ChatColor.GOLD + text);
        stack.setItemMeta(meta);
        return stack;
    }

    private GuiItem[] getBackupItemsOf(BackupTrigger trigger) {
        Map<String, Backup> backups = this.instance.getBackupManager().getBackupsOf(this.target, List.of(trigger));
        GuiItem[] items = new GuiItem[backups.size()];
        int index = 0;
        Material material = Material.getMaterial(this.instance.getConfigManager().getSettings().get().getString("backup-material", "STONE"));
        for(String key : backups.keySet()) {
            Backup backup = backups.get(key);
            ZonedDateTime createdAt = backup.getCreatedAt();
            String date = createdAt.getDayOfMonth() + "/" + createdAt.getMonthValue() + "/" + createdAt.getYear();
            String time = createdAt.getHour() + ":" + createdAt.getMinute() + ":" + createdAt.getSecond();
            ItemStack stack = new ItemStack(material == null ? Material.STONE : material);
            ItemMeta meta = stack.getItemMeta();
            if(meta != null) {
                meta.setDisplayName(ChatColor.YELLOW + key);
                meta.setLore(List.of(ChatColor.GRAY + "Created on " + ChatColor.WHITE + date, ChatColor.GRAY + "at " + ChatColor.GOLD + time));
            }
            stack.setItemMeta(meta);
            items[index++] = ItemBuilder.from(stack).asGuiItem(event -> {
                select(backup);
                event.setCancelled(true);
            });
        }
        return items;
    }

    private void select(Backup backup) {
        for(ItemStack stack : backup.getContents()) this.inventoryGui.addItem(ItemBuilder.from(stack).asGuiItem(event -> {
            if(player.hasPermission("inventorybackupper.interact")) return;
            Utils.sendMessage(player, Utils.getColor(this.instance.getConfigManager().getLanguage().get().getString("no-permission", "")));
            event.setCancelled(true);
        }));
        this.inventoryGui.open(player);
    }
}
