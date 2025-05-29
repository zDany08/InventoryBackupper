package me.zdany.inventorybackupper.config;

import me.zdany.inventorybackupper.InventoryBackupper;
import me.zdany.inventorybackupper.util.Logger;

import java.io.File;
import java.io.IOException;

public abstract class Config {

    protected final InventoryBackupper instance;
    protected final String path;
    protected final File file;
    protected final ConfigPair<?>[] defaults;

    public Config(InventoryBackupper instance, String path, ConfigPair<?>... defaults) {
        this.instance = instance;
        this.path = path;
        this.defaults = defaults;
        this.file = new File(instance.getDataFolder(), path);
    }

    public void create() {
        if(this.file.exists()) return;
        String errorMessage = "Cannot create config \"" + this.path + "\"";
        try {
            if(!this.file.createNewFile()) this.instance.getPluginLogger().error(errorMessage + ".");
        }catch(IOException e) {
            this.instance.getPluginLogger().error(errorMessage + ": " + e.getMessage());
        }
    }

    public void reload() {
        save();
        load();
    }

    public abstract void load();

    public abstract void save();
}
