package dev.iidanto.kitPreview.cache;

import dev.iidanto.kitPreview.objects.Kit;
import dev.iidanto.kitPreview.objects.KitHolder;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@UtilityClass
public class KitCache {
    private final Map<UUID, KitHolder> kits = new ConcurrentHashMap<>();

    public Kit getKit(UUID playerUUID, int id) {
        if (kits.get(playerUUID) == null) {
            return null;
        }
        return kits.get(playerUUID).getList().get(id);
    }

    public KitHolder get(UUID uuid) {
        return kits.getOrDefault(uuid, KitHolder.newEmpty(uuid));
    }

    public Map<Integer, Kit> getAll(UUID playerUUID) {
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

    public void put(UUID playerUUID, KitHolder kit) {
        kits.put(playerUUID, kit);
    }

    public void remove(UUID playerUUID, Kit item) {
        kits.compute(playerUUID, (uuid, items) -> {
            items.getList().remove(item.getID());
            return items;
        });
    }

    public Map<UUID, Map<Integer, Kit>> getAllKitsMap() {
        Map<UUID, Map<Integer, Kit>> all = new HashMap<>();
        kits.forEach((uuid, kitHolder) -> all.put(uuid, new HashMap<>(kitHolder.getList())));
        return all;
    }
}