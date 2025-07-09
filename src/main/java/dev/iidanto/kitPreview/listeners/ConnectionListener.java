package dev.iidanto.kitPreview.listeners;

import dev.iidanto.kitPreview.cache.KitCache;
import dev.iidanto.kitPreview.objects.KitHolder;
import dev.iidanto.kitPreview.storage.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        final UUID uuid = event.getUniqueId();

        DatabaseManager.getInstance().get(KitHolder.class, uuid).thenAcceptAsync(kitHolderRef -> {
            kitHolderRef.ifPresentOrElse(kitHolder -> KitCache.put(uuid, kitHolder), () -> KitCache.put(uuid, KitHolder.newEmpty(uuid)));
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        final UUID uuid = event.getPlayer().getUniqueId();

        DatabaseManager.getInstance().save(KitHolder.class, KitCache.get(uuid));
    }
}
