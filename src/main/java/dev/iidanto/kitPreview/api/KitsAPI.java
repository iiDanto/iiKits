package dev.iidanto.kitPreview.api;

import dev.iidanto.kitPreview.KitPreview;
import dev.iidanto.kitPreview.cache.KitCache;
import dev.iidanto.kitPreview.events.KitLoadEvent;
import dev.iidanto.kitPreview.objects.Kit;
import dev.iidanto.kitPreview.objects.KitHolder;
import dev.iidanto.kitPreview.storage.DatabaseManager;
import dev.iidanto.kitPreview.storage.DatabaseProvider;
import dev.iidanto.kitPreview.storage.KitDatabase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.xml.crypto.Data;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class KitsAPI {
    public static KitHolder fetchKitHolder(UUID uuid){
        return KitCache.get(uuid);
    }

    public static Kit fetchKit(UUID uuid, int index){
        return KitCache.getKit(uuid, index);
    }

    public static void saveKitHolderAsync(KitHolder kitholder) {
        CompletableFuture.runAsync(() -> DatabaseManager.getInstance().save(KitHolder.class, kitholder));
    }

    public static CompletableFuture<KitHolder> loadKitHolderFromDatabaseAsync(UUID uuid) {
        return DatabaseManager.getInstance().get(KitHolder.class, uuid)
                .thenApply(optional -> optional.orElseGet(() -> KitHolder.newEmpty(uuid)));
    }

    public static void saveKitAsync(Kit kit){
        CompletableFuture.runAsync(() -> DatabaseManager.getInstance().save(Kit.class, kit));
    }

    public static CompletableFuture<Kit> loadKitFromDatabaseAsync(UUID uuid, int index) {
        return DatabaseManager.getInstance().get(KitHolder.class, uuid)
                .thenApply(optionalHolder -> {
                    KitHolder holder = optionalHolder.orElseGet(() -> KitHolder.newEmpty(uuid));
                    return holder.getList().get(index);
                });
    }

    public static void loadKit(Player player, Kit kit){
        player.getInventory().clear();
        for (Map.Entry<Integer, ItemStack> entry : kit.getContent().entrySet()) {
            int slot = entry.getKey();
            ItemStack item = entry.getValue();
            player.getInventory().setItem(slot, entry.getValue());
        }

        Bukkit.getServer().getPluginManager().callEvent(new KitLoadEvent(kit));
    }

    public static void updateKitAsync(UUID uuid, int index, Map<Integer, ItemStack> content){
        CompletableFuture.runAsync(() -> {
            KitHolder holder = KitCache.get(uuid);
            holder.getList().put(index, new Kit(uuid, index, content));
            KitCache.put(uuid, holder);
        });
    }

    public static void updateKit(UUID uuid, int index, Map<Integer, ItemStack> content){
        KitHolder holder = KitCache.get(uuid);
        holder.getList().put(index, new Kit(uuid, index, content));
        KitCache.put(uuid, holder);
    }

    public static void deleteKit(UUID uuid, int index){
        KitHolder holder = KitCache.get(uuid);
        holder.getList().remove(index);
    }
}
