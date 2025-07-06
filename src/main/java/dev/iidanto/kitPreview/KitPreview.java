package dev.iidanto.kitPreview;

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

    public static final Logger LOGGER = Logger.getLogger("PreviewKit"); // Useless

    @Getter
    private static KitPreview instance;

    private Map<UUID, Kit> lastLoadedKit = new HashMap<>();
    private static List<Location> regearShulkers = new ArrayList<>(); // Why in main Class?
    private static Map<UUID, Timestamp> lastDamage = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        DatabaseManager.getInstance();
        InventoryManager.register(this);
        getServer().getPluginManager().registerEvents(new ConnectionMessageListener(), this);
    }


    // It may be clean and good practice to do this. But it's useless tbh.
    @Override
    public void onDisable() {
        lastLoadedKit.clear();
        regearShulkers.clear();
        lastDamage.clear();
    }
}
