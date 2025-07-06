package dev.iidanto.kitPreview.cache;

import dev.iidanto.kitPreview.models.Kit;
import dev.iidanto.kitPreview.models.KitHolder;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class KitCache {
    private final Map<UUID, KitHolder> kits = new ConcurrentHashMap<>();

    public Map<Integer, Kit> getAllKits(UUID playerUUID) {
        if (kits.get(playerUUID) == null) {
            return new HashMap<>();
        }
        Map<Integer, Kit> toreturn = new HashMap<>();
        kits.compute(playerUUID, (uuid, items) -> {
            toreturn.putAll(items.getList());
            return items;
        });
        return toreturn;
    }

    public void putKit(UUID playerUUID, Kit kit) {
        kits.computeIfAbsent(playerUUID, KitHolder::newEmpty);
        KitHolder kitHolder = kits.get(playerUUID);
        kitHolder.getList().remove(kit.getID());
        kitHolder.getList().put(kit.getID(), kit);
    }

    public void removeKit(UUID playerUUID, Kit item) {
        kits.compute(playerUUID, (uuid, items) -> {
            items.getList().remove(item.getID());
            return items;
        });
    }

    public Map<UUID, Map<Integer, Kit>> getAllKitsMap() {
        Map<UUID, Map<Integer, Kit>> all = new HashMap<>();
        kits.forEach((uuid, kitHolder) -> {
            all.put(uuid, new HashMap<>(kitHolder.getList()));
        });
        return all;
    }
}