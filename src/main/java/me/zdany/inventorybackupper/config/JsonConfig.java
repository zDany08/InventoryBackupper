package me.zdany.inventorybackupper.config;

import me.zdany.inventorybackupper.InventoryBackupper;
import me.zdany.inventorybackupper.util.Utils;
import me.zdany.inventorybackupper.util.Logger;
import org.json.JSONObject;

import java.io.*;
import java.util.Scanner;

public class JsonConfig extends Config {

    private JSONObject config;

    public JsonConfig(InventoryBackupper instance, String path, ConfigPair<?>... defaults) {
        super(instance, path + ".json", defaults);
        this.config = null;
    }

    @Override
    public void load() {
        if(read().isEmpty()) write("{}");
        JSONObject content = Utils.toJson(read());
        this.config = content == null ? new JSONObject() : content;
        for(ConfigPair<?> def : defaults) {
            if(this.config.has(def.key())) continue;
            this.config.put(def.key(), def.value());
        }
        save();
    }

    @Override
    public void save() {
        write(this.config.toString(4));
    }

    private String read() {
        StringBuilder content = new StringBuilder();
        try(Scanner reader = new Scanner(this.file)) {
            while(reader.hasNextLine()) content.append(reader.nextLine()).append("\n");
        }catch(FileNotFoundException e) {
            this.instance.getPluginLogger().error("Cannot find config \"" + this.path + "\": " + e.getMessage());
        }
        return content.toString();
    }

    private void write(String content) {
        try(FileWriter writer = new FileWriter(this.file)) {
            writer.write(content);
        }catch(IOException e) {
            this.instance.getPluginLogger().error("Cannot write into config \"" + this.path + "\": " + e.getMessage());
        }
    }

    public JSONObject get() {
        return this.config;
    }
}
