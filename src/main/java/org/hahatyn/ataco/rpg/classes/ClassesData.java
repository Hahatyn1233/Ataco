package org.hahatyn.ataco.rpg.classes;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ClassesData {

    private static final String DB_URL = "jdbc:sqlite:plugins/Ataco/classes.db";

    // Кэш по никам (необязательно, можно убрать)
    private final Map<String, ClassesPlayerStats> cache = new HashMap<>();

    public ClassesData() {
        createTable();
    }

    private void createTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS classes (
                    name TEXT PRIMARY KEY,
                    class_type TEXT,
                    level INTEGER,
                    experience INTEGER,
                    max_experience INTEGER,
                    strength INTEGER,
                    agility INTEGER,
                    intelligence INTEGER,
                    stat_points INTEGER
                );
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initPlayer(String name, ClassesType type) {
        if (hasPlayer(name)) return;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO classes (
                    name, class_type, level, experience, max_experience,
                    strength, agility, intelligence, stat_points
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
            """)) {

            stmt.setString(1, name);
            stmt.setString(2, type.name());
            stmt.setInt(3, 1);      // level
            stmt.setInt(4, 0);      // exp
            stmt.setInt(5, 100);    // max exp
            stmt.setInt(6, 1);      // strength
            stmt.setInt(7, 1);      // agility
            stmt.setInt(8, 1);      // intelligence
            stmt.setInt(9, 0);      // stat points

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPlayer(String name) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM classes WHERE name = ?")) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ClassesPlayerStats getPlayerStats(String name) {
        if (cache.containsKey(name)) return cache.get(name);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM classes WHERE name = ?")) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ClassesPlayerStats stats = new ClassesPlayerStats(
                            name,
                            ClassesType.valueOf(rs.getString("class_type")),
                            rs.getInt("level"),
                            rs.getInt("experience"),
                            rs.getInt("max_experience"),
                            rs.getInt("strength"),
                            rs.getInt("agility"),
                            rs.getInt("intelligence"),
                            rs.getInt("stat_points")
                    );
                    cache.put(name, stats);
                    return stats;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void savePlayerStats(ClassesPlayerStats stats) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("""
                UPDATE classes SET
                    class_type = ?,
                    level = ?,
                    experience = ?,
                    max_experience = ?,
                    strength = ?,
                    agility = ?,
                    intelligence = ?,
                    stat_points = ?
                WHERE name = ?;
            """)) {

            stmt.setString(1, stats.getClassType().name());
            stmt.setInt(2, stats.getLevel());
            stmt.setInt(3, stats.getExperience());
            stmt.setInt(4, stats.getMaxExperience());
            stmt.setInt(5, stats.getStrength());
            stmt.setInt(6, stats.getAgility());
            stmt.setInt(7, stats.getIntelligence());
            stmt.setInt(8, stats.getStatPoints());
            stmt.setString(9, stats.getPlayerName());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}