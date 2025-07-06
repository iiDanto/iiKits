package dev.iidanto.kitPreview.menus;

import dev.iidanto.kitPreview.KitPreview;
import dev.iidanto.kitPreview.models.Kit;
import dev.iidanto.kitPreview.utils.ColorUtils;
import dev.iidanto.kitPreview.utils.InventoryBuilder;
import dev.iidanto.kitPreview.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RegearMenu extends InventoryBuilder {
    String colour;
    public RegearMenu(Player player, Location location){
        super(27, KitPreview.getInstance().getConfig().getString("messages.colour") + "Regear Shulker");
        colour = KitPreview.getInstance().getConfig().getString("messages.colour");
        this.setItem(13, new ItemBuilder(Material.SHULKER_SHELL)
                        .name(colour + "Regear Shell")
                        .lore(ColorUtils.empty(),
                                ColorUtils.parse(colour + "➥ <gray>Click to Use"))
                        .glow().build(), event -> {
                    event.setCancelled(true);
                    Kit kit = KitPreview.getLastLoadedKit().get(player.getUniqueId());

                    if (kit == null) {
                        player.sendActionBar(ColorUtils.parse("<red>✖ No kit found."));
                        player.closeInventory();
                        return;
                    }

                    List<String> whitelistStrings = KitPreview.getInstance().getConfig().getStringList("whitelist");
                    Set<Material> whitelist = whitelistStrings.stream()
                            .map(name -> {
                                try {
                                    return Material.valueOf(name);
                                } catch (IllegalArgumentException e) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

            for (Map.Entry<Integer, ItemStack> entry : kit.getContent().entrySet()) {
                int slot = entry.getKey();
                ItemStack kititem = entry.getValue();

                if (kititem == null || !whitelist.contains(kititem.getType())) {
                    continue;
                }

                ItemStack item = player.getInventory().getItem(slot);

                if (item == null || item.getType() == Material.AIR) {
                    player.getInventory().setItem(slot, kititem.clone());
                } else if (item.isSimilar(kititem)) {
                    int max = kititem.getMaxStackSize();
                    int amt = item.getAmount() + kititem.getAmount();

                    int newAmount = Math.min(amt, max);
                    item.setAmount(newAmount);
                    player.getInventory().setItem(slot, item);
                }
            }
            location.getBlock().setType(Material.AIR);
            player.closeInventory();

            player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f, 5.0f);
            player.sendActionBar(ColorUtils.parse(colour + "<green>✔ Regear complete!"));
        }
        );
    }
}
