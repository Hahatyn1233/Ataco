package org.hahatyn.ataco;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.hahatyn.ataco.rpg.attribute.AttributeData;
import org.hahatyn.ataco.rpg.attribute.AttributeListener;
import org.hahatyn.ataco.rpg.classes.ClassesData;
import org.hahatyn.ataco.rpg.items.CustomItemsUpdater;
import org.hahatyn.ataco.worlds.GoMapCommand;
import org.hahatyn.ataco.worlds.WorldManager;

public final class Ataco extends JavaPlugin {

    private static Ataco instance;
    private AttributeData attributeData;
    private ClassesData classesData;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        new CustomItemsUpdater(this);
        this.classesData = new ClassesData();
        this.attributeData = new AttributeData();
        Bukkit.getPluginManager().registerEvents(new AttributeListener(this, attributeData), this);
        getCommand("gomap").setExecutor(new GoMapCommand(new WorldManager()));
    }

    @Override
    public void onDisable() {

    }

    public static Ataco getInstance() {
        return instance;
    }
    public ClassesData getClassesData() {
        return classesData;
    }
    public AttributeData getAttributeData() {
        return attributeData;
    }

}
