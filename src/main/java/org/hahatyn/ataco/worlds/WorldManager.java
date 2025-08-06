package org.hahatyn.ataco.worlds;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class WorldManager {

    private final Map<String, World> loadedWorlds = new HashMap<>();

    public World getOrLoadWorld(String name) {
        if (loadedWorlds.containsKey(name)) {
            return loadedWorlds.get(name);
        }

        WorldCreator creator = new WorldCreator(name);
        World world = Bukkit.createWorld(creator);
        if (world != null) {
            loadedWorlds.put(name, world);
            return world;
        }

        return null;
    }

    public void teleportToWorld(Player player, String worldName) {
        World world = getOrLoadWorld(worldName);
        if (world == null) {
            player.sendMessage("§cМир '" + worldName + "' не найден или не удалось загрузить.");
            return;
        }

        Location spawn = world.getSpawnLocation();
        player.teleport(spawn);
        player.sendMessage("§aВы были перемещены в мир §e" + worldName);
    }
}