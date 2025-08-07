package org.hahatyn.ataco.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class RegionManager {

    private final IRegion iRegion;

    public RegionManager(IRegion store) {
        this.iRegion = store;
    }

    public Region getRegionAt(Location loc) {
        long key = ((loc.getBlockX()>>4)<<32) | (loc.getBlockZ()>>4 & 0xffffffffL);
        Set<Region> bucket = ((RegionData) iRegion).getBucket(key);
        if (bucket == null) return null;
        return bucket.stream()
                .filter(r -> r.getCuboid().isIn(loc))
                .findFirst().orElse(null);
    }

    public boolean canInteract(Player p, Location loc) {
        Region r = getRegionAt(loc);
        return r == null || r.getOwner().equalsIgnoreCase(p.getName()) || r.isMember(p.getName());
    }
}