package me.zdany.inventorybackupper.config;

import me.zdany.inventorybackupper.InventoryBackupper;
import me.zdany.inventorybackupper.util.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;

public class YamlConfig extends Config {

    private FileConfiguration config;

    public YamlConfig(InventoryBackupper instance, String path, ConfigPair<?>... defaults) {
        super(instance, path + ".yml", defaults);
    }

    @Override
    public void load() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
        for(ConfigPair<?> def : defaults) {
            if(this.config.contains(def.key())) continue;
            this.config.set(def.key(), def.value());
        }
        save();
    }

    @Override
    public void save() {
        try {
            this.config.save(this.file);
        }catch(IOException e) {
            this.instance.getPluginLogger().error("Cannot save config \"" + this.path + "\": " + e.getMessage());
        }
    }

    public FileConfiguration get() {
        return this.config;
    }
}
