package dev.iidanto.kitPreview.storage;

import dev.iidanto.kitPreview.KitPreview;
import dev.iidanto.kitPreview.objects.KitHolder;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class DatabaseManager {

    private static DatabaseManager manager;
    private Connection connection;
    private boolean isConnected = false;
    private final Map<Class<?>, DatabaseProvider<?>> providers = new HashMap<>();

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        if (manager == null) {
            manager = new DatabaseManager();
            manager.connect();
        }
        return manager;
    }

    private void connect() {
        FileConfiguration config = KitPreview.getInstance().getConfig();
        String type = config.getString("storage.type", "SQLite");

        try {
            if ("MySQL".equalsIgnoreCase(type)) {
                String url = config.getString("storage.credentials.url");
                String username = config.getString("storage.credentials.username");
                String password = config.getString("storage.credentials.password");

                if (url == null || username == null || password == null) {
                    throw new IllegalArgumentException("MySQL credentials missing in config.yml");
                }

                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, username, password);

            } else {
                Class.forName("org.sqlite.JDBC");
                String dataFolder = KitPreview.getInstance().getDataFolder().getAbsolutePath();
                String sqliteUrl = "jdbc:sqlite:" + dataFolder + "/kits.db";
                connection = DriverManager.getConnection(sqliteUrl);
            }
            isConnected = true;
            registerProviders();
            for (DatabaseProvider<?> provider : providers.values()) {
                provider.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
            isConnected = false;
        }
    }

    private void registerProviders() {
        providers.put(KitHolder.class, new KitDatabase(connection));
    }

    public <T> CompletableFuture<Optional<T>> get(Class<T> clazz, UUID uuid) {
        if (!isConnected) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return CompletableFuture.supplyAsync(() -> (Optional<T>) providers.get(clazz).get(uuid));
    }

    public <T> CompletableFuture<Void> save(Class<T> clazz, T t) {
        if (!isConnected) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> ((DatabaseProvider<T>) providers.get(clazz)).save(t));
    }
}
