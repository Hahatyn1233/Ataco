package org.hahatyn.ataco.region;

import org.hahatyn.ataco.utils.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RegionData implements IRegion {
    private final Map<Long, Set<Region>> buckets = new ConcurrentHashMap<>();
    private final Map<String, Region> byId = new ConcurrentHashMap<>();
    private final String dbUrl;

    public RegionData(String dbUrl) {
        this.dbUrl = dbUrl;
        initDatabase();
        loadAll();
    }

    private void initDatabase() {
        try (Connection c = DriverManager.getConnection(dbUrl);
             Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS regions ("
                    + "id TEXT PRIMARY KEY, owner TEXT, type TEXT, members TEXT, world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, health DOUBLE, max_health DOUBLE);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private long bucketKey(Location loc) {
        int cx = loc.getBlockX() >> 4;
        int cz = loc.getBlockZ() >> 4;
        return (((long)cx) << 32) | (cz & 0xffffffffL);
    }

    private void indexRegion(Region r) {
        Cuboid c = r.getCuboid();
        for (int x = c.getPoint1().getBlockX() >> 4; x <= c.getPoint2().getBlockX() >> 4; x++) {
            for (int z = c.getPoint1().getBlockZ() >> 4; z <= c.getPoint2().getBlockZ() >> 4; z++) {
                long key = (((long)x) << 32) | (z & 0xffffffffL);
                buckets.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(r);
            }
        }
        byId.put(r.getId(), r);
    }

    private void deindexRegion(Region r) {
        Cuboid c = r.getCuboid();
        for (int x = c.getPoint1().getBlockX() >> 4; x <= c.getPoint2().getBlockX() >> 4; x++) {
            for (int z = c.getPoint1().getBlockZ() >> 4; z <= c.getPoint2().getBlockZ() >> 4; z++) {
                long key = (((long)x) << 32) | (z & 0xffffffffL);
                Set<Region> set = buckets.get(key);
                if (set != null) set.remove(r);
            }
        }
        byId.remove(r.getId());
    }

    @Override
    public void save(Region r) {

        try (Connection c = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = c.prepareStatement(
                     "INSERT OR REPLACE INTO regions VALUES(?,?,?,?,?,?,?,?,?,?);")) {
            ps.setString(1, r.getId());
            ps.setString(2, r.getOwner());
            ps.setString(3, r.getType().name());
            ps.setString(4, String.join(";", r.getMembers()));
            Location loc = r.getCuboid().getCenter();
            ps.setString(5, loc.getWorld().getName());
            ps.setDouble(6, loc.getX()); ps.setDouble(7, loc.getY()); ps.setDouble(8, loc.getZ());
            ps.setDouble(9, r.getHealth()); ps.setDouble(10, r.getMaxHealth());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        deindexRegion(r);
        indexRegion(r);
    }

    @Override
    public void delete(String id) {
        Region r = byId.get(id);
        if (r != null) {
            deindexRegion(r);
            try (Connection c = DriverManager.getConnection(dbUrl);
                 PreparedStatement ps = c.prepareStatement("DELETE FROM regions WHERE id=?")) {
                ps.setString(1, id);
                ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public Region findById(String id) {
        return byId.get(id);
    }

    @Override
    public Collection<Region> findAll() {
        return Collections.unmodifiableCollection(byId.values());
    }

    private void loadAll() {
        try (Connection c = DriverManager.getConnection(dbUrl);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM regions;")) {
            while (rs.next()) {
                Location loc = new Location(
                        Bukkit.getWorld(rs.getString("world")),
                        rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")
                );
                RegionType type = RegionType.valueOf(rs.getString("type"));
                Cuboid cub = new Cuboid(loc, type.getRadius());
                Region r = new Region(
                        rs.getString("id"), rs.getString("owner"), type, cub, rs.getDouble("max_health")
                );
                r.damage(r.getMaxHealth() - rs.getDouble("health"));
                indexRegion(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Set<Region> getBucket(long key) {
        return buckets.getOrDefault(key, Collections.emptySet());
    }

}