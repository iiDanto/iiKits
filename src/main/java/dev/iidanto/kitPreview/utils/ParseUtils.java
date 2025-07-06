package dev.iidanto.kitPreview.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class ParseUtils {
    public static String serializeKitItems(Map<Integer, ItemStack> itemStacks) {
        List<String> serializedItems = new ArrayList<>();
        for (Map.Entry<Integer, ItemStack> entry : itemStacks.entrySet()) {
            int slot = entry.getKey();
            String serializedItem = serializeItemStack(entry.getValue());
            serializedItems.add(slot + ":" + serializedItem);
        }
        return String.join(";", serializedItems);
    }


    public static String serializeItemStack(ItemStack itemStack) {
        ByteArrayOutputStream byteoutput = new ByteArrayOutputStream();
        BukkitObjectOutputStream objectoutput;
        try {
            objectoutput = new BukkitObjectOutputStream(byteoutput);
            objectoutput.writeObject(itemStack);
            objectoutput.close();
            return Base64.getEncoder().encodeToString(byteoutput.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack deserializeToItemStack(String data) {
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream byteinput = new ByteArrayInputStream(bytes);
        try {
            BukkitObjectInputStream objectinput = new BukkitObjectInputStream(byteinput);
            ItemStack itemStack = (ItemStack) objectinput.readObject();
            objectinput.close();
            return itemStack;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<Integer, ItemStack> deserializeItemStackMap(String data) {
        Map<Integer, ItemStack> toReturn = new HashMap<>();
        if (data == null || data.isEmpty()) return toReturn;
        String[] serializedItems = data.split(";");
        for (String serializedItem : serializedItems) {
            String[] parts = serializedItem.split(":", 2);
            if (parts.length != 2) continue;
            int slot;
            try {
                slot = Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                continue;
            }
            ItemStack item = deserializeToItemStack(parts[1]);
            toReturn.put(slot, item);
        }
        return toReturn;
    }

}
