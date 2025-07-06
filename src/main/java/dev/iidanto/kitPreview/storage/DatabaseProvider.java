package dev.iidanto.kitPreview.storage;

import java.util.Optional;
import java.util.UUID;

public interface DatabaseProvider<T> {

    void start();
    void save(T t);
    Optional<T> get(UUID id);

}
