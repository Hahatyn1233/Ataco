package org.hahatyn.ataco.region;

public enum RegionType {
    SMALL(5),
    MEDIUM(10),
    BIG(15),
    DEVELOPER(100);

    private final int radius;

    RegionType(int radius) { this.radius = radius; }

    public int getRadius() { return radius; }
}
