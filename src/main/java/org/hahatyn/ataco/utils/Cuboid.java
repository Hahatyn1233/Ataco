package org.hahatyn.ataco.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Cuboid {

    private final int xMin, xMax, yMin, yMax, zMin, zMax;
    private final double xMinCentered, xMaxCentered, yMinCentered, yMaxCentered, zMinCentered, zMaxCentered;
    private final World world;

    // Кэшируем размеры
    private final int xWidth;
    private final int yHeight;
    private final int zWidth;

    // Кэшируем центр (точные координаты)
    private final Location center;

    // Один объект Random на весь класс
    private final Random random = new Random();

    // Конструктор по двум точкам
    public Cuboid(final Location point1, final Location point2) {
        this.world = point1.getWorld();

        this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        this.yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        this.yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());

        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;

        this.xWidth = this.xMax - this.xMin + 1;
        this.yHeight = this.yMax - this.yMin + 1;
        this.zWidth = this.zMax - this.zMin + 1;

        this.center = new Location(world,
                this.xMin + xWidth / 2.0,
                this.yMin + yHeight / 2.0,
                this.zMin + zWidth / 2.0);
    }

    // Конструктор с центром и радиусом (радиус распространяется во все стороны)
    public Cuboid(final Location center, final int radius) {
        this.world = center.getWorld();

        this.xMin = center.getBlockX() - radius;
        this.xMax = center.getBlockX() + radius;
        this.yMin = center.getBlockY() - radius;
        this.yMax = center.getBlockY() + radius;
        this.zMin = center.getBlockZ() - radius;
        this.zMax = center.getBlockZ() + radius;

        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;

        this.xWidth = this.xMax - this.xMin + 1;
        this.yHeight = this.yMax - this.yMin + 1;
        this.zWidth = this.zMax - this.zMin + 1;

        this.center = new Location(world,
                this.xMin + xWidth / 2.0,
                this.yMin + yHeight / 2.0,
                this.zMin + zWidth / 2.0);
    }

    // Ленивый итератор по блокам (не выделяет большой список)
    public Iterable<Block> blocks() {
        return () -> new Iterator<>() {
            private int x = xMin;
            private int y = yMin;
            private int z = zMin;

            @Override
            public boolean hasNext() {
                return x <= xMax;
            }

            @Override
            public Block next() {
                if (!hasNext()) throw new NoSuchElementException();

                Block block = world.getBlockAt(x, y, z);

                // Инкрементируем координаты
                if (++z > zMax) {
                    z = zMin;
                    if (++y > yMax) {
                        y = yMin;
                        x++;
                    }
                }
                return block;
            }
        };
    }

    public Location getCenter() {
        return center.clone();
    }

    public double getDistance() {
        return getPoint1().distance(getPoint2());
    }

    public double getDistanceSquared() {
        return getPoint1().distanceSquared(getPoint2());
    }

    public int getHeight() {
        return yHeight;
    }

    public Location getPoint1() {
        return new Location(world, xMin, yMin, zMin);
    }

    public Location getPoint2() {
        return new Location(world, xMax, yMax, zMax);
    }

    // Используем общий Random
    public Location getRandomLocation() {
        int x = random.nextInt(xWidth) + xMin;
        int y = random.nextInt(yHeight) + yMin;
        int z = random.nextInt(zWidth) + zMin;
        return new Location(world, x, y, z);
    }

    public int getTotalBlockSize() {
        return xWidth * yHeight * zWidth;
    }

    public int getXWidth() {
        return xWidth;
    }

    public int getZWidth() {
        return zWidth;
    }

    public boolean isIn(final Location loc) {
        if (loc.getWorld() != world) return false;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        return x >= xMin && x <= xMax && y >= yMin && y <= yMax && z >= zMin && z <= zMax;
    }

    public boolean isIn(final Player player) {
        return isIn(player.getLocation());
    }

    public boolean isInWithMarge(final Location loc, final double marge) {
        if (loc.getWorld() != world) return false;
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        return x >= xMinCentered - marge && x <= xMaxCentered + marge
                && y >= yMinCentered - marge && y <= yMaxCentered + marge
                && z >= zMinCentered - marge && z <= zMaxCentered + marge;
    }
}

