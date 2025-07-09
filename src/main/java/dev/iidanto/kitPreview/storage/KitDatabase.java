package dev.iidanto.kitPreview.storage;

import dev.iidanto.kitPreview.objects.Kit;
import dev.iidanto.kitPreview.objects.KitHolder;
import dev.iidanto.kitPreview.utils.SerializeUtils;

import java.sql.*;
import java.util.*;

public class KitDatabase implements DatabaseProvider<KitHolder> {

    private final Connection connection;
    private final boolean isMySQL;

    public KitDatabase(Connection connection) {
        this.connection = connection;
        try {
            String dbProductName = connection.getMetaData().getDatabaseProductName().toLowerCase();
            isMySQL = dbProductName.contains("mysql");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to detect database type", e);
        }
    }

    @Override
    public void start() {
        try (Statement statement = connection.createStatement()) {
            if (isMySQL) {
                statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS kits (
                        player_id VARCHAR(36) NOT NULL,
                        kit_id INT NOT NULL CHECK(kit_id BETWEEN 1 AND 9),
                        items TEXT,
                        PRIMARY KEY (player_id, kit_id)
                    )
                """);
            } else {
                statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS kits (
                        player_id TEXT NOT NULL,
                        kit_id INTEGER NOT NULL CHECK(kit_id BETWEEN 1 AND 9),
                        items TEXT,
                        PRIMARY KEY (player_id, kit_id)
                    )
                """);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<KitHolder> get(UUID id) {
        Map<Integer, Kit> kits = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement("""
            SELECT * FROM kits WHERE player_id = ?
        """)) {
            statement.setString(1, id.toString());
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int kitId = rs.getInt("kit_id");
                String itemsSerialized = rs.getString("items");

                Map<Integer, org.bukkit.inventory.ItemStack> items = SerializeUtils.deserializeItemStackMap(itemsSerialized);
                kits.put(kitId, new Kit(id, kitId, items));
            }

            for (int i = 1; i <= 9; i++) {
                kits.putIfAbsent(i, new Kit(id, i, new HashMap<>()));
            }

            return Optional.of(new KitHolder(id, kits));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void save(KitHolder holder) {
        for (int i = 1; i <= 9; i++) {
            Kit kit = holder.getList().get(i);
            if (kit == null) continue;

            String sql;
            if (isMySQL) {
                sql = """
                    INSERT INTO kits (player_id, kit_id, items)
                    VALUES (?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        items = VALUES(items)
                """;
            } else {
                sql = """
                    INSERT INTO kits (player_id, kit_id, items)
                    VALUES (?, ?, ?)
                    ON CONFLICT(player_id, kit_id) DO UPDATE SET
                        items = excluded.items
                """;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, kit.getOwner().toString());
                statement.setInt(2, kit.getID());
                statement.setString(3, SerializeUtils.serializeKitItems(kit.getContent()));
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
