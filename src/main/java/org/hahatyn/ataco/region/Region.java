package org.hahatyn.ataco.region;

import org.bukkit.Location;
import org.hahatyn.ataco.utils.Cuboid;

import java.util.ArrayList;
import java.util.List;

public class Region {

    private String id;
    private String ownerName;
    private RegionType regionType;
    private List<String> members;
    private Location centerLocation;
    private double heath;
    private double maxHealth;

    public Region(String id, String ownerName, RegionType regionType, List<String> members, Location centerLocation, double heath, double maxHealth) {
        this.id = id;
        this.ownerName = ownerName;
        this.regionType = regionType;
        this.members = new ArrayList<>(members);
        this.centerLocation = centerLocation;
        this.heath = heath;
        this.maxHealth = maxHealth;
    }

    public Cuboid getCuboid() {
        return new Cuboid(centerLocation, regionType.getSize());
    }

    public String getId() {
        return id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public List<String> getMembers() {
        return members;
    }

    public Location getCenterLocation() {
        return centerLocation;
    }

    public double getHeath() {
        return heath;
    }

    public double getMaxHealth() {
        return maxHealth;
    }
    public boolean addMember(String playerName) {
        if (playerName.equalsIgnoreCase(ownerName) || members.contains(playerName)) return false;
        members.add(playerName);
        return true;
    }

    public boolean removeMember(String playerName) {
        return members.remove(playerName);
    }

    public boolean isMember(String playerName) {
        return playerName.equalsIgnoreCase(ownerName) || members.contains(playerName);
    }
}
