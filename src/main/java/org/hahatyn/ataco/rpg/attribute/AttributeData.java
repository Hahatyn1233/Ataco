package org.hahatyn.ataco.rpg.attribute;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AttributeData {

    private final Connection connection;

    public AttributeData(String dbFilePath) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        createTableIfNotExists();
    }

    private void createTableIfNotExists() throws SQLException {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS player_attributes (player_name TEXT PRIMARY KEY");
        for (AttributeType type : AttributeType.values()) {
            sql.append(", ").append(type.name()).append(" REAL DEFAULT 0");
        }
        sql.append(");");

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql.toString());
        }
    }

    /**
     * Собирает атрибуты с экипировки игрока (рука + броня), суммирует их и сохраняет в БД.
     */
    public void updatePlayerAttributes(Player player) {
        Map<AttributeType, Double> aggregatedAttributes = new EnumMap<>(AttributeType.class);

        // Обработать руку
        processItem(player.getInventory().getItemInMainHand(), aggregatedAttributes);

        // Обработать броню (голова, грудь, штаны, ботинки)
        for (ItemStack armorPiece : player.getInventory().getArmorContents()) {
            processItem(armorPiece, aggregatedAttributes);
        }

        // Сохраняем в БД
        try {
            saveAttributes(player.getName(), aggregatedAttributes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void processItem(ItemStack item, Map<AttributeType, Double> aggregatedAttributes) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return;

        List<String> lore = meta.getLore();
        if (lore == null) return;

        for (String line : lore) {
            for (AttributeType type : AttributeType.values()) {
                String prefix = type.getDisplayName();
                if (line.startsWith(prefix)) {
                    String[] parts = line.split(":");
                    if (parts.length < 2) continue;
                    String valuePart = parts[1].replaceAll("§.", "").trim(); // убрать цвет
                    valuePart = valuePart.replace("%", "").replace("+", "");

                    try {
                        double value = Double.parseDouble(valuePart);
                        aggregatedAttributes.put(type, aggregatedAttributes.getOrDefault(type, 0.0) + value);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
    }

    private void saveAttributes(String playerName, Map<AttributeType, Double> attributes) throws SQLException {
        // Формируем запрос с upsert (INSERT OR REPLACE)
        StringBuilder columns = new StringBuilder("player_name");
        StringBuilder placeholders = new StringBuilder("?");
        StringBuilder updates = new StringBuilder();

        for (AttributeType type : AttributeType.values()) {
            columns.append(", ").append(type.name());
            placeholders.append(", ?");
            if (updates.length() > 0) updates.append(", ");
            updates.append(type.name()).append("=excluded.").append(type.name());
        }

        String sql = "INSERT INTO player_attributes (" + columns + ") VALUES (" + placeholders + ") " +
                "ON CONFLICT(player_name) DO UPDATE SET " + updates;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            int index = 2;
            for (AttributeType type : AttributeType.values()) {
                double val = attributes.getOrDefault(type, 0.0);
                stmt.setDouble(index++, val);
            }
            stmt.executeUpdate();
        }
    }

    /**
     * Получить атрибуты игрока из БД
     */
    public Map<AttributeType, Double> getAttributes(String playerName) throws SQLException {
        Map<AttributeType, Double> result = new EnumMap<>(AttributeType.class);

        StringBuilder sql = new StringBuilder("SELECT ");
        for (AttributeType type : AttributeType.values()) {
            sql.append(type.name()).append(", ");
        }
        sql.setLength(sql.length() - 2); // убрать запятую и пробел
        sql.append(" FROM player_attributes WHERE player_name = ?");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                for (AttributeType type : AttributeType.values()) {
                    double val = rs.getDouble(type.name());
                    result.put(type, val);
                }
            }
        }
        return result;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public Map<AttributeType, Double> getAttributesOrEmpty(String playerName) {
        try {
            return getAttributes(playerName);
        } catch (SQLException e) {
            e.printStackTrace();
            return new EnumMap<>(AttributeType.class);
        }
    }
}
