package dev.iidanto.kitPreview.storage;

import dev.iidanto.kitPreview.models.Kit;

import java.util.concurrent.CompletableFuture;
import java.util.UUID;

public final class DatabaseManager {

    private static DatabaseManager manager;
    private boolean isConnected = false;
    private KitDatabase kitDatabase;

    public DatabaseManager() {
        manager = this;
        try {
            kitDatabase = new KitDatabase();
            isConnected = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize KitDatabase!", e);
        }
    }

    public static DatabaseManager getInstance() {
        if (manager == null) {
            synchronized (DatabaseManager.class) {
                if (manager == null) {
                    manager = new DatabaseManager();
                }
            }
        }
        return manager;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public KitDatabase getKitDatabase() {
        return kitDatabase;
    }

    public CompletableFuture<Void> saveKitAsync(Kit kit) {
        if (!isConnected) return CompletableFuture.completedFuture(null);
        return CompletableFuture.runAsync(() -> kitDatabase.saveKit(kit));
    }

    public CompletableFuture<Void> deleteKitAsync(UUID uuid, int kitId) {
        if (!isConnected) return CompletableFuture.completedFuture(null);
        return CompletableFuture.runAsync(() -> kitDatabase.deleteKit(uuid, kitId));
    }

    public void close() {
        if (kitDatabase != null) {
            try {
                kitDatabase.closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isConnected = false;
    }
}
