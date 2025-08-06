package org.hahatyn.ataco.region;

public enum RegionType {

    SMALL(5),
    MEDIUM(10),
    BIG(15),
    DEVELOPER(100);

    private final int size;

    RegionType(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
