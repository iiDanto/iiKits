package dev.iidanto.kitPreview;

import dev.iidanto.kitPreview.commands.KitCommand;
import dev.iidanto.kitPreview.commands.RegearCommand;
import dev.iidanto.kitPreview.listeners.ConnectionListener;
import dev.iidanto.kitPreview.objects.Kit;
import dev.iidanto.kitPreview.storage.DatabaseManager;
import dev.iidanto.kitPreview.utils.InventoryManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

@Getter
public final class KitPreview extends JavaPlugin {

    public static final Logger LOGGER = Logger.getLogger("iiKits");

    @Getter
    private static KitPreview instance;

    private Map<UUID, Kit> lastLoadedKit = new HashMap<>();
    private static List<Location> regearShulkers = new ArrayList<>();
    private static Map<UUID, Timestamp> lastDamage = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        new KitCommand().setup();
        new KitCommand().register();
        new RegearCommand();

        DatabaseManager.getInstance();
        InventoryManager.register(this);
        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
    }

    @Override
    public void onDisable() {
        lastLoadedKit.clear();
        regearShulkers.clear();
        lastDamage.clear();
    }
}
