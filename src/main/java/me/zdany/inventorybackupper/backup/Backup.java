package me.zdany.inventorybackupper.backup;

import me.zdany.inventorybackupper.InventoryBackupper;
import me.zdany.inventorybackupper.util.JsonSerializable;
import me.zdany.inventorybackupper.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.UUID;

public class Backup implements JsonSerializable {

    private final InventoryBackupper instance;
    private final UUID owner;
    private final ZonedDateTime createdAt;
    private final BackupTrigger trigger;
    private final ItemStack[] contents;

    public Backup(InventoryBackupper instance, UUID owner, BackupTrigger trigger, ItemStack[] contents) {
        this.instance = instance;
        this.owner = owner;
        this.createdAt = ZonedDateTime.now();
        this.trigger = trigger;
        this.contents = contents;
    }

    public Backup(InventoryBackupper instance, UUID owner, ZonedDateTime createdAt, BackupTrigger trigger, ItemStack[] contents) {
        this.instance = instance;
        this.owner = owner;
        this.createdAt = createdAt;
        this.trigger = trigger;
        this.contents = contents;
    }

    @Override
    public JSONObject serialize() {
        JSONObject obj = new JSONObject();
        obj.put("owner", owner.toString());
        obj.put("createdAt", createdAt.toInstant().getEpochSecond());
        obj.put("trigger", trigger.toString());
        JSONArray contents = new JSONArray();
        for(ItemStack content : this.contents) if(content != null && content.getType() != Material.AIR) contents.put(Utils.stringify(Utils.serialize(this.instance, content)));
        obj.put("contents", contents);
        return obj;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public BackupTrigger getTrigger() {
        return this.trigger;
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public static Backup deserialize(InventoryBackupper instance, JSONObject obj) {
        JSONArray contentsArr = obj.getJSONArray("contents");
        Iterator<Object> iter = contentsArr.iterator();
        ItemStack[] contents = new ItemStack[contentsArr.length()];
        int index = 0;
        while(iter.hasNext()) contents[index++] = Utils.deserialize(instance, Utils.unstringify((String) iter.next()));
        return new Backup(
                instance,
                UUID.fromString(obj.getString("owner")),
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(obj.getLong("createdAt")), ZoneId.systemDefault()),
                BackupTrigger.valueOf(obj.getString("trigger")),
                contents
        );
    }
}
