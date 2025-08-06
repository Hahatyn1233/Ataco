package org.hahatyn.ataco.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.*;

public class RegionData {

    private static final String DB_URL = "jdbc:sqlite:plugins/Ataco/regions.db";
    private final Map<String, Region> regionMap = new HashMap<>();

    public RegionData() {
        createTable();
        loadRegions();
    }

    private void createTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS regions (
                    id TEXT PRIMARY KEY,
                    owner TEXT,
                    type TEXT,
                    members TEXT,
                    world TEXT,
                    x DOUBLE,
                    y DOUBLE,
                    z DOUBLE,
                    health DOUBLE,
                    max_health DOUBLE
                );
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveRegion(Region region) {
        regionMap.put(region.getId(), region);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("""
                INSERT OR REPLACE INTO regions (id, owner, type, members, world, x, y, z, health, max_health)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """)) {

            stmt.setString(1, region.getId());
            stmt.setString(2, region.getOwnerName());
            stmt.setString(3, region.getRegionType().name());
            stmt.setString(4, String.join(";", region.getMembers()));
            stmt.setString(5, region.getCenterLocation().getWorld().getName());
            stmt.setDouble(6, region.getCenterLocation().getX());
            stmt.setDouble(7, region.getCenterLocation().getY());
            stmt.setDouble(8, region.getCenterLocation().getZ());
            stmt.setDouble(9, region.getHeath());
            stmt.setDouble(10, region.getMaxHealth());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRegion(String id) {
        regionMap.remove(id);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM regions WHERE id = ?")) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadRegions() {
        regionMap.clear();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM regions")) {

            while (rs.next()) {
                String id = rs.getString("id");
                String owner = rs.getString("owner");
                RegionType type = RegionType.valueOf(rs.getString("type"));
                List<String> members = Arrays.asList(rs.getString("members").split(";"));
                Location location = new Location(
                        Bukkit.getWorld(rs.getString("world")),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z")
                );
                double health = rs.getDouble("health");
                double maxHealth = rs.getDouble("max_health");

                Region region = new Region(id, owner, type, members, location, health, maxHealth);
                regionMap.put(id, region);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Region> getAllRegions() {
        return regionMap;
    }

    public Region getRegionById(String id) {
        return regionMap.get(id);
    }

    public String generateRegionId(String ownerName) {
        int count = (int) regionMap.keySet().stream()
                .filter(key -> key.endsWith("_" + ownerName))
                .count();

        return (count + 1) + "_" + ownerName;
    }
}
