package dev.iidanto.kitPreview.menus;

import dev.iidanto.kitPreview.KitPreview;
import dev.iidanto.kitPreview.manager.KitRoomManager;
import dev.iidanto.kitPreview.utils.ColorUtils;
import dev.iidanto.kitPreview.utils.InventoryBuilder;
import dev.iidanto.kitPreview.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


// The Code just looks ugly tbh
public class KitRoomMenu extends InventoryBuilder {
    private final Player player;
    private final KitRoomManager kitRoomManager;

    public KitRoomMenu(Player player, boolean isFromKitMenu) {
        super(54, KitPreview.getInstance().getConfig().getString("messages.colour") + "ᴋɪᴛ ʀᴏᴏᴍ");
        this.player = player;
        this.kitRoomManager = new KitRoomManager();
        loadKitItems();
        String colour = KitPreview.getInstance().getConfig().getString("messages.colour");
        if (player.hasPermission("vanillacore.kitroom.admin")){
            this.setItem(45, ItemBuilder.item(Material.GREEN_WOOL)
                    .name(colour + "Save")
                    .glow()
                    .lore(
                            ColorUtils.empty(),
                            ColorUtils.parse(colour + "➥ <gray>Click to Save")
                    )
                    .build(), event -> {
                event.setCancelled(true);
                kitRoomManager.saveKitRoomInventory(this.getInventory());
                player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f, 5.0f);
                player.closeInventory();
            });
        }

        this.setItem(53, ItemBuilder.item(Material.BARRIER)
                .name(colour + "Close Kit Room")
                .glow()
                .lore(
                        ColorUtils.empty(),
                        ColorUtils.parse(colour + "➥ <gray>Click to Close")
                )
                .build(), event -> {
            event.setCancelled(true);
            if (isFromKitMenu){
                new KitMenu(player).open(player);
            } else {
                player.closeInventory();
            }
            player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0F, 5.0f);
        });

        ItemStack filler = ItemBuilder.item(Material.BLACK_STAINED_GLASS_PANE)
                .name(" ")
                .build();

        for (int slot = 46; slot <= 52; slot++) {
            this.setItem(slot, filler, event -> event.setCancelled(true));
        }

        ItemStack[] items = new ItemStack[] {
                new ItemBuilder(Material.END_CRYSTAL)
                        .name(colour + "Restock Kit Room")
                        .glow()
                        .lore(
                                ColorUtils.empty(),
                                ColorUtils.parse(colour + "➥ <gray>Click to Restock")
                        )
                        .build(),
                new ItemBuilder(Material.RESPAWN_ANCHOR)
                        .name(colour + "Restock Kit Room")
                        .glow()
                        .lore(
                                ColorUtils.empty(),
                                ColorUtils.parse(colour + "➥ <gray>Click to Restock")
                        )
                        .build(),
                new ItemBuilder(Material.TOTEM_OF_UNDYING)
                        .name(colour + "Restock Kit Room")
                        .glow()
                        .lore(
                                ColorUtils.empty(),
                                ColorUtils.parse(colour + "➥ <gray>Click to Restock")
                        )
                        .build(),
                new ItemBuilder(Material.GLOWSTONE)
                        .name(colour + "Restock Kit Room")
                        .glow()
                        .lore(
                                ColorUtils.empty(),
                                ColorUtils.parse(colour + "➥ <gray>Click to Restock")
                        )
                        .build(),
                new ItemBuilder(Material.OBSIDIAN)
                        .name(colour + "Restock Kit Room")
                        .glow()
                        .lore(
                                ColorUtils.empty(),
                                ColorUtils.parse(colour + "➥ <gray>Click to Restock")
                        )
                        .build()
        };
        this.setAnimatedItem(49, items, 2, event -> {
            event.setCancelled(true);
            loadKitItems();
            player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f, 5.0f);
        });
        this.open(player);
    }

    private void loadKitItems() {
        Inventory loadedInventory = kitRoomManager.loadKitRoomInventory();
        for (int i = 0; i < 45; i++) {
            ItemStack item = loadedInventory.getItem(i);
            if (item != null) {
                this.setItem(i, item, event -> {});
            } else {
                this.setItem(i, null);
            }
        }
    }
}
