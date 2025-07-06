package dev.iidanto.kitPreview.menus;

import dev.iidanto.kitPreview.KitPreview;
import dev.iidanto.kitPreview.cache.KitCache;
import dev.iidanto.kitPreview.models.Kit;
import dev.iidanto.kitPreview.utils.ColorUtils;
import dev.iidanto.kitPreview.utils.InventoryBuilder;
import dev.iidanto.kitPreview.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitDisplayMenu extends InventoryBuilder {
    String colour = KitPreview.getInstance().getConfig().getString("messages.colour");

    public KitDisplayMenu(Player player, Kit kit, boolean isFromKitMenu, boolean viewMenu){
        super(54, "<#28a8ff>Kit Display Menu - Kit #" + kit.getID());
        getInventory().clear();
        Map<Integer, ItemStack> items = kit.getContent();
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            int slot = entry.getKey();
            ItemStack item = entry.getValue();
            if (item != null && slot >= 0 && slot < getInventory().getSize()) {
                getInventory().setItem(slot, item);
            }
        }
        this.setViewOnly(true);
        if (!viewMenu){
            this.setItem(51, new ItemBuilder(Material.GREEN_CONCRETE)
                    .name(colour + "Save Kit")
                    .glow()
                    .lore(
                            ColorUtils.empty(),
                            ColorUtils.parse(colour + "➥ <gray>Click to Save")
                    ).build(), event -> {
                event.setCancelled(true);
                player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f, 5.0f);
                Map<Integer, ItemStack> itemstacks = new HashMap<>();
                for (int i = 0; i < getInventory().getSize(); i++) {
                    if (i == 53) continue;

                    ItemStack item = getInventory().getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                        itemstacks.put(i, item);
                    }
                }

                kit.setContent(itemstacks);
                KitCache.putKit(player.getUniqueId(), kit);
                player.closeInventory();
                player.sendActionBar(ColorUtils.parse("<green>✔ Successfully Saved Kit #" + kit.getID()));
            });
            this.setItem(52, new ItemBuilder(Material.CHEST)
                    .name(colour + "Import Inventory")
                    .glow()
                    .lore(
                            ColorUtils.empty(),
                            ColorUtils.parse(colour + "➥ <gray>Click to Import!")
                    ).build(), event -> {
                event.setCancelled(true);
                player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.5f);
                for (int i = 0; i < getInventory().getSize(); i++) {
                    if (i == 51 || i == 53 || i == 52) continue;
                    getInventory().setItem(i, null);
                }
                for (int i = 0; i < player.getInventory().getSize(); i++) {
                    ItemStack item = player.getInventory().getItem(i);
                    if (item != null && item.getType() != Material.AIR) {
                        if (i < getInventory().getSize()) {
                            getInventory().setItem(i, item.clone());
                        }
                    }
                }
            });
        }
        this.setItem(53, new ItemBuilder(Material.BARRIER)
                .name("<red>Close")
                .glow()
                .lore(ColorUtils.empty(),
                        ColorUtils.parse(colour + "➥ <gray>Click to Close"))
                .build(), event -> {
            event.setCancelled(true);
            if (isFromKitMenu){
                new KitMenu(player).open(player);
            } else {
                player.closeInventory();
            }
            player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f, 5.0f);
                }
        );
    }
}
