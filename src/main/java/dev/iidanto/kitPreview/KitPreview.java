package dev.iidanto.kitPreview;

import dev.iidanto.kitPreview.models.Kit;
import dev.iidanto.kitPreview.utils.InventoryManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.util.*;

public final class KitPreview extends JavaPlugin {

    @Getter
    public static KitPreview instance;
    @Getter
    public static Map<UUID, Kit> lastLoadedKit = new HashMap<>();
    @Getter
    public static List<Location> regearShulkers = new ArrayList<>();
    @Getter
    public static Map<UUID, Timestamp> lastDamage = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        InventoryManager.register(this);
        getServer().getPluginManager().registerEvents(new ConnectionMessageListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
