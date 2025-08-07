package org.hahatyn.ataco.region;

import org.hahatyn.ataco.utils.Cuboid;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Region {
    private final String id;
    private final String owner;
    private final RegionType type;
    private final Set<String> members = ConcurrentHashMap.newKeySet();
    private final Cuboid cuboid;
    private volatile double health;
    private final double maxHealth;

    public Region(String id, String owner, RegionType type, Cuboid cuboid, double maxHealth) {
        this.id = id;
        this.owner = owner;
        this.type = type;
        this.cuboid = cuboid;
        this.health = maxHealth;
        this.maxHealth = maxHealth;
    }

    public String getId() { return id; }
    public String getOwner() { return owner; }
    public RegionType getType() { return type; }
    public Cuboid getCuboid() { return cuboid; }
    public double getHealth() { return health; }
    public double getMaxHealth() { return maxHealth; }

    public boolean addMember(String name) {
        if (name.equalsIgnoreCase(owner) || members.contains(name)) return false;
        return members.add(name);
    }

    public boolean removeMember(String name) {
        return members.remove(name);
    }

    public Set<String> getMembers() {
        return members;
    }

    public boolean isMember(String name) {
        return name.equalsIgnoreCase(owner) || members.contains(name);
    }

    public void damage(double amount) {
        health = Math.max(0, health - amount);
    }

    public void heal(double amount) {
        health = Math.min(maxHealth, health + amount);
    }
}