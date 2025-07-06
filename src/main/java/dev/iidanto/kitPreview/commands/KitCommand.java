package dev.iidanto.kitPreview.commands;

import dev.iidanto.kitPreview.KitPreview;
import dev.iidanto.kitPreview.cache.KitCache;
import dev.iidanto.kitPreview.menus.KitDisplayMenu;
import dev.iidanto.kitPreview.menus.KitMenu;
import dev.iidanto.kitPreview.menus.KitRoomMenu;
import dev.iidanto.kitPreview.models.Kit;
import dev.iidanto.kitPreview.storage.DatabaseManager;
import dev.iidanto.kitPreview.utils.ColorUtils;
import dev.iidanto.kitPreview.utils.PlayerUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitCommand extends CommandAPICommand {
    private final KitPreview main = KitPreview.getInstance();
    private final FileConfiguration config = main.getConfig();
    private final String prefix = config.getString("messages.prefix");
    private final String colour = config.getString("messages.colour");

    public KitCommand() {
        super("kit");
        this.executesPlayer((player, args) -> {
            new KitMenu(player).open(player);
        });
        this.withSubcommand(new CommandAPICommand("view")
                .withPermission("vanillacore.kits.view")
                .withArguments(new OfflinePlayerArgument("player"), new IntegerArgument("kit", 1, 9))
                .executesPlayer((player, args) -> {
                    OfflinePlayer target = (OfflinePlayer) args.get("player");
                    int kitint = (int) args.get("kit");
                    Kit kit = KitCache.getAllKits(target.getUniqueId()).get(kitint);
                    if (kit == null){
                        DatabaseManager.getInstance().getKitDatabase().loadKits(target.getUniqueId());
                        kit = KitCache.getAllKits(target.getUniqueId()).get(kitint);
                        if (kit == null || kit.getContent().isEmpty()){
                            player.sendActionBar(ColorUtils.parse("<red>✗ Kit #%id% does not exist.".replace("%id%", Integer.toString(kitint))));
                            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                            return;
                        }
                    }
                    new KitDisplayMenu(player, kit, false, true).open(player);
                }));
        this.withSubcommand(new CommandAPICommand("preview")
                .withPermission("vanillacore.kits.preview")
                .withArguments(new IntegerArgument("kit", 1, 9))
                .executesPlayer((player, args) -> {
                    new KitDisplayMenu(player, KitCache.getAllKits(player.getUniqueId()).get(args.get("kit")), false, false).open(player);
                    player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0F, 5.0f);
                }));
        this.withSubcommand(new CommandAPICommand("save")
                .withPermission("vanillacore.kits.save")
                .withArguments(new IntegerArgument("kit", 1, 9))
                .executesPlayer((player, args) -> {
                    int kit = (int) args.get("kit");
                    Map<Integer, ItemStack> items = new HashMap<>();

                    for (int i = 0; i < player.getInventory().getSize(); i++) {
                        ItemStack item = player.getInventory().getItem(i);
                        if (item != null && item.getType() != Material.AIR) {
                            items.put(i, item.clone());
                        }
                    }

                    KitCache.putKit(player.getUniqueId(), new Kit(player.getUniqueId(), kit, items));
                    player.sendMessage(ColorUtils.parse(prefix + "<gray>Successfully saved " + colour + "Kit <int>".replace("<int>", String.valueOf(kit))));
                })
        );

        this.withSubcommand(new CommandAPICommand("load")
                .withPermission("vanillacore.kits.load")
                .withArguments(new IntegerArgument("kit", 1, 9))
                .executesPlayer((player, args) -> {
                    int kitnum = (int) args.get("kit");
                    Kit kit = KitCache.getAllKits(player.getUniqueId()).get(kitnum);

                    if (kit == null || kit.getContent().isEmpty()) {
                        player.sendMessage(ColorUtils.parse(prefix + "<red>That kit does not exist or is empty."));
                        return;
                    }

                    player.getInventory().clear();
                    System.out.println("Loading kit " + kitnum + ": " + kit.getContent());

                    for (Map.Entry<Integer, ItemStack> entry : kit.getContent().entrySet()) {
                        int slot = entry.getKey();
                        ItemStack item = entry.getValue();
                        if (slot >= 0 && slot < player.getInventory().getSize()) {
                            player.getInventory().setItem(slot, item == null ? new ItemStack(Material.AIR) : item);
                        }
                    }

                    player.setFoodLevel(20);
                    player.setSaturation(20);

                    KitPreview.getLastLoadedKit().put(player.getUniqueId(), kit);
                    player.sendMessage(ColorUtils.parse(prefix + "<gray>You have successfully loaded " + colour + "Kit <int>".replace("<int>", String.valueOf(kitnum))));
                    if (config.getBoolean("general.rekits.announce-rekits")) {
                        int distance = config.getInt("general.rekits.distance");
                        if (distance == 0) {
                            Bukkit.getOnlinePlayers().forEach(p ->
                                    p.sendMessage(ColorUtils.parse(prefix + colour + player.getName() + " <gray>Has Loaded A Kit!"))
                            );
                        } else {
                            PlayerUtils.getPlayersInDistance(player.getLocation(), distance).forEach(p ->
                                    p.sendMessage(ColorUtils.parse(prefix + colour + player.getName() + " <gray>Has Loaded A Kit!"))
                            );
                        }
                    }
                })
        );

        this.withSubcommand(new CommandAPICommand("room")
                .withPermission("vanillacore.kitroom")
                .executesPlayer((player, args) -> {
                    new KitRoomMenu(player, false).open(player);
                }));

        this.register();
    }
}
