package dev.iidanto.kitPreview;

import dev.iidanto.kitPreview.cache.KitCache;
import dev.iidanto.kitPreview.models.Kit;
import dev.iidanto.kitPreview.models.KitHolder;
import dev.iidanto.kitPreview.storage.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class ConnectionMessageListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        Map<Integer, Kit> kits = KitCache.getAllKits(uuid);
        kits.values().forEach(kit -> DatabaseManager.getInstance().saveKitAsync(kit));

    }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) throws IOException {
        UUID uuid = event.getUniqueId();

        KitHolder kitHolder = DatabaseManager.getInstance().getKitDatabase().loadKits(uuid);
        if (kitHolder != null) {
            kitHolder.getList().forEach((id, kit) -> KitCache.putKit(uuid, kit));
        }
    }
}
