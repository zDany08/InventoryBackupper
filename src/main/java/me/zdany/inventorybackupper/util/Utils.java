package me.zdany.inventorybackupper.util;

import me.zdany.inventorybackupper.InventoryBackupper;
import me.zdany.inventorybackupper.backup.BackupTrigger;
import me.zdany.inventorybackupper.config.YamlConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Utils {

    public static String capitalize(String str) {
        if(str.isEmpty() || str.isBlank()) return str;
        char[] chars = str.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return String.valueOf(chars);
    }

    public static void generateDataFolder(InventoryBackupper instance) {
        if(instance.getDataFolder().exists()) return;
        if(instance.getDataFolder().mkdir()) return;
        instance.getPluginLogger().error("Cannot create plugin data folder.");
    }

    public static String getColor(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static void sendMessage(CommandSender sender, String message) {
        if(message.isEmpty()) return;
        sender.sendMessage(message);
    }

    public static void sendMessage(Player player, String message) {
        if(message.isEmpty()) return;
        player.sendMessage(message);
    }

    public static byte[] serialize(InventoryBackupper instance, ItemStack stack) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitOut = new BukkitObjectOutputStream(out);
            bukkitOut.writeObject(stack);
            bukkitOut.flush();
            return out.toByteArray();
        }catch(IOException e) {
            instance.getPluginLogger().error("Cannot serialize an item stack: " + e.getMessage());
            return null;
        }
    }

    public static String stringify(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] unstringify(String data) {
        return Base64.getDecoder().decode(data);
    }

    public static ItemStack deserialize(InventoryBackupper instance, byte[] data) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            BukkitObjectInputStream bukkitIn = new BukkitObjectInputStream(in);
            return (ItemStack) bukkitIn.readObject();
        }catch(IOException | ClassNotFoundException e) {
            instance.getPluginLogger().error("Cannot deserialize an item stack: " + e.getMessage());
            return null;
        }
    }

    public static JSONObject toJson(String str) {
        try {
            return new JSONObject(str);
        }catch(JSONException e) {
            Bukkit.getLogger().severe("Cannot convert \"" + str + "\" to json: " + e.getMessage());
            return null;
        }
    }

    public static int getBackups(InventoryBackupper instance, Player player, BackupTrigger trigger) {
        FileConfiguration config = instance.getConfigManager().getPlayerData().get();
        String path = "players." + player.getUniqueId() + "." + trigger.toString().toLowerCase() + "-backups";
        return config.getInt(path, 0);
    }

    public static void incrementBackups(InventoryBackupper instance, Player player, BackupTrigger trigger) {
        YamlConfig config = instance.getConfigManager().getPlayerData();
        String path = "players." + player.getUniqueId() + "." + trigger.toString().toLowerCase() + "-backups";
        int backups = config.get().getInt(path);
        config.get().set(path, backups + 1);
        config.save();
    }
}
