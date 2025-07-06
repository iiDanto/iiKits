package dev.iidanto.kitPreview.storage;


import dev.iidanto.kitPreview.KitPreview;
import dev.iidanto.kitPreview.models.Kit;
import dev.iidanto.kitPreview.models.KitHolder;
import dev.iidanto.kitPreview.utils.ParseUtils;

import java.io.File;
import java.sql.*;
import java.util.*;

public class KitDatabase {

    private final Connection connection;
    private static final String DB_FILE = KitPreview.getInstance().getDataFolder() + File.separator + "kits.db";

    public KitDatabase() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
        start();
    }

    public void start() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS kits (
                    player_id TEXT NOT NULL,
                    kit_id INTEGER NOT NULL CHECK(kit_id BETWEEN 1 AND 9),
                    items TEXT,
                    PRIMARY KEY (player_id, kit_id)
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveKit(Kit kit) {
        if (kit.getID() < 1 || kit.getID() > 9) return;

        try (PreparedStatement statement = connection.prepareStatement("""
            INSERT INTO kits (player_id, kit_id, items)
            VALUES (?, ?, ?)
            ON CONFLICT(player_id, kit_id) DO UPDATE SET
                items = excluded.items
            """)) {
            statement.setString(1, kit.getOwner().toString());
            statement.setInt(2, kit.getID());
            statement.setString(3, ParseUtils.serializeKitItems(kit.getContent()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public KitHolder loadKits(UUID uuid) {
        Map<Integer, Kit> kits = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT * FROM kits WHERE player_id = ?
            """)) {
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int kitId = rs.getInt("kit_id");
                String itemsSerialized = rs.getString("items");

                Map<Integer, org.bukkit.inventory.ItemStack> items = ParseUtils.deserializeItemStackMap(itemsSerialized);
                kits.put(kitId, new Kit(uuid, kitId, items));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 1; i <= 9; i++) {
            kits.putIfAbsent(i, new Kit(uuid, i, new HashMap<>()));
        }

        return new KitHolder(uuid, kits);
    }

    public void deleteKit(UUID uuid, int kitId) {
        try (PreparedStatement statement = connection.prepareStatement("""
                DELETE FROM kits WHERE player_id = ? AND kit_id = ?
            """)) {
            statement.setString(1, uuid.toString());
            statement.setInt(2, kitId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
