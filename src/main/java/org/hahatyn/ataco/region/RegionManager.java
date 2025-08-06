package org.hahatyn.ataco.region;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hahatyn.ataco.utils.Cuboid;


public class RegionManager {

    private final RegionData regionData;

    public RegionManager(RegionData regionData) {
        this.regionData = regionData;
    }

    /**
     * Получить регион, в котором находится данная локация.
     */
    public Region getRegionAt(Location location) {
        for (Region region : regionData.getAllRegions().values()) {
            if (region.getCuboid().isIn(location)) {
                return region;
            }
        }
        return null;
    }

    /**
     * Проверка — игрок в чужом регионе?
     */
    public boolean isInForeignRegion(Player player) {
        Location loc = player.getLocation();
        Region region = getRegionAt(loc);
        if (region == null) return false;

        String playerName = player.getName();
        return !region.getOwnerName().equalsIgnoreCase(playerName)
                && !region.getMembers().contains(playerName);
    }

    /**
     * Можно ли взаимодействовать с миром (ставить/ломать/открывать)?
     */
    public boolean canInteract(Player player) {
        return !isInForeignRegion(player);
    }

    /**
     * Можно ли взаимодействовать с данной точкой?
     */
    public boolean canInteract(Player player, Location location) {
        Region region = getRegionAt(location);
        if (region == null) return true;

        String playerName = player.getName();
        return region.getOwnerName().equalsIgnoreCase(playerName)
                || region.getMembers().contains(playerName);
    }
    /**
     * Показать рамку границы (каждые 2 блока для оптимизации)
     */
    public void showRegionBorder(Player player, Cuboid cuboid) {
        Location min = cuboid.getPoint1();
        Location max = cuboid.getPoint2();
        World world = min.getWorld();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x += 2) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y += 2) {
                world.spawnParticle(Particle.HAPPY_VILLAGER, new Location(world, x, y, min.getBlockZ()), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.HAPPY_VILLAGER, new Location(world, x, y, max.getBlockZ()), 1, 0, 0, 0, 0);
            }
        }

        for (int z = min.getBlockZ(); z <= max.getBlockZ(); z += 2) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y += 2) {
                world.spawnParticle(Particle.HAPPY_VILLAGER, new Location(world, min.getBlockX(), y, z), 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.HAPPY_VILLAGER, new Location(world, max.getBlockX(), y, z), 1, 0, 0, 0, 0);
            }
        }
    }

    /**
     * Пересечение с чужими регионами запрещено
     */
    public boolean isIntersecting(Cuboid a, Cuboid b) {
        if (!a.getPoint1().getWorld().equals(b.getPoint1().getWorld())) return false;

        return a.getPoint1().getBlockX() <= b.getPoint2().getBlockX() && a.getPoint2().getBlockX() >= b.getPoint1().getBlockX()
                && a.getPoint1().getBlockY() <= b.getPoint2().getBlockY() && a.getPoint2().getBlockY() >= b.getPoint1().getBlockY()
                && a.getPoint1().getBlockZ() <= b.getPoint2().getBlockZ() && a.getPoint2().getBlockZ() >= b.getPoint1().getBlockZ();
    }

    /**
     * Проверка бумаги для создания региона
     */
    public boolean isCreateRegionPaper(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() &&
                ChatColor.stripColor(meta.getDisplayName()).equalsIgnoreCase("Создать регион");
    }

    /**
     * Добавить участника в регион
     */
    public boolean addMemberToRegion(String regionId, String playerName) {
        Region region = regionData.getRegionById(regionId);
        if (region == null) return false;

        boolean added = region.addMember(playerName);
        if (added) regionData.saveRegion(region);
        return added;
    }

    /**
     * Удалить участника из региона
     */
    public boolean removeMemberFromRegion(String regionId, String playerName) {
        Region region = regionData.getRegionById(regionId);
        if (region == null) return false;

        boolean removed = region.removeMember(playerName);
        if (removed) regionData.saveRegion(region);
        return removed;
    }
}
