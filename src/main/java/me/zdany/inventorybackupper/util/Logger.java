package me.zdany.inventorybackupper.util;

import me.zdany.inventorybackupper.InventoryBackupper;

public final class Logger {

    private final InventoryBackupper instance;
    private boolean enabled;

    public Logger(InventoryBackupper instance) {
        this.instance = instance;
        this.enabled = true;
    }

    public void info(String message) {
        if(!this.enabled) return;
        this.instance.getLogger().info(message);
    }

    public void warn(String message) {
        if(!this.enabled) return;
        this.instance.getLogger().warning(message);
    }

    public void error(String message) {
        if(!this.enabled) return;
        this.instance.getLogger().severe(message);
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }
}
