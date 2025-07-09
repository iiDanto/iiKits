package dev.iidanto.kitPreview.manager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.iidanto.kitPreview.KitPreview;
import dev.iidanto.kitPreview.utils.SerializeUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class KitRoomManager {

    private final JavaPlugin plugin = KitPreview.getInstance();
    private final File dataFile;
    private final Gson gson = new Gson();

    public KitRoomManager() {
        this.dataFile = new File(plugin.getDataFolder(), "kitroom.json");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
    }


    public void saveKitRoomInventory(Inventory inventory) {
        Map<Integer, ItemStack> itemsToSave = new HashMap<>();
        for (int slot = 0; slot < 45; slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null) {
                itemsToSave.put(slot, item);
            }
        }
        String serialized = SerializeUtils.serializeKitItems(itemsToSave);
        try (FileWriter writer = new FileWriter(dataFile)) {
            JsonObject obj = new JsonObject();
            obj.addProperty("kit", serialized);
            writer.write(gson.toJson(obj));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save kitroom.json");
            e.printStackTrace();
        }
    }

    public Inventory loadKitRoomInventory() {
        Inventory inventory = Bukkit.createInventory(null, 54, "Kit Room");
        if (!dataFile.exists()) {
            return inventory;
        }
        try (FileReader reader = new FileReader(dataFile)) {
            JsonObject obj = gson.fromJson(reader, JsonObject.class);
            if (obj.has("kit")) {
                String serialized = obj.get("kit").getAsString();
                Map<Integer, ItemStack> items = SerializeUtils.deserializeItemStackMap(serialized);

                for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                    if (entry.getKey() >= 0 && entry.getKey() < 45) {
                        inventory.setItem(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load kitroom.json");
            e.printStackTrace();
        }
        return inventory;
    }
}
