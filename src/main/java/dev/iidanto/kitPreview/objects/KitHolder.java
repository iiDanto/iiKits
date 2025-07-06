package dev.iidanto.kitPreview.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*

Rather use records. They are literally the same and cleaner.

 */

@RequiredArgsConstructor @Getter
public class KitHolder {

    private final UUID uuid;
    private final Map<Integer, Kit> list;

    public static KitHolder newEmpty(UUID uuid) {
        return new KitHolder(uuid, new HashMap<>());
    }


}