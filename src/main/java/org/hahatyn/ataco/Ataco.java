package org.hahatyn.ataco;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.hahatyn.ataco.region.RegionData;
import org.hahatyn.ataco.region.RegionEvent;
import org.hahatyn.ataco.region.RegionManager;
import org.hahatyn.ataco.rpg.attribute.AttributeData;
import org.hahatyn.ataco.rpg.attribute.AttributeEvent;
import org.hahatyn.ataco.rpg.attribute.AttributeUpdateTask;
import org.hahatyn.ataco.rpg.classes.ClassesData;
import org.hahatyn.ataco.rpg.items.CustomItemsUpdater;
import org.hahatyn.ataco.worlds.GoMapCommand;
import org.hahatyn.ataco.worlds.WorldManager;

public final class Ataco extends JavaPlugin {

    private static Ataco instance;
    private RegionData regionData;
    private AttributeData attributeData;
    private ClassesData classesData;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        new CustomItemsUpdater(this);
        this.regionData = new RegionData();
        this.classesData = new ClassesData();
        this.attributeData = new AttributeData();
        new AttributeUpdateTask(this, attributeData).start();
        Bukkit.getPluginManager().registerEvents(new RegionEvent(new RegionManager(regionData)), this);
        Bukkit.getPluginManager().registerEvents(new AttributeEvent(this, attributeData), this);
        getCommand("gomap").setExecutor(new GoMapCommand(new WorldManager()));
    }

    @Override
    public void onDisable() {

    }

    public static Ataco getInstance() {
        return instance;
    }
    public RegionData getRegionData() {
        return regionData;
    }
    public ClassesData getClassesData() {
        return classesData;
    }
    public AttributeData getAttributeData() {
        return attributeData;
    }

}
