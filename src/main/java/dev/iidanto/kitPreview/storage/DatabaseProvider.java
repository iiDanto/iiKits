package dev.iidanto.kitPreview.storage;

import dev.iidanto.kitPreview.models.KitHolder;

import java.util.Optional;

public interface DatabaseProvider<T> {

    void start();
    void save(T t);
    Optional<KitHolder> get(String id);
}
