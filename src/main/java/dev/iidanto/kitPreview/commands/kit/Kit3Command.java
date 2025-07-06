package dev.iidanto.kitPreview.commands.kit;

import dev.iidanto.kitPreview.KitPreview;
import dev.iidanto.kitPreview.cache.KitCache;
import dev.iidanto.kitPreview.models.Kit;
import dev.iidanto.kitPreview.utils.ColorUtils;
import dev.iidanto.kitPreview.utils.PlayerUtils;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Kit3Command extends CommandAPICommand {
    private final KitPreview main = KitPreview.getInstance();
    private final FileConfiguration config = main.getConfig();
    private final String prefix = config.getString("messages.prefix");
    private final String colour = config.getString("messages.colour");

    public Kit3Command(){
        super("k3");
        this.withPermission("vanillacore.k3");
        this.executesPlayer((player, args) -> {
            Kit kit = KitCache.getAllKits(player.getUniqueId()).get(3);
            player.getInventory().clear();
            for (Map.Entry<Integer, ItemStack> entry : kit.getContent().entrySet()) {
                int slot = entry.getKey();
                ItemStack item = entry.getValue();
                if (slot >= 0 && slot < player.getInventory().getSize()) {
                    player.getInventory().setItem(slot, item == null ? new ItemStack(Material.AIR) : item);
                }
            }
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.sendMessage(ColorUtils.parse(prefix + "<gray>You have successfully loaded " + colour + "Kit 3"));
            KitPreview.getLastLoadedKit().put(player.getUniqueId(), kit);
            if (config.getBoolean("general.rekits.announce-rekits")){
                if (config.getInt("general.rekits.distance") == 0){
                    Bukkit.getOnlinePlayers().forEach(player1 -> {
                        player1.sendMessage(ColorUtils.parse(prefix + colour + "<player> <gray>Has Loaded A Kit!".replace("<player>", player.getName())));
                    });
                } else {
                    PlayerUtils.getPlayersInDistance(player.getLocation(), config.getInt("general.rekits.distance")).forEach(player1 -> {
                        player1.sendMessage(ColorUtils.parse(prefix + colour + "<player> <gray>Has Loaded A Kit!".replace("<player>", player.getName())));
                    });
                }
            }
        });
        this.register();
    }
}
