package dev.iidanto.kitPreview.menus;

import dev.iidanto.kitPreview.KitPreview;
import dev.iidanto.kitPreview.cache.KitCache;
import dev.iidanto.kitPreview.manager.KitRoomManager;
import dev.iidanto.kitPreview.models.Kit;
import dev.iidanto.kitPreview.utils.ColorUtils;
import dev.iidanto.kitPreview.utils.InventoryBuilder;
import dev.iidanto.kitPreview.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class KitMenu extends InventoryBuilder {
    private final Player player;
    private final KitRoomManager kitRoomManager;
    String colour = KitPreview.getInstance().getConfig().getString("messages.colour");

    public KitMenu(Player player) {
        super(36, KitPreview.getInstance().getConfig().getString("messages.colour") + "Kits");
        this.player = player;
        this.kitRoomManager = new KitRoomManager();
        ItemStack bg = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(ColorUtils.empty())
                .build();
        for (int x = 1; x <= 35; x++){
            this.setItem(x, bg);
        }
        for (int i = 1; i <= 9; i++) {
            int slot = i - 1;
            int id = i;
            this.setItem(slot, new ItemBuilder(Material.CHEST)
                    .name(colour + "Kit " + id)
                    .lore(
                            ColorUtils.empty(),
                            ColorUtils.parse(colour + "➥ <gray>Click to Open")
                    )
                    .build(), event -> {
                event.setCancelled(true);
                Kit kit = KitCache.getAllKits(player.getUniqueId()).get(id);
                if (kit != null) {
                    new KitDisplayMenu(player, kit, true, false).open(player);
                } else {
                    player.sendActionBar(ColorUtils.parse("<red>✗ Kit #%id% does not exist.".replace("%id%", Integer.toString(id))));
                    player.closeInventory();
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
                }
            });
        }
        this.setItem(23, new ItemBuilder(Material.REDSTONE_BLOCK)
                .name(colour + "Clear Inventory")
                .glow()
                .lore(
                        ColorUtils.empty(),
                        ColorUtils.parse(colour + "➥ <gray>Click to clear your inventory")
                ).build(), event -> {
            event.setCancelled(true);
            player.getInventory().clear();
            player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f, 5.0f);
                }
        );
        this.setItem(21, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .name(colour + "Repair Items")
                .glow()
                .lore(
                        ColorUtils.empty(),
                        ColorUtils.parse(colour + "➥ <gray>Click to Repair")
                ).build(), event -> {
            event.setCancelled(true);
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR) continue;
                if (item.getItemMeta() instanceof Damageable damageable) {
                    damageable.setDamage(0);
                    item.setItemMeta(damageable);
                }
            }
            player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f, 5.0f);
            player.sendActionBar(ColorUtils.parse("<green>✔ Repaired all damageable items"));
        });
        ItemStack[] items = new ItemStack[] {
                new ItemBuilder(Material.END_CRYSTAL)
                        .name(colour + "Kit Room")
                        .glow()
                        .lore(
                                ColorUtils.empty(),
                                ColorUtils.parse(colour + "➥ <gray>Click to Open")
                        )
                        .build(),
                new ItemBuilder(Material.RESPAWN_ANCHOR)
                        .name(colour + "Kit Room")
                        .glow()
                        .lore(
                                ColorUtils.empty(),
                                ColorUtils.parse(colour + "➥ <gray>Click to Open")
                        )
                        .build(),
                new ItemBuilder(Material.TOTEM_OF_UNDYING)
                        .name(colour + "Kit Room")
                        .glow()
                        .lore(
                                ColorUtils.empty(),
                                ColorUtils.parse(colour + "➥ <gray>Click to Open")
                        )
                        .build(),
                new ItemBuilder(Material.GLOWSTONE)
                        .name(colour + "Kit Room")
                        .glow()
                        .lore(
                                ColorUtils.empty(),
                                ColorUtils.parse(colour + "➥ <gray>Click to Open")
                        )
                        .build(),
                new ItemBuilder(Material.OBSIDIAN)
                        .name(colour + "Kit Room")
                        .glow()
                        .lore(
                                ColorUtils.empty(),
                                ColorUtils.parse(colour + "➥ <gray>Click to Open")
                        )
                        .build()
        };
        this.setAnimatedItem(22, items, 2, event -> {
            event.setCancelled(true);
            new KitRoomMenu(player, true).open(player);
        });
    }
}
