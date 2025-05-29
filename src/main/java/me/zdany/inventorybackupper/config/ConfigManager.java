package me.zdany.inventorybackupper.config;

import me.zdany.inventorybackupper.util.Logger;
import me.zdany.inventorybackupper.InventoryBackupper;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final InventoryBackupper instance;
    private final List<Config> configs;
    private final YamlConfig settingsConfig, languageConfig, playerDataConfig;
    private final JsonConfig dataConfig;

    public ConfigManager(InventoryBackupper instance) {
        this.instance = instance;
        this.configs = new ArrayList<>();
        this.configs.add(this.settingsConfig = new YamlConfig(
                instance,
                "settings",
                new ConfigPair<>("use-database", false),
                new ConfigPair<>("backup-material", "CHEST"),
                new ConfigPair<>("triggers.join.enabled", true),
                new ConfigPair<>("triggers.join.max-backups", 1),
                new ConfigPair<>("triggers.join.trigger-material", "LIME_WOOL"),
                new ConfigPair<>("triggers.quit.enabled", true),
                new ConfigPair<>("triggers.quit.max-backups", 1),
                new ConfigPair<>("triggers.quit.trigger-material", "RED_WOOL"),
                new ConfigPair<>("triggers.death.enabled", true),
                new ConfigPair<>("triggers.death.max-backups", 1),
                new ConfigPair<>("triggers.death.trigger-material", "GUNPOWDER"),
                new ConfigPair<>("triggers.damage.enabled", true),
                new ConfigPair<>("triggers.damage.max-backups", 1),
                new ConfigPair<>("triggers.damage.trigger-material", "REDSTONE"),
                new ConfigPair<>("triggers.kill.enabled", true),
                new ConfigPair<>("triggers.kill.max-backups", 1),
                new ConfigPair<>("triggers.kill.trigger-material", "IRON_SWORD")
        ));
        this.configs.add(this.languageConfig = new YamlConfig(
                instance,
                "lang",
                new ConfigPair<>("only-players", "&cOnly players can perform this action."),
                new ConfigPair<>("no-permission", "&cYou have no permission to perform this action."),
                new ConfigPair<>("player-not-online", "&4{player} &cis not online."),
                new ConfigPair<>("config-reload", "&7Reloading configuration files..."),
                new ConfigPair<>("backup-created", "&aA new inventory backup has been created."),
                new ConfigPair<>("backup-not-created", "&cFailed to create an inventory backup."),
                new ConfigPair<>("no-backups", "&cYou have no inventory backups saved."),
                new ConfigPair<>("backup-count", "&7You have &6{count} &7inventory backups."),
                new ConfigPair<>("backups-menu", "&r{player}'s Backups"),
                new ConfigPair<>("commands-usage.inventorybackupper", "&7Usage: &c/ib [info | loadinv] <player>")
        ));
        this.configs.add(this.playerDataConfig = new YamlConfig(
                instance,
                "player_data"
        ));
        this.configs.add(this.dataConfig = new JsonConfig(
                instance,
                "data",
                new ConfigPair<>("backups", new JSONObject())
        ));
    }

    public void load() {
        this.instance.getPluginLogger().info("Loading configuration files...");
        this.configs.forEach(this::loadConfig);
    }

    public void save() {
        this.instance.getPluginLogger().info("Saving configuration files...");
        this.configs.forEach(this::saveConfig);
    }

    public void reload() {
        this.instance.getPluginLogger().info("Reloading configuration files...");
        this.instance.getPluginLogger().disable();
        save();
        load();
        this.instance.getPluginLogger().enable();
    }

    public YamlConfig getSettings() {
        return this.settingsConfig;
    }

    public YamlConfig getLanguage() {
        return this.languageConfig;
    }

    public YamlConfig getPlayerData() {
        return this.playerDataConfig;
    }

    public JsonConfig getData() {
        return this.dataConfig;
    }

    private void loadConfig(Config config) {
        if(config == this.dataConfig && this.instance.isUsingDatabase()) return;
        config.create();
        config.load();
    }

    private void saveConfig(Config config) {
        if(config == this.dataConfig && this.instance.isUsingDatabase()) return;
        config.save();
    }
}
