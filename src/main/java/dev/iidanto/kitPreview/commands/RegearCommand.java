package dev.iidanto.kitPreview.commands;

import dev.iidanto.kitPreview.KitPreview;
import dev.iidanto.kitPreview.utils.ColorUtils;
import dev.iidanto.kitPreview.utils.ItemBuilder;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class RegearCommand extends CommandAPICommand {
    private final KitPreview main;
    private final FileConfiguration config;
    private final String prefix;
    private final String colour;

    public RegearCommand(){
        super("regear");
        main = KitPreview.getInstance();
        config = main.getConfig();
        prefix = config.getString("messages.prefix");
        colour = config.getString("messages.colour");
        this.withAliases("rg");
        this.withPermission("iikits.regear");
        this.executesPlayer((player, args) -> {
            ItemStack rgshulk = new ItemBuilder(Material.SHULKER_BOX)
                    .name(colour + "ʀᴇɢᴇᴀʀ ꜱʜᴜʟᴋᴇʀ")
                    .addContainerValue(main, "regearshulker", true)
                    .lore(
                            ColorUtils.empty(),
                            ColorUtils.parse(colour + "➥ <gray>Place to Use")
                    ).build();
            player.getInventory().addItem(rgshulk);
            player.sendMessage(ColorUtils.parse(prefix + "<gray>You have been given a " + colour + "regear shulker<gray>!"));
        });
        this.register();
    }
}
